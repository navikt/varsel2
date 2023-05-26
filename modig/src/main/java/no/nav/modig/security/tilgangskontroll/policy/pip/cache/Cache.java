package no.nav.modig.security.tilgangskontroll.policy.pip.cache;

public interface Cache<T, U> {

    void purge();

    int getSize();

    U get(T key, String cachename);

    void put(T key, U value, String cachename);

}
