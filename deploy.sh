#!/bin/sh

mvn clean package deploy -Prelease -pl mica-mqtt-codec,mica-mqtt-core,mica-mqtt-spring-boot-starter
