#!/bin/sh

## 1. java version
java -version
printf "\n"

## 2. mvn version
mvn -version
printf "\n"

## 3. modules
modules="mica-mqtt-codec,mica-mqtt-common,"
modules="$modules mica-mqtt-client,mica-mqtt-server,"
modules="$modules starter/mica-mqtt-client-spring-boot-starter,"
modules="$modules starter/mica-mqtt-server-spring-boot-starter,"
modules="$modules starter/mica-mqtt-client-solon-plugin,"
modules="$modules starter/mica-mqtt-server-solon-plugin,"
modules="$modules starter/mica-mqtt-client-jfinal-plugin,"
modules="$modules starter/mica-mqtt-server-jfinal-plugin"
printf "modules [%s] \n" "$modules"

## 4. deploy
mvn clean deploy -Prelease -pl "$modules"
