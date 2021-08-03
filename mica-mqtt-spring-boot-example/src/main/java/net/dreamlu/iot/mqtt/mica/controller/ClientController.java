package net.dreamlu.iot.mqtt.mica.controller;

import net.dreamlu.iot.mqtt.mica.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/micamqtt")
@RestController
public class ClientController {
	@Autowired
	private ClientService service;

	@PostMapping("/client/publish")
	public boolean publish(@RequestBody String body) {
		return service.publish(body);
	}

	@GetMapping("/client/sub")
	public boolean sub() {
		return service.sub();
	}

}
