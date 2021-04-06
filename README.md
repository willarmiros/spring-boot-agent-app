# Spring Boot Sample App with X-Ray Java Agent

## Getting Started

Running locally:

```shell script
mvn package
java -javaagent:disco/disco-java-agent.jar=pluginPath=disco/disco-plugins -jar target/contextaware-0.0.1-SNAPSHOT.jar
```

Running on Docker:

```shell script
docker-compose up
```
