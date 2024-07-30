package net.dreamlu.iot.mqtt.server.noear.controller;


import net.dreamlu.iot.mqtt.server.noear.service.ServerService;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;

@Mapping("/mqtt/server")
@Controller
public class ServerController {
	@Inject
	private ServerService service;

	@Mapping("publish")
	@Post
	public boolean publish(String body) {
		return service.publish(body);
	}

}
