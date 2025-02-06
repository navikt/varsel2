package no.nav.varsel.tvarsel001.jms.consumer;

import no.nav.melding.virksomhet.varsel.v1.varsel.XMLVarsel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.jms.Message;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectMessageWrapperTest {

	private final Message messageMock = Mockito.mock(Message.class);
	private final XMLVarsel varsel = new XMLVarsel();

	@Test
	public void constructorRequiresUnmarshalObject() {
		assertThrows(IllegalArgumentException.class, () -> new ObjectMessageWrapper<XMLVarsel>(null, messageMock));
	}

	@Test
	public void constructorRequiresMessage() {
		assertThrows(IllegalArgumentException.class, () -> new ObjectMessageWrapper<>(varsel, null));
	}

	@Test
	public void holdsObject() {
		ObjectMessageWrapper<XMLVarsel> wrappedWithMessage = new ObjectMessageWrapper<>(varsel, messageMock);
		assertThat(wrappedWithMessage.getObject(), is(varsel));
	}

	@Test
	public void holdsMessage() {
		ObjectMessageWrapper<XMLVarsel> wrappedWithMessage = new ObjectMessageWrapper<>(varsel, messageMock);
		assertThat(wrappedWithMessage.getMessage(), is(messageMock));
	}
}