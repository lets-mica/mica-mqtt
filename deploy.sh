#!/bin/sh

mvn clean deploy -Prelease -pl mica-mqtt-codec,mica-mqtt-core,mica-mqtt-spring-boot-starter
