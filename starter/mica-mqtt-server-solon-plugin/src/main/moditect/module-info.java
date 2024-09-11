open module net.dreamlu.mica.mqtt.server.solon.plugin {
	requires solon;
	requires lombok;
	requires transitive net.dreamlu.mica.mqtt.server;
	exports net.dreamlu.iot.mqtt.server.noear;
	exports net.dreamlu.iot.mqtt.server.noear.event;
	exports net.dreamlu.iot.mqtt.server.noear.config;
	provides org.noear.solon.core.Plugin with net.dreamlu.iot.mqtt.server.noear.integration.MqttServerPluginImpl;
}
