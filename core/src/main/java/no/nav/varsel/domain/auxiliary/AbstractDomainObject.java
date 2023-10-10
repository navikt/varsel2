package no.nav.varsel.domain.auxiliary;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;

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
