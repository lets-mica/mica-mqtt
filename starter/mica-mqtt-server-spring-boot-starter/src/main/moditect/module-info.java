open module org.dromara.mica.mqtt.server.spring.boot.starter {
	requires lombok;
	requires spring.core;
	requires spring.context;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires transitive org.dromara.mica.mqtt.server;
	exports org.dromara.mica.mqtt.spring.server;
	exports org.dromara.mica.mqtt.spring.server.config;
	exports org.dromara.mica.mqtt.spring.server.event;
}
