package no.nav.varsel.wsconsumer.dokkat;

import no.nav.dokkat.schemas.tkat021.VarselInfoRestTo;
import no.nav.varsel.wsconsumer.dokkat.support.VarselInfoMapper;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * VarselInfo Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoConsumer {

	@Inject
	private RestTemplate restTemplate;
	private String varselinfoUrlGet;

	@Inject
	private VarselInfoMapper varselInfoMapper;

	public VarselInfoTo hentVarselInfo(String varslingstype) {
		VarselInfoRestTo varselInfo = restTemplate.getForObject(varselinfoUrlGet, VarselInfoRestTo.class, varslingstype);
		return varselInfoMapper.map(varselInfo);
	}

	@Inject
	public void setVarselinfoUrl(@Value("${dokkat.varselinfo.rest.url}") String varselinfoUrl) {
		varselinfoUrlGet = varselinfoUrl;
		if (!varselinfoUrlGet.endsWith("/")) {
			varselinfoUrlGet += "/";
		}
		varselinfoUrlGet += "{varslingstype}";
	}

	public void ping() {
		String ping = restTemplate.getForObject(varselinfoUrlGet, String.class, "ping");
		Assert.isTrue("ok".equals(ping), "VarselInfo ping failed " + ping);
	}
}
