package no.nav.varsel.service;

/**
 * Service that can be used to check that the application is up and running.
 *
 * @author Lars Aune
 */
public interface PingService {

	/**
	 * Perform the ping, will do a simple database select to check that the database connection is working.
	 */
	void ping();
}
