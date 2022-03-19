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