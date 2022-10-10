FROM navikt/java:17

COPY app/target/app.jar /app/app.jar
COPY export-vault-secrets.sh /init-scripts/10-export-vault-secrets.sh
COPY varseli-java-opts.sh /init-scripts/20-varseli-java-opts.sh