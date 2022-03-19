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

then
    echo "Setting varsel_serviceuser_username"
    export VARSEL_SERVICEUSER_USERNAME=$(cat /secrets/serviceuser/srvvarsel/username)
fi
if test -f /secrets/serviceuser/srvsafselvbetjening/password;
then
    echo "Setting varsel_serviceuser_password"
    export VARSEL_SERVICEUSER_PASSWORD=$(cat /secrets/serviceuser/srvvarsel/password)
fi