# internship-pcs-server-positions

Microservice for positions of the project _Internship 4.0_ from Poli-USP.

## Run Application

### Locally

```bash
docker-compose --profile local up -d
mvn spring-boot:run
```

### In a docker container

```bash
docker-compose --profile default up -d
```

## Deploy Application
First, check the current version of application on `pom.xml` file in the `master` branch. Then run:
```bash
git checkout develop
git checkout -b release/v<new_version>
./mvnw versions:set -DnewVersion=<new version>-SNAPSHOT
git add pom.xml
git commit -m "v<new version>"
git tag v<new version>
git push origin v<new version>
git checkout master
git merge release/v<new version> --no-ff
git push origin master
```
