package no.nav.varsel.azure;

public interface TokenConsumer {
	TokenResponse getClientCredentialToken(String token);
}
