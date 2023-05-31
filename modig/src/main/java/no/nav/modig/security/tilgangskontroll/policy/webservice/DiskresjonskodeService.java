package no.nav.modig.security.tilgangskontroll.policy.webservice;

import java.net.MalformedURLException;

public interface DiskresjonskodeService {
    String getDiskresjonskode(String fnr, String webserviceUrl) throws MalformedURLException;
}