FROM registry.access.redhat.com/ubi8/openjdk-11

ARG JMETER_VERSION="5.4.1"
ARG KAFKA_CLIENT_VERSION="2.7.0"
ARG PROMETHEUS_PLUGIN_VERSION="0.6.0"

LABEL name="JMeter - with Apache Kafka Load Teat Tool" \
      vendor="Apache" \
      io.k8s.display-name="JMeter - with Apache Kafka Load Teat Tool" \
      io.k8s.description="Load test using JMeter for Apache Kafka" \
      summary="Load test using JMeter for Apache Kafka" \
      io.openshift.tags="jmeter" \
      build-date="2021-03-10" \
      version="${JMETER_VERSION}" \
      kafkaclientversion="${KAFKA_CLIENT_VERSION}" \ 
      release="1" \
      maintainer="CK Gan <chengkuan@gmail.com>"

USER root

RUN microdnf install wget

ENV JMETER_HOME /opt/jmeter
ENV JMETER_BIN ${JMETER_HOME}/bin
ENV JMETER_TESTPLANS=${JMETER_HOME}/testplans
ENV JMETER_RESULTS=/tmp/jmeter-results
ENV PATH $JMETER_BIN:$PATH
ENV HEAP "-Xms512m -Xmx2048m"

RUN cd /opt && wget https://downloads.apache.org//jmeter/binaries/apache-jmeter-${JMETER_VERSION}.tgz && \
tar -xvzf apache-jmeter-${JMETER_VERSION}.tgz && \
rm apache-jmeter-${JMETER_VERSION}.tgz && \
mv apache-jmeter-${JMETER_VERSION} ${JMETER_HOME}

RUN wget https://repo1.maven.org/maven2/org/apache/kafka/kafka-clients/${KAFKA_CLIENT_VERSION}/kafka-clients-${KAFKA_CLIENT_VERSION}.jar && mv kafka-clients-${KAFKA_CLIENT_VERSION}.jar ${JMETER_HOME}/lib/

RUN wget https://repo1.maven.org/maven2/com/github/johrstrom/jmeter-prometheus-plugin/${PROMETHEUS_PLUGIN_VERSION}/jmeter-prometheus-plugin-${PROMETHEUS_PLUGIN_VERSION}.jar && mv jmeter-prometheus-plugin-${PROMETHEUS_PLUGIN_VERSION}.jar ${JMETER_HOME}/lib/ext/

RUN mkdir -p ${JMETER_TESTPLANS}
COPY ./testplans/* ${JMETER_TESTPLANS}/
COPY ./run.sh ${JMETER_BIN}/
RUN chmod +x ${JMETER_BIN}/run.sh

EXPOSE 8080

CMD ${JMETER_BIN}/run.sh
