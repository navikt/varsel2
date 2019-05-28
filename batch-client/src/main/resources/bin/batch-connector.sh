#!/bin/sh

BATCH_CONNECTOR="./modig-batch-connector-with-dependencies.jar";

if [ -f $BATCH_CONNECTOR ]
then
        keystore***passord=gammelt_passord***)
        truststore***passord=gammelt_passord***)
        java -jar -Dlogback.configurationFile=file:logback.xml -Djavax.net.ssl.keyStore=/opt/jboss/etc/keystore.jks -Djavax.net.ssl.keyStore***passord=gammelt_passord*** "$@";
        exit $?;
else
        echo "Could not locate jar file.";
        exit 1;
fi
