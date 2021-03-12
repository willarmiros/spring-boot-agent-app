FROM public.ecr.aws/highspot/openjdk:11.0-jdk

# set the working directory in the container
WORKDIR /app

# copy the artifacts to the working directory
COPY target/contextaware-0.0.1-SNAPSHOT.jar .
COPY disco/ disco/

EXPOSE 8080

# command to run on container start
CMD [ "sh", "-c", "java -javaagent:disco/disco-java-agent.jar=pluginPath=disco/disco-plugins/ -jar contextaware-0.0.1-SNAPSHOT.jar" ]
