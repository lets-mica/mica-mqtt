open module net.dreamlu.mica.mqtt.server.jfinal.plugin {
	requires jfinal;
	requires transitive net.dreamlu.mica.mqtt.server;
	exports net.dreamlu.iot.mqtt.jfinal.server;
}
