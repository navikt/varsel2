#!/bin/sh

BATCH_CONNECTOR="./modig-batch-connector-with-dependencies.jar";

if [ -f $BATCH_CONNECTOR ]
then
        keystorepassword=$(grep -Po "(?<=^keystorepassword=).*" /opt/jboss/etc/keystorecredentials.properties)
        truststorepassword=$(grep -Po "(?<=^truststorepassword=).*" /opt/jboss/etc/keystorecredentials.properties)
        java -jar -Dlogback.configurationFile=file:logback.xml -Djavax.net.ssl.keyStore=/opt/jboss/etc/keystore.jks -Djavax.net.ssl.keyStorePassword=$keystorepassword -Djavax.net.ssl.trustStore=/opt/jboss/etc/truststore.jts -Djavax.net.ssl.trustStorePassword=$truststorepassword $BATCH_CONNECTOR "$@";
        exit $?;
else
        echo "Could not locate jar file.";
        exit 1;
fi
