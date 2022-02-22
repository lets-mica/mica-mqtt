package net.dreamlu.iot.mqtt.client.controller;

import io.swagger.annotations.Api;
import net.dreamlu.iot.mqtt.client.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Mqtt::客户端")
@RequestMapping("/mqtt/client")
@RestController
public class ClientController {

	@Autowired
	private ClientService service;

	@PostMapping("/publish")
	public boolean publish(@RequestBody String body) {
		return service.publish(body);
	}

	@GetMapping("/sub")
	public boolean sub() {
		return service.sub();
	}

}
