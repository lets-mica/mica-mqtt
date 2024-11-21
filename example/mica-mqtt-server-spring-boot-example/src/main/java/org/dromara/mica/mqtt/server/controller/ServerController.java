package org.dromara.mica.mqtt.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dromara.mica.mqtt.server.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Mqtt::服务端")
@RequestMapping("/mqtt/server")
@RestController
public class ServerController {
	@Autowired
	private ServerService service;

	@Operation(summary = "publish")
	@PostMapping("publish")
	public boolean publish(@RequestBody String body) {
		return service.publish(body);
	}

}
