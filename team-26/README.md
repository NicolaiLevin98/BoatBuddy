
# BoatBuddy

En kort beskrivelse av appfunksjonaliteten går her. Denne appen krever at GPS-lokasjonstjenesten er satt til et sted innenfor Norge og at enheten som kjører Android har API-nivå 26 eller høyere.

## Kom i gang

Disse instruksjonene vil få en kopi av prosjektet oppe og kjører på din lokale maskin.

### Forutsetninger

- Android Studio
- Java Development Kit (JDK)
- Android SDK
- En Android-emulator eller en fysisk Android-enhet som kjører API 26 eller høyere

[Du kan laste ned Android Studio her][1], som inkluderer JDK, Android SDK og emulator:

### [Installasjon][2]

Følg disse trinnene for å sette opp miljøet:

1. **Pakk ut prosjektet**
   - Last ned ZIP-filen som inneholder appens kildekode.
   - Pakk ut innholdet i ZIP-filen til en mappe på datamaskinen din.

2. **Åpne Android Studio**
   - Hvis du ikke har Android Studio, vennligst last ned og installer fra linken ovenfor.

3. **Importer prosjekt**
   - Åpne Android Studio.
   - Klikk på `File` > `New` > `Import Project...`.
   - Naviger til mappen der du pakket ut ZIP-filen.
   - Velg prosjektmappe og klikk `OK`.

4. **Synkroniser prosjektet med Gradle-filer**
   - Android Studio starter automatisk synkroniseringen av prosjektet med de nødvendige Gradle-filene.
   - Vent til prosessen er fullført. Dette kan ta noen minutter.

5. **[Konfigurer en emulator][3] eller [koble til en fysisk enhet][4]**
   - Hvis du bruker en emulator, gå til `Tools` > `AVD Manager` og opprett en ny Android Virtual Device som sikrer at den kjører API-nivå 26 eller høyere.
   - Hvis du bruker en fysisk enhet, sørg for at den kjører Android API-nivå 26 eller høyere, aktiver USB-feilsøking på enheten din og koble den til datamaskinen din via USB.

### [Konfigurere GPS-lokasjon][5]

Før du kjører appen, sørg for å sette GPS-lokasjonen til et sted innenfor Norge:

1. **Stille inn lokasjon på emulator**
   - Start emulatoren din.
   - Klikk på flere alternativer (`...`)-knappen i emulatorens kontrollpanel.
   - Gå til `Location`-fanen.
   - Skriv inn koordinatene, eller søk etter en lokasjon i Norge.
   - Klikk på `Set Location` for å anvende endringene.

[1]: https://developer.android.com/studio
[2]: https://developer.android.com/codelabs/basic-android-kotlin-compose-install-android-studio#0
[3]: https://developer.android.com/studio/run/managing-avds
[4]: https://developer.android.com/studio/run/emulator-extended-controls
[5]: https://developer.android.com/studio/run/device

### Våre flotte medlemmer:
- Albert
- Thomas
- Maria
- Nicolai
- Aleksandra
- Elise

# Hvor finner jeg x?

### Modeller:

Modeller og diagrammer for prosjektets hovedflyt kan finnes i MODELING.md-filen.

### Arkitektur:

Informasjon om appens artikektur, relevante valg tatt og deres konsekvenser, samt refleksjon, finnes i ARCHITECTURE.md.

# Eksterne biblioteker:

### Kotlinx Serialization

Kotlinx Serialization bidrar til serialisering og deserialisering av JSON-responser fra API; brukes til MetAlerts, OceanForecast og LocationForecast API-ene

### Jackson

Jackson står for serialisering og deserialisering av XML, som er tatt i bruk for å deserialisere havnivå-data fra kartverkets se havnivå-API.

### Java Stream

Java Stream-biblioteket brukes for å hente java-filer som trengs til Jackson deserialisering. Noen av filene som trengs er ikke med i standardbiblioteket, og må eksplisitt importeres.

