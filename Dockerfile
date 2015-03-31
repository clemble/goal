FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 8080

ADD target/goal-management-0.17.0-SNAPSHOT.jar /data/goal-management.jar

CMD java -jar -Dspring.profiles.active=cloud /data/goal-management.jar
