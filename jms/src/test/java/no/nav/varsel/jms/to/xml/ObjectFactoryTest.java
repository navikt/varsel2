package no.nav.varsel.jms.to.xml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import static no.nav.varsel.jms.to.xml.ObjectFactory.JMS_REPLY_QNAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ObjectFactoryTest {

	private final ObjectFactory objectFactory = new ObjectFactory();
	private JmsReply jmsReply;

	@BeforeEach
	public void setUp() throws Exception {
		jmsReply = objectFactory.createJmsReply();
		jmsReply.getParams().put("key", "val");
	}

	@Test
	public void shouldCreateJmsReplyJaxbElement() {
		JAXBElement<JmsReply> replyJAXBElement = objectFactory.createJmsReply(this.jmsReply);
		assertThat(replyJAXBElement.getValue(), is(jmsReply));
		assertThat(replyJAXBElement.getName(), is(JMS_REPLY_QNAME));

		assertThat(jmsReply.getParams().get("key"), is("val"));
	}

	@Test
	public void shouldHaveCorrectAnnotations() throws Exception {
		assertThat(ObjectFactory.class.getAnnotation(XmlRegistry.class), notNullValue());
		XmlElementDecl xmlElementDecl = ObjectFactory.class.getMethod("createJmsReply", JmsReply.class)
				.getAnnotation(XmlElementDecl.class);
		assertThat(xmlElementDecl, notNullValue());
		assertThat(xmlElementDecl.name(), is(JMS_REPLY_QNAME.getLocalPart()));
		assertThat(xmlElementDecl.namespace(), is(JMS_REPLY_QNAME.getNamespaceURI()));
	}
}