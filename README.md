# Varsel2

Varsel2 les varsel-bestillingar frå MQ, hentar varselmal og opprettar beskjed på Ditt Nav, med mogelegheit for ekstern notifikasjon via sms og/eller e-post, gjennom brukarnotifikasjon. Meldingar som går ut av appen blir sendt via Kakfa.

Du finn meir informasjon om det funksjonelle på [Confluence-sidene for varsel-2 (Nav-internt)](https://confluence.adeo.no/display/BOA/Varsel-2).

## Funksjonalitet
- tvarsel001: handtering av bestilte servicemeldingar
- kvarsel001: mottak av statusmelding frå doknotifikasjon-2 og oppdatering av status på varslar i varsel-db

## Komme i gang

Kjør tester og bygg appen

```
mvn clean verify
```

---

## Henvendelser

Lag en issue i repository.

### For Nav-ansatte

Spørsmål om appen kan stilles på [#team_dokumentløsninger](https://nav-it.slack.com/archives/C6W9E5GPJ)

## Lisens

[MIT](LICENSE.md)
