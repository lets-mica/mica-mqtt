package net.dreamlu.iot.mqtt.mica.controller;

import io.swagger.annotations.Api;
import net.dreamlu.iot.mqtt.mica.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Mqtt::服务端")
@RequestMapping("/mqtt/server")
@RestController
public class ServerController {
	@Autowired
	private ServerService service;

	@PostMapping("publish")
	public boolean publish(@RequestBody String body) {
		return service.publish(body);
	}

}