### Ktor

Ktor er ansvarlig for å opprette en HTTP-klient som kan sende dataforespørsler til API-ene, samt motta svarene.

### Jetpack Compose

Jetpack Compose brukes som byggeklossene til UI-et i appen, og er et direkte alternativ til XML.

### AndroidX Lifecycle

Lifecycle-bilbioteket er brukt for å holde på og ivareta deler av tilstanden til appen til tross for endringer i appens aktivitetstilstand.

### Google Play Services Location

Lar oss bruke lokasjonen til telefonen

### MOCKK

Mockk bibilioteket er brukt for å simulere responser fra kotlin klasser. Dette er veldig viktig for unit testing siden det gjør at vi f.eks kan teste en datasource uten å være avhengig av et api.

### JUNIT

Junit er et test framework som lar oss skrive og kjøre automatiserte unit tester. Dette sikkrer at funksjonene og klassene våre fungerer slik som forventet.

### Google truth

Google truth er et tesing bibliotek, med mer lesbare asserts enn det som kommer med junit.

### OSMDroid

OSMDroid-biblioteket supplerer et API som lar oss legge inn et interaktivt kart i appen. Dette kartet inneholder både OpenStreetMap og OpenSeaMap, hvor den sistnevnte inneholder svært relevante ikoner for sjøfaring.

### Datastore

Datastore lar oss lagre informasjon permanent i key value pairs. Vi bruker dette til å lagre hvilket tema brukeren har valgt.

# GIT-REGLER:

### 1. Alltid pull fra main:
- Før du begynner å kode noe som helst, **MÅ** du kjøre en 'Git pull' (Update Project)
### 2. Branch ny funksjoner:
- Alle nye funksjonaliteter skal utvikles i en egen branch slik at vi unngår feil i main
### 3. Branch navn konvensjon:
- Alle nye brancher skal navngis '\<type\>/\<beskrivelse\>' der '\<type\>' beskriver hva man gjør og '\<beskrivelse\>' beskriver hvilken funksjonalitet det inngår. Vi deler inn '\<type\>' i 'feat' og 'fix', der 'feat' er implementasjon av nye features og 'fix' er debugging/bugfixing. \<beskrivelse\> er en kort beskrivelse av hva din feature er/gjør f.eks 'search-bar-home-screen'
### 4. Commit med meldinger:
- Alle commits skal ha et navn lik '\<verb\> \<funksjon\> \<sted\> i arkitektur', f.eks. 'Change color of search bar in home screen'. Disse meldingene skal ikke være lange, ca. 50-80 karakterer. Om det er vanskelig å skrive en kort melding kan det være lurt å dele opp commitene i deler som gir mening og skrive separate git-meldinger.
### 5.Test før du Committer:
- Test for feil før  du committer noe kode. Om du må Commite skriver du WIP(work in progress) i commit meldingen
### 6. Merge med main:
- Alle skal kjøre en 'Git pull' og lage en pull request for å merge med main. Ikke merge til main manuelt.
### 7. Merge Conflict:
- Skjer det en merge conflict, skal enten Albert eller Thomas kontaktes for å hjelpe å rette opp i den. Du kan også bruke git merge --abort for å abortere mergen
### 8. Forandre Main:
- Det er ikke lov å gjøre endringer på main. Uansett om det er en bugfix eller noe større, endringer skal alltid gjøres gjennom branches. Main endres bare via Pull requests
### 9. Force Push:
- Ikke bruk force push, dette kan være veldig destruktivt og er generelt bare unødvendig.
### 10. Slett Branches etter bruk:
- Brancher skal ikke leve lenger enn de trenger, dette minimerer sjansen for merge conflicts. Få en annen person til å se over før du sletter en branch om du gjør det manuelt.
### Det skal være lav terskel for å be om hjelp med noe som helst av problemer - ikke bare merge conflicts