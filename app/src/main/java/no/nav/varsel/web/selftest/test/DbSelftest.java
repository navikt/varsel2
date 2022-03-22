package no.nav.varsel.web.selftest.test;

import no.nav.varsel.domain.to.Ping;
import no.nav.varsel.repo.VarselRepo;
import no.nav.varsel.web.selftest.support.AbstractSelftest;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Selftest for Db
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class DbSelftest extends AbstractSelftest {

	@Autowired
	private VarselRepo varselRepo;

	public DbSelftest() {
		super(Ping.Type.Datasource, "varselDS", "Varsel Oracle Database");
	}

	@Override
	protected void doCheck() {
		varselRepo.ping();
	}
}
