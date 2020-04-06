FROM centos:7

ENV container docker
ENV JAVA_VERSON 1.8.0
ENV MAVEN_VERSION 3.3.9

ENV HADOOP_VERSION 2.7.4
ENV HIVE_VERSION 2.3.2
ENV HADOOP_URL https://archive.apache.org/dist/hadoop/core/hadoop-$HADOOP_VERSION/hadoop-$HADOOP_VERSION.tar.gz
ENV HIVE_URL https://archive.apache.org/dist/hive/hive-$HIVE_VERSION/apache-hive-$HIVE_VERSION-bin.tar.gz

RUN yum update -y && \
  yum install -y curl && \
  yum install -y bash && \
  yum install -y which && \
  yum install -y java-$JAVA_VERSON-openjdk java-$JAVA_VERSON-openjdk-devel

RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

RUN echo "Downloading $HADOOP_URL"
RUN curl -fsSL $HADOOP_URL | tar xzf - -C /usr/share
RUN echo "Downloading $HIVE_URL"
RUN curl -fsSL $HIVE_URL | tar xzf - -C /usr/share

#COPY run.sh /


