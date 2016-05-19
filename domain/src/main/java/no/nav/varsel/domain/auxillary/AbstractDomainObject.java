package no.nav.varsel.domain.auxillary;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;

/**
 * Base class for no.nav.varsel.domain objects.
 *
 * @author Andreas Skomedal, Visma Consulting
 */
@MappedSuperclass
@SuppressWarnings("serial")
public abstract class AbstractDomainObject implements Serializable {

	@Embedded
	private ChangeStamp changeStamp;

	@Version
	@Column(name = "versjon", nullable = false)
	private long version;

	public ChangeStamp getChangeStamp() {
		return changeStamp;
	}

	public void setChangeStamp(ChangeStamp changeStamp) {
		this.changeStamp = changeStamp;
	}

	public long getVersion() {
		return version;
	}

	protected void setVersion(long version) {
		this.version = version;
	}

}
