package no.nav.modig.core.context;

/**
 * ONLY FOR USE WITH WEBLOGIC
 */
public final class WlsPrincipal implements Attribute {
    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String identType;
    private final String authenticationLevel;
    private final String consumerId;

    private WlsPrincipal(Builder builder) {
        this.userId = builder.userId;
        this.identType = builder.identType;
        this.authenticationLevel = builder.authenticationLevel;
        this.consumerId = builder.consumerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getIdentType() {
        return identType;
    }

    public String getAuthenticationLevel() {
        return authenticationLevel;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public static class Builder {

        private String userId;
        private String identType;
        private String authenticationLevel;
        private String consumerId;

        public Builder userId(String value) {
            userId = value;
            return this;
        }

        public Builder identType(String value) {
            identType = value;
            return this;
        }

        public Builder authenticationLevel(String value) {
            authenticationLevel = value;
            return this;
        }

        public Builder consumerId(String value) {
            consumerId = value;
            return this;
        }

        public WlsPrincipal build() {
            if (userId == null || identType == null || authenticationLevel == null || consumerId == null) {
                throw new RuntimeException("Not all required attributes were set.");
            }
            return new WlsPrincipal(this);
        }
    }
}
