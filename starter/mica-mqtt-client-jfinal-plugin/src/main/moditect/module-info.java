open module org.dromara.mica.mqtt.client.jfinal.plugin {
	requires jfinal;
	requires transitive org.dromara.mica.mqtt.client;
	exports org.dromara.mica.mqtt.jfinal.client;
}
