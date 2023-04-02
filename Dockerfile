FROM amazoncorretto:17.0.2
COPY ./build/libs/batchadmin-0.0.1-SNAPSHOT.jar /data/
COPY ./dd-java-agent.jar /data/
CMD java $JVM_OPTS -jar /data/batchadmin-0.0.1-SNAPSHOT.jar