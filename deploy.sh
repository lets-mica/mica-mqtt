#!/bin/sh

## 1. java version
java -version
printf "\n"

## 2. mvn version
mvn -version
printf "\n"

## 3. deploy
mvn clean package deploy -Prelease -pl mica-mqtt-codec,mica-mqtt-common,mica-mqtt-client,mica-mqtt-server,starter/mica-mqtt-client-spring-boot-starter,starter/mica-mqtt-server-spring-boot-starter,starter/jfinal-mica-mqtt-client,starter/jfinal-mica-mqtt-server
