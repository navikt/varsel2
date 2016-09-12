package no.nav.varsel.jms.consumer;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import no.nav.melding.virksomhet.varsel.v1.varsel.Varsel;
import org.junit.Test;
import org.mockito.Mockito;

import javax.jms.Message;

/**
 * Unittest for ObjectMessageWrapper
 *
 * @author Lars Aune
 */
public class ObjectMessageWrapperTest {
	private Message messageMock = Mockito.mock(Message.class);
	private Varsel varsel = new Varsel();

	@Test(expected = IllegalArgumentException.class)
	public void constructorRequiresUnmarshalObject() {
		new ObjectMessageWrapper<Varsel>(null, messageMock);
	}

	@Test(expected = IllegalArgumentException.class)
	public void constructorRequiresMessage() {
		new ObjectMessageWrapper<>(varsel, null);
	}

	@Test
	public void holdsObject() {
		ObjectMessageWrapper<Varsel> wrappedWithMessage = new ObjectMessageWrapper<>(varsel, messageMock);
		assertThat(wrappedWithMessage.getObject(), is(varsel));
	}

	@Test
	public void holdsMessage() {
		ObjectMessageWrapper<Varsel> wrappedWithMessage = new ObjectMessageWrapper<>(varsel, messageMock);
		assertThat(wrappedWithMessage.getMessage(), is(messageMock));
	}
}