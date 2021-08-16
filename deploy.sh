#!/bin/sh

## 1. java version
java -version
echo "\n"

## 2. mvn version
mvn -version
echo "\n"

## 3. deploy
mvn clean package deploy -Prelease -pl mica-mqtt-codec,mica-mqtt-core,mica-mqtt-spring-boot-starter
