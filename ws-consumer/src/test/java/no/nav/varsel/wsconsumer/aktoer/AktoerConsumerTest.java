package no.nav.varsel.wsconsumer.aktoer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import no.nav.tjeneste.virksomhet.aktoer.v2.binding.AktoerV2;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentAktoerIdForIdentResponse;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdRequest;
import no.nav.tjeneste.virksomhet.aktoer.v2.meldinger.HentIdentForAktoerIdResponse;
import no.nav.varsel.domain.to.AktoerTo;
import no.nav.varsel.domain.to.MottakerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit test for {@link AktoerConsumer}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
@RunWith(MockitoJUnitRunner.class)
public class AktoerConsumerTest {

	private static final String PERSON_ID = "id";
	private static final String AKTOER_ID = "aktoerId";

	@Mock
	private AktoerV2 aktoerV2Mock;
	@InjectMocks
	private AktoerConsumer aktoerConsumer;

	@Before
	public void setUp() throws Exception {
		HentAktoerIdForIdentResponse aktoerIdForIdentResponse = new HentAktoerIdForIdentResponse();
		aktoerIdForIdentResponse.setAktoerId(AKTOER_ID);
		when(aktoerV2Mock.hentAktoerIdForIdent(persId(PERSON_ID))).thenReturn(aktoerIdForIdentResponse);

		HentIdentForAktoerIdResponse identForAktoerIdResponse = new HentIdentForAktoerIdResponse();
		identForAktoerIdResponse.setIdent(PERSON_ID);
		when(aktoerV2Mock.hentIdentForAktoerId(aktId(AKTOER_ID))).thenReturn(identForAktoerIdResponse);
	}

	@Test
	public void shouldHentPersonIdent() throws Exception {
		AktoerTo aktoerTo = aktoerConsumer.hentIdent(AktoerTo.newAktoerId(AKTOER_ID));
		assertThat(aktoerTo.getMottakerType(), is(MottakerType.PERSON));
		assertThat(aktoerTo.getIdent(), is(PERSON_ID));
	}

	@Test
	public void shouldHentAktoerId() throws Exception {
		AktoerTo aktoerTo = aktoerConsumer.hentIdent(AktoerTo.newPersonIdent(PERSON_ID));
		assertThat(aktoerTo.getMottakerType(), is(MottakerType.AKTOER));
		assertThat(aktoerTo.getIdent(), is(AKTOER_ID));
	}

	private HentAktoerIdForIdentRequest persId(String ident) {
		HentAktoerIdForIdentRequest request = new HentAktoerIdForIdentRequest() {
			@Override
			public boolean equals(Object obj) {
				return obj instanceof HentAktoerIdForIdentRequest &&
						this.getIdent().equals(((HentAktoerIdForIdentRequest) obj).getIdent());
			}
		};
		request.setIdent(ident);
		return eq(request);
	}

	private HentIdentForAktoerIdRequest aktId(String ident) {
		HentIdentForAktoerIdRequest request = new HentIdentForAktoerIdRequest() {
			@Override
			public boolean equals(Object obj) {
				return obj instanceof HentIdentForAktoerIdRequest &&
						this.getAktoerId().equals(((HentIdentForAktoerIdRequest) obj).getAktoerId());
			}
		};
		request.setAktoerId(ident);
		return eq(request);
	}

}