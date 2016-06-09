package no.nav.varsel.wsconsumer.dokkat;

import no.nav.dokkat.schemas.tkat021.VarselInfo;
import no.nav.varsel.wsconsumer.dokkat.support.VarselInfoMapper;
import no.nav.varsel.wsconsumer.dokkat.to.VarselInfoTo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

/**
 * VarselInfo Stub
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class VarselInfoConsumer implements InitializingBean {

	@Value("${dokkat.varselinfo.rest.url}")
	private String varselinfoUrl;
	@Inject
	private RestTemplate restTemplate;
	private String varselinfoUrlGet;

	@Inject
	private VarselInfoMapper varselInfoMapper;

	public VarselInfoTo hentVarselInfo(String varslingstype) {
		VarselInfo varselInfo = restTemplate.getForObject(varselinfoUrlGet, VarselInfo.class, varslingstype);
		return varselInfoMapper.map(varselInfo);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		varselinfoUrlGet = varselinfoUrl;
		if (!varselinfoUrlGet.endsWith("/")) {
			varselinfoUrlGet += "/";
		}
		varselinfoUrlGet += "{varslingstype}";
	}
}
