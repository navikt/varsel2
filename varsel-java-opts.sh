#!/usr/bin/env sh

JAVA_OPTS="${JAVA_OPTS} -Djavax.net.ssl.keyStore=${VARSEL_CERT_KEYSTORE}"
JAVA_OPTS="${JAVA_OPTS} -Djavax.net.ssl.keyStoreType=jks"
JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=60 -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=nais"

export JAVA_OPTS
