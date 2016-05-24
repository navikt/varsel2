package no.nav.varsel.jms.to;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

	private final static QName _JmsReply_QNAME = new QName("http://nav.no/varsel/jms/reply", "JmsReply");

	public ObjectFactory() {
	}

	public JmsReply createJmsReply() {
		return new JmsReply();
	}

	@XmlElementDecl(namespace = "http://nav.no/varsel/jms/reply", name = "JmsReply")
	public JAXBElement<JmsReply> createJmsReply(JmsReply value) {
		return new JAXBElement<JmsReply>(_JmsReply_QNAME, JmsReply.class, null, value);
	}

}
