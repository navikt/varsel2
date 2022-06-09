Varsel2
=====

Varsel2 les varsel-bestillingar frå MQ, hentar varselmal og bestiller sms/epost-utsending gjennom [doknotifikasjon-2](https://github.com/navikt/doknotifikasjon-2).
Det blir også oppretta varsel til Ditt NAV gjennom brukarnotifikasjon. Meldingar som går ut av appen blir sendt via Kakfa.

Du finn meir informasjon om det funksjonelle på [Confluence-sidene for varsel-2](https://confluence.adeo.no/display/BOA/Varsel-2).

## Funksjonalitet
- tvarsel001: handtering av bestilte servicemeldingar
- tvarsel006: handtering av bestilte servicemeldingar med kontaktinformasjon
- kvarsel001: mottak av statusmelding frå doknotifikasjon-2 og oppdatering av status på varslar i varsel-db