FROM openjdk:8

ADD build/libs/jms-client-1.0-SNAPSHOT.jar jms-client-0.0.1-SNAPSHOT.jar

ENTRYPOINT java -jar jms-client-0.0.1-SNAPSHOT.jar