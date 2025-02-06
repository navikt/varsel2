package no.nav.varsel.tvarsel001.jms.xml;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
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
