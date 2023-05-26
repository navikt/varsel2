package no.nav.modig.security.tilgangskontroll.policy.webservice;

import javax.xml.ws.BindingProvider;

/**
 * SPI for configuring webservices.
 *
 * Used by PIP locators to delegate configuration of WS clients,
 * i.e. to add WS-Policies and properties to the endpoint.
 *
 * Implement this interface to provide configuration of the PIP locators ws-clients.
 * A provider-configuration file must be added to the resource directory META-INF/services.
 * The file-name must be set to the fully qualified name of this interface.
 * The file contains a list of concrete provider classes.
 */
public interface WebServiceConfigurator {
    void configure(BindingProvider portType);
}
