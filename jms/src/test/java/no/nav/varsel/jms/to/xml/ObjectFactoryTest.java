package no.nav.varsel.jms.to.xml;

import static no.nav.varsel.jms.to.xml.ObjectFactory._JmsReply_QNAME;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;

/**
 * Unit test for {@link ObjectFactory}
 *
 * @author Andreas Skomedal, Visma Consulting.
 */
public class ObjectFactoryTest {

	private ObjectFactory objectFactory = new ObjectFactory();
	private JmsReply jmsReply;

	@Before
	public void setUp() throws Exception {
		jmsReply = objectFactory.createJmsReply();
		jmsReply.getParams().put("key", "val");
	}

	@Test
	public void shouldCreateJmsReplyJaxbElement() throws Exception {
		JAXBElement<JmsReply> replyJAXBElement = objectFactory.createJmsReply(this.jmsReply);
		assertThat(replyJAXBElement.getValue(), is(jmsReply));
		assertThat(replyJAXBElement.getName(), is(_JmsReply_QNAME));

		assertThat(jmsReply.getParams().get("key"), is("val"));
	}

	@Test
	public void shouldHaveCorrectAnnotations() throws Exception {
		assertThat(ObjectFactory.class.getAnnotation(XmlRegistry.class), notNullValue());
		XmlElementDecl xmlElementDecl = ObjectFactory.class.getMethod("createJmsReply", JmsReply.class)
				.getAnnotation(XmlElementDecl.class);
		assertThat(xmlElementDecl, notNullValue());
		assertThat(xmlElementDecl.name(), is(_JmsReply_QNAME.getLocalPart()));
		assertThat(xmlElementDecl.namespace(), is(_JmsReply_QNAME.getNamespaceURI()));
	}
}