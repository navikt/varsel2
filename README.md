Varsel
=====

[Confluence](https://confluence.adeo.no/display/BOA/Varsel) for inforamsjon av applikasjonen.

Local Server
------------
    1. VM property: -Dspring.profiles.active=local
    2. [system properties](https://fasit.adeo.no/search/varsel?type=resource)
    3. Deploy war to a tomcat server

BVARSEL001
------------
Hvordan starte og stopp batch jobb bvarsel001
```bash
#Start av batch:

sh ./batch/varsel/bin/batch-connector.sh -start  \
-baseurl https://<ingress>:8443/varsel/batch \
-jobname BVARSEL001 -jobparameters \
"startTime='`date +%d.%m.%Y-%H:%M:%S`',\
workUnit=100"

#Stopp av batch:

sh /data/batch/varsel/bin/batch-connector.sh -stop -baseurl https://<ingress>:8443/varsel/batch -jobname BVARSEL001
```

For mer info [confluence-bvarsel001](https://confluence.adeo.no/display/BOA/Varsel+-+BVARSEL001)