#!/usr/bin/env sh

JAVA_OPTS="${JAVA_OPTS} -Djakarta.net.ssl.keyStore=${VARSEL_CERT_KEYSTORE}"
JAVA_OPTS="${JAVA_OPTS} -Djakarta.net.ssl.keyStoreType=jks"
JAVA_OPTS="${JAVA_OPTS} -Xmx1536m -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=nais"

export JAVA_OPTS
