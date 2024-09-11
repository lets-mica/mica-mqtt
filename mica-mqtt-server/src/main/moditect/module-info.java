open module net.dreamlu.mica.mqtt.server {
	requires transitive net.dreamlu.mica.mqtt.common;
	requires transitive net.dreamlu.mica.net.http;
	requires java.management;

	exports net.dreamlu.iot.mqtt.core.server;
	exports net.dreamlu.iot.mqtt.core.server.auth;
	exports net.dreamlu.iot.mqtt.core.server.broker;
	exports net.dreamlu.iot.mqtt.core.server.cluster;
	exports net.dreamlu.iot.mqtt.core.server.dispatcher;
	exports net.dreamlu.iot.mqtt.core.server.enums;
	exports net.dreamlu.iot.mqtt.core.server.event;
	exports net.dreamlu.iot.mqtt.core.server.http.handler;
	exports net.dreamlu.iot.mqtt.core.server.interceptor;
	exports net.dreamlu.iot.mqtt.core.server.model;
	exports net.dreamlu.iot.mqtt.core.server.protocol;
	exports net.dreamlu.iot.mqtt.core.server.serializer;
	exports net.dreamlu.iot.mqtt.core.server.session;
	exports net.dreamlu.iot.mqtt.core.server.store;
	exports net.dreamlu.iot.mqtt.core.server.support;
}
