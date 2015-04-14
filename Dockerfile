FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10006

ADD target/goal-management-*-SNAPSHOT.jar /data/goal-management.jar

CMD java -jar -Dspring.profiles.active=cloud -Dserver.port=10006 /data/goal-management.jar
