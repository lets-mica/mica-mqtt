open module net.dreamlu.mica.mqtt.common {
	requires transitive net.dreamlu.mica.net.core;
	requires transitive net.dreamlu.mica.mqtt.codec;
	exports net.dreamlu.iot.mqtt.core.common;
	exports net.dreamlu.iot.mqtt.core.util;
	exports net.dreamlu.iot.mqtt.core.util.timer;
	exports net.dreamlu.iot.mqtt.core.util.compression;
}
