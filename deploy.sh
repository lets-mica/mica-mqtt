#!/bin/sh

## 1. java version
java -version
printf "\n"

## 2. mvn version
mvn -version
printf "\n"

## 3. deploy
mvn clean package deploy -Prelease -pl mica-mqtt-codec,mica-mqtt-model,mica-mqtt-core,starter/mica-mqtt-client-spring-boot-starter,starter/mica-mqtt-server-spring-boot-starter
