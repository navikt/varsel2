package no.nav.varsel.tvarsel001.service.service;

import no.nav.varsel.consumer.dkif.HentDigitalKontaktinformasjonConsumer;
import no.nav.varsel.consumer.dkif.to.KontaktregisterTo;
import no.nav.varsel.consumer.dokmet.DokmetConsumer;
import no.nav.varsel.consumer.dokmet.Varselinfo;
import no.nav.varsel.consumer.support.VarselKanalDecider;
import no.nav.varsel.domain.code.KanalCode;
import no.nav.varsel.domain.object.Varselbestilling;
import no.nav.varsel.repo.VarselbestillingRepo;
import no.nav.varsel.tvarsel001.service.service.support.BrukernotifikasjonMapper;
import no.nav.varsel.tvarsel001.service.service.support.VarselBestillingDomainMapper;
import no.nav.varsel.exception.functional.ServicemeldingMappingException;
import no.nav.varsel.exception.functional.VarselInaktivVarselmalException;
import no.nav.varsel.exception.functional.VarselbestillingUtloeptException;
import no.nav.varsel.tvarsel001.service.service.to.BestillVarselTo;
import no.nav.varsel.tvarsel001.BrukernotifikasjonBeskjedPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static no.nav.varsel.domain.code.KanalCode.DITT_NAV;

@Component
public class ServicemeldingService {

	private static final Logger log = LoggerFactory.getLogger(ServicemeldingService.class);

	private final AktoerService aktoerService;
	private final DokmetConsumer dokmetConsumer;
	private final HentDigitalKontaktinformasjonConsumer dkifConsumer;
	private final VarselKanalDecider varselKanalDecider;
	private final VarselBestillingDomainMapper domainMapper;
	private final VarselbestillingRepo varselbestillingRepo;
	private final BrukernotifikasjonBeskjedPublisher brukernotifikasjonBeskjedPublisher;

	public ServicemeldingService(AktoerService aktoerService, DokmetConsumer dokmetConsumer, HentDigitalKontaktinformasjonConsumer dkifConsumer, VarselKanalDecider varselKanalDecider, VarselBestillingDomainMapper domainMapper, VarselbestillingRepo varselbestillingRepo, BrukernotifikasjonBeskjedPublisher brukernotifikasjonBeskjedPublisher) {
		this.aktoerService = aktoerService;
		this.dokmetConsumer = dokmetConsumer;
		this.dkifConsumer = dkifConsumer;
		this.varselKanalDecider = varselKanalDecider;
		this.domainMapper = domainMapper;
		this.varselbestillingRepo = varselbestillingRepo;
		this.brukernotifikasjonBeskjedPublisher = brukernotifikasjonBeskjedPublisher;
	}

	public void bestillServicemelding(BestillVarselTo bestilling) {
		if (bestilling.getUtloepstidspunkt() != null && bestilling.getUtloepstidspunkt().isBefore(LocalDateTime.now())) {
			throw new VarselbestillingUtloeptException(bestilling.getVarselBestillingId(), bestilling.getUtloepstidspunkt());
		}

		//1.1 Duplikatkontroll
		if (bestilling.getVarselBestillingId() != null && varselbestillingRepo.existsByVarselbestillingId(bestilling.getVarselBestillingId())) {
			log.info("Varselbestilling med bestillingId={} finnes allerede. Sender ikke nytt varsel.", bestilling.getVarselBestillingId());
			return;
		}

		bestilling.setVarselBestillingId(getVarselbestillingId(bestilling));

		//2.Hent Aktørid for Ident
		bestilling.setMottaker(aktoerService.findMissingAktoer(bestilling));

		//3.Hent Varselinfo
		Varselinfo varselinfo = dokmetConsumer.hentVarselinfo(bestilling.getVarseltypeId());
		validateVarselinfoForBestilling(bestilling, varselinfo);

		if (bestilling.isTestvarsel()) {
			varselinfo = varselinfo.withPreferertKanal(Set.of(KanalCode.values()));
		}

		KontaktregisterTo kontaktregisterTo = dkifConsumer.hentDigitalKontaktinformasjon(bestilling.getPersonIdent());

		//4.Bestem varslingskanal
		Set<KanalCode> kanaler = varselKanalDecider.decideKanaler(kontaktregisterTo, varselinfo.getPreferertKanal());
		kontaktregisterTo.setKanaler(kanaler);

		//5. Flett varsel
		Varselbestilling varselbestilling = domainMapper.mapVarselbestilling(bestilling, varselinfo, kontaktregisterTo);

		//6. Register varsel i DB
		varselbestillingRepo.saveAndFlush(varselbestilling);

		try {
			sendBrukernotifikasjon(varselbestilling);
		} catch (ServicemeldingMappingException e) {
			log.error("Feil ved mapping av data til servicemelding med BestillingId={}. Feilmelding={}", bestilling.getVarselBestillingId(), e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("Ukjent feil ved sending av servicemelding med BestillingId={}. Feilmelding={}", bestilling.getVarselBestillingId(), e.getMessage());
			throw e;
		}
	}

	private void sendBrukernotifikasjon(Varselbestilling varselbestilling) {
		if (varselbestilling.getVarsels().stream().noneMatch(it -> DITT_NAV.equals(it.getKanal()))) {
			log.info("Varsel med bestillingsId={} og varseltypeId={} mangler kanal=DITT_NAV. Oppretter ikke beskjed gjennom brukernotifikasjon.",
					varselbestilling == null ? null : varselbestilling.getVarselbestillingId(),
					varselbestilling == null ? null : varselbestilling.getVarseltypeId());
			return;
		}

		String opprettVarselJson = BrukernotifikasjonMapper.mapAndMarshalOpprettVarsel(varselbestilling);

		log.info("Sender brukernotifikasjon med bestillingId={}, varseltypeId={} til kanal(er)={}",
				varselbestilling.getVarselbestillingId(),
				varselbestilling.getVarseltypeId(),
				varselbestilling.getVarsels().stream().map(it -> it.getKanal().name()).toList());

		brukernotifikasjonBeskjedPublisher.sendNotifikasjon(varselbestilling.getVarselbestillingId(), opprettVarselJson);
	}

	private void validateVarselinfoForBestilling(BestillVarselTo to, Varselinfo varselinfo) {
		if (varselinfo.isInaktiv() && !to.isTestvarsel()) {
			throw new VarselInaktivVarselmalException(to.getPersonIdent(), to.getVarseltypeId(), to.getVarselBestillingId());
		}
	}

	private String getVarselbestillingId(BestillVarselTo bestilling) {
		if (bestilling.getVarselBestillingId() == null) {
			return UUID.randomUUID().toString();
		}
		return bestilling.getVarselBestillingId();
	}
}
