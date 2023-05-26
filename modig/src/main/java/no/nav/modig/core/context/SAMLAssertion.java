package no.nav.modig.core.context;

import org.w3c.dom.Element;

public class SAMLAssertion implements Attribute {
    private static final long serialVersionUID = 1L;

    private Element element;

    public Element getElement() {
        return element;
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
