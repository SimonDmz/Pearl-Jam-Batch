# PearlJam-Back-Office
Batch service for PearlJam  
Batch using to implement PEARLJAM DB from xml files.

## Requirements
For building and running the application you need:
- [JDK 1.11](https://jdk.java.net/archive/)
- Maven 3

## Install and excute unit tests and ent-to-end tests
Use the maven clean and maven install  
``` shell
mvn clean install
```

## Running batch
Use the folowing cmd :
``` shell
echo $@
java8 -Xms64m -Xmx512m -classpath '/opt/insee/queen/developpement/lib/*' -Dlog4j.configurationFile=file:/opt/insee/queen/developpement/properties/log4j2.xml -Dproperties.path=/opt/insee/queen/developpement/properties -DcheminLog=/opt/insee/queen/developpement/log fr.insee.queen.batch.Lanceur $@
CODE_ERREUR=$? [DELETECAMPAIGN] || [LOADCAMPAIGN] || [LOADCONTEXT] || [DAILYUPDATE] || [SYNCHRONIZE])
echo "CODE ERREUR=$CODE_ERREUR"
exit $CODE_ERREUR
```

#### Properties file
Some properties are externalize in ${path.properties}/queen-bo.properties.  
Bellow, properties to define :
``` shell
fr.insee.pearljam.persistence.database.host = localhost
fr.insee.pearljam.persistence.database.port = 5433
fr.insee.pearljam.persistence.database.schema = XXXXXXXX
fr.insee.pearljam.persistence.database.user = XXXXXXXX
fr.insee.pearljam.persistence.database.password = XXXXXXXX
fr.insee.pearljam.persistence.database.driver = org.postgresql.Driver
fr.insee.pearljam.folder.in=path/to/in
fr.insee.pearljam.folder.out=path/to/out
fr.insee.pearljam.folder.processing=path/to/processing

# Context referential URL
fr.insee.pearljam.context.referential.service.url.scheme=http
fr.insee.pearljam.context.referential.service.url.host=localhost
fr.insee.pearljam.context.referential.service.url.port=8080
fr.insee.pearljam.context.referential.service.url.path=XXXXXXXX

#Maximum number of SU reaffected to an other interviewer during synchronization
fr.insee.pearljam.context.synchronization.interviewers.reaffectation.threshold.absolute=2
#Maximum percentage SU reaffected to an other interviewer / SU processed during synchronization
fr.insee.pearljam.context.synchronization.interviewers.reaffectation.threshold.relative=50
#Maximum number of SU reaffected to an other OU during synchronization
fr.insee.pearljam.context.synchronization.organization.reaffectation.threshold.absolute=2
#Maximum percentage SU reaffected to an other OU / SU processed during synchronization
fr.insee.pearljam.context.synchronization.organization.reaffectation.threshold.relative=50


#Keycloak configuration
keycloak.auth-server-url=http://localhost:8180/auth
keycloak.realm=XXX
keycloak.client.id=XXX
keycloak.client.secret=XXXXXX

# Log created and updated elements in the result file? ( YES | NO | IN_SEPARATE_FILE )
fr.insee.pearljam.context.synchronization.log.elements=YES

```

## Before you commit
Before committing code please ensure,  
1 - README.md is updated  
2 - A successful build is run and all tests are sucessful  
3 - All newly implemented APIs are documented  
4 - All newly added properties are documented  

## Libraries used
- spring-core
- spring-jdbc
- spring-oxm
- spring-data-jpa
- spring-web
- commons-lang3
- postgresql
- liquibase
- spring-test
- test-containers
- json-simple
- jackson-core
- jackson-databind
- log4j

## Developers
- Benjamin Claudel (benjamin.claudel@keyconsulting.fr)
- Samuel Corcaud (samuel.corcaud@keyconsulting.fr)
- Paul Guillemet (paul.guillemet@keyconsulting.fr)