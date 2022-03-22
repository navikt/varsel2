package no.nav.varsel.provider.ws.brukervarsel.support;

import static no.nav.modig.security.tilgangskontroll.policy.attributes.AttributeIds.ATTR_ACTION_ID;
import static no.nav.modig.security.tilgangskontroll.policy.attributes.AttributeIds.ATTR_RESOURCE_ID;
import static no.nav.varsel.xacml.attributeid.ActionAttributeIds.READ_OPERATION;
import static no.nav.varsel.xacml.attributeid.EnvironmentAttributeIds.ATTR_ENVIRONMENT_RECIEVER;
import static no.nav.varsel.xacml.attributeid.EnvironmentAttributeIds.INTERNAL;
import static no.nav.varsel.xacml.attributeid.ResourceAttributeIds.VARSELBESTILLING;

import no.nav.modig.security.tilgangskontroll.policy.pep.AccessControl;
import no.nav.modig.security.tilgangskontroll.policy.pep.AccessControlAttribute;
import no.nav.modig.security.tilgangskontroll.policy.pep.AttributeType;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.binding.HentVarselForBrukerUgyldigInput;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerRequest;
import no.nav.tjeneste.virksomhet.brukervarsel.v1.meldinger.HentVarselForBrukerResponse;
import no.nav.varsel.service.interfaces.BrukervarselService;
import no.nav.varsel.service.tvarsel005.to.HentVarselForBrukerResponseTo;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Provider for Tvarsel005 HentVarselForBruker
 *
 * @author Lars Aune
 */
public class BrukervarselV1Provider {

	@Autowired
	private HentVarselForBrukerRequestMapper hentVarselForBrukerRequestMapper;

	@Autowired
	private HentVarselForBrukerResponseMapper hentVarselForbrukerResponseMapper;

	@Autowired
	private BrukervarselService brukervarselV1Service;

	public void ping() {
		//ping
	}

	@AccessControl(attributes = {
			@AccessControlAttribute(name = ATTR_ACTION_ID, value = READ_OPERATION, type = AttributeType.ACTION),
			@AccessControlAttribute(name = ATTR_RESOURCE_ID, value = VARSELBESTILLING, type = AttributeType.RESOURCE),
			@AccessControlAttribute(name = ATTR_ENVIRONMENT_RECIEVER, value = INTERNAL, type = AttributeType.SUBJECT)
			// ATTR_ENVIRONMENT_RECIEVER burde vært laget med type=environment, men en bug i
			// modig-security gjør at "environment"-variabler aldri blir sendt
			// inn som en del av XACML-requesten. Se EnvironmentAttributeIds-klassen for mer info.
	})
	public HentVarselForBrukerResponse hentVarselForBruker(HentVarselForBrukerRequest hentVarselForBrukerRequest)
			throws HentVarselForBrukerUgyldigInput {
		HentVarselForBrukerResponseTo hentVarselForBrukerResponseTo =
				brukervarselV1Service.hentVarselForBruker(hentVarselForBrukerRequestMapper.map(hentVarselForBrukerRequest));
		return hentVarselForbrukerResponseMapper.map(hentVarselForBrukerResponseTo);
	}
}
