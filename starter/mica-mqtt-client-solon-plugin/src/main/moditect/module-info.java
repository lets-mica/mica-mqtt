open module net.dreamlu.mica.mqtt.client.solon.plugin {
	requires solon;
	requires lombok;
	requires transitive net.dreamlu.mica.mqtt.client;
	exports net.dreamlu.iot.mqtt.client.noear;
	exports net.dreamlu.iot.mqtt.client.noear.event;
	exports net.dreamlu.iot.mqtt.client.noear.config;
	provides org.noear.solon.core.Plugin with net.dreamlu.iot.mqtt.client.noear.integration.MqttClientPluginImpl;
}
