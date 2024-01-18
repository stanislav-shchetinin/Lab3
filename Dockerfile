FROM quay.io/wildfly/wildfly:27.0.0.Final-jdk11
ADD ./build/libs/l3-1.0-SNAPSHOT.war /opt/jboss/wildfly/standalone/deployments/