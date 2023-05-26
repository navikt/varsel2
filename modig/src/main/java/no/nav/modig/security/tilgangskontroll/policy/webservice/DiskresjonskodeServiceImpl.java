package no.nav.modig.security.tilgangskontroll.policy.webservice;

import no.nav.tjeneste.pip.diskresjonskode.Diskresjonskode;
import no.nav.tjeneste.pip.diskresjonskode.DiskresjonskodePortType;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeRequest;
import no.nav.tjeneste.pip.diskresjonskode.meldinger.HentDiskresjonskodeResponse;
import org.apache.commons.lang3.Validate;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ServiceLoader;

public class DiskresjonskodeServiceImpl implements DiskresjonskodeService {
    public String getDiskresjonskode(String fnr, String webserviceUrl) throws MalformedURLException {
        Validate.notBlank(webserviceUrl, "URL til ServiceGateway er ikke satt.");

        DiskresjonskodePortType port = createPort(webserviceUrl);
        HentDiskresjonskodeRequest request = createRequest(fnr);

        HentDiskresjonskodeResponse response = port.hentDiskresjonskode(request);
        return response.getDiskresjonskode();
    }

    private HentDiskresjonskodeRequest createRequest(String fnr) {
        HentDiskresjonskodeRequest request = new HentDiskresjonskodeRequest();
        request.setIdent(fnr);
        return request;
    }

    private DiskresjonskodePortType createPort(String webserviceUrl) throws MalformedURLException {
        URL url = Diskresjonskode.class.getClassLoader().getResource("wsdl/Diskresjonskode.wsdl");
        Diskresjonskode diskresjonskode = new Diskresjonskode(url, new QName("http://nav.no/tjeneste/pip/diskresjonskode/", "Diskresjonskode"));
        DiskresjonskodePortType portType = diskresjonskode.getDiskresjonskode();

        configure(portType, webserviceUrl);

        return portType;
    }

    private void configure(DiskresjonskodePortType portType, String webserviceUrl) {
        BindingProvider bindingProvider = (BindingProvider) portType;
        bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, webserviceUrl);

        ServiceLoader<WebServiceConfigurator> loader = ServiceLoader.load(WebServiceConfigurator.class);
        for (WebServiceConfigurator configurator : loader) {
            configurator.configure(bindingProvider);
        }
    }
}
