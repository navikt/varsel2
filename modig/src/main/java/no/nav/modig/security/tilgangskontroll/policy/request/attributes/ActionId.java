package no.nav.modig.security.tilgangskontroll.policy.request.attributes;

import no.nav.modig.security.tilgangskontroll.policy.attributes.AttributeIds;
import no.nav.modig.security.tilgangskontroll.policy.attributes.values.StringValue;

/**
 * Action-attributt
 */
public class ActionId extends ActionAttribute {

    public ActionId(StringValue attributeValue) {
        super(AttributeIds.ACTION_ID, attributeValue);
    }
}
