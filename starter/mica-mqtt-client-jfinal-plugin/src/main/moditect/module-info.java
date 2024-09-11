open module net.dreamlu.mica.mqtt.client.jfinal.plugin {
	requires jfinal;
	requires transitive net.dreamlu.mica.mqtt.client;
	exports net.dreamlu.iot.mqtt.jfinal.client;
}
