open module org.dromara.mica.mqtt.server.jfinal.plugin {
	requires jfinal;
	requires transitive org.dromara.mica.mqtt.server;
	exports org.dromara.mica.mqtt.jfinal.server;
}
