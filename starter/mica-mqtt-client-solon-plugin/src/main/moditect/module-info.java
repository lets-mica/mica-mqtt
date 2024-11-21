open module org.dromara.mica.mqtt.client.solon.plugin {
	requires solon;
	requires lombok;
	requires transitive org.dromara.mica.mqtt.client;
	exports org.dromara.mica.mqtt.client.solon;
	exports org.dromara.mica.mqtt.client.solon.event;
	exports org.dromara.mica.mqtt.client.solon.config;
	provides org.noear.solon.core.Plugin with org.dromara.mica.mqtt.client.solon.integration.MqttClientPluginImpl;
}
