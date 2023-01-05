FROM ghcr.io/navikt/baseimages/temurin:17

COPY app/target/app.jar /app/app.jar
COPY export-vault-secrets.sh /init-scripts/10-export-vault-secrets.sh
COPY varsel-java-opts.sh /init-scripts/20-varsel-java-opts.sh