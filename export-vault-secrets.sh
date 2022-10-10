#!/usr/bin/env sh

if test -f /var/run/secrets/nais.io/varselDS/username;
then
    echo "Setting SPRING_DATASOURCE_USERNAME"
    export  SPRING_DATASOURCE_USERNAME=$(cat /var/run/secrets/nais.io/varselDS/username)
fi

if test -f /var/run/secrets/nais.io/varselDS/password;
then
    echo "Setting SPRING_DATASOURCE_PASSWORD"
    export  SPRING_DATASOURCE_PASSWORD=$(cat /var/run/secrets/nais.io/varselDS/password)
fi

if test -f /secrets/serviceuser/srvvarsel/username;
then
    echo "Setting varsel_serviceuser_username"
    export VARSEL_SERVICEUSER_USERNAME=$(cat /secrets/serviceuser/srvvarsel/username)
fi

if test -f /secrets/serviceuser/srvvarsel/password;
then
    echo "Setting varsel_serviceuser_password"
    export VARSEL_SERVICEUSER_PASSWORD=$(cat /secrets/serviceuser/srvvarsel/password)
fi
if test -f /var/run/secrets/nais.io/certificate/keystore
then
    echo "Setting VARSEL_CERT_KEYSTORE"
    CERT_PATH='/var/run/secrets/nais.io/certificate/keystore-extracted'
    openssl base64 -d -A -in /var/run/secrets/nais.io/certificate/keystore -out $CERT_PATH
    export VARSEL_CERT_KEYSTORE=$CERT_PATH
fi

if test -f /var/run/secrets/nais.io/certificate/keystorepassword
then
    echo "Setting VARSEL_CERT_KEYSTORE_PASSWORD"
    export VARSEL_CERT_KEYSTORE_PASSWORD=$(cat /var/run/secrets/nais.io/certificate/keystorepassword)
fi