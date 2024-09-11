open module net.dreamlu.mica.mqtt.client.spring.boot.starter {
	requires lombok;
	requires spring.core;
	requires spring.context;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires transitive net.dreamlu.mica.mqtt.client;
	exports net.dreamlu.iot.mqtt.spring.client;
	exports net.dreamlu.iot.mqtt.spring.client.config;
	exports net.dreamlu.iot.mqtt.spring.client.event;
}
