package no.nav.varsel.jms.to.xml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

	static final QName JMS_REPLY_QNAME = new QName("http://nav.no/varsel/jms/reply", "JmsReply");

	public JmsReply createJmsReply() {
		return new JmsReply();
	}

	@XmlElementDecl(namespace = "http://nav.no/varsel/jms/reply", name = "JmsReply")
	public JAXBElement<JmsReply> createJmsReply(JmsReply value) {
		return new JAXBElement<>(JMS_REPLY_QNAME, JmsReply.class, value);
	}

}
