package no.nav.varsel.jms.to.xml;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement
@XmlType(name = "JmsReply")
public class JmsReply implements Serializable {

	@Serial
	private static final long serialVersionUID = 5559790053976092330L;

	private Map<String, String> params = new HashMap<>();

	public JmsReply() {
	}

	public JmsReply(String result) {
		params.put("result", result);
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public boolean isOk() {
		return params.get("result").equals("ok");
	}

}
