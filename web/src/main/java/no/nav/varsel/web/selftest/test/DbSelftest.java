package no.nav.varsel.web.selftest.test;

import no.nav.varsel.web.selftest.support.AbstractSelftest;

import javax.inject.Inject;

/**
 * Selftest for Db
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class DbSelftest extends AbstractSelftest {
//
//	@Inject
//	private DokumentCacheRepo dokumentCacheRepo;

	public DbSelftest() {
		super("varselDS", "Varsel Oracle Database");
	}

	@Override
	protected void doCheck() {
		//TODO;
	}
}
