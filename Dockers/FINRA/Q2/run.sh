#!/bin/bash

export HADOOP_VERSION=2.7.4
export HIVE_VERSION=2.3.2

cd /project/

mvn clean package

export HADOOP_HOME=/usr/share/hadoop-$HADOOP_VERSION
export beeline=/usr/share/apache-hive-$HIVE_VERSION-bin/bin/beeline

success=0
for i in {1..100}; do
	$beeline -u jdbc:hive2://hive-server:10000 -e "select 1;" > /dev/null 2>&1 && success=1 && break || echo "Waiting for Hive Server" && sleep 10
done

if [ "$success" = "1" ]; then
	echo "Connected"
	$beeline -u jdbc:hive2://hive-server:10000/default -f /project/Dockers/FINRA/Q2/hive.sql
fi
