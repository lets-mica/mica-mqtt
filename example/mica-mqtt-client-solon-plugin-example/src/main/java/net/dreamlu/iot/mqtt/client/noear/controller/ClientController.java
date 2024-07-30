package net.dreamlu.iot.mqtt.client.noear.controller;


import net.dreamlu.iot.mqtt.client.noear.service.ClientService;
import org.noear.solon.annotation.*;

@Mapping("/mqtt/client")
@Controller
public class ClientController {

	@Inject
	private ClientService service;

	@Post
	@Mapping("/publish")
	public boolean publish(String body) {
		return service.publish(body);
	}

	@Get
	@Mapping("/sub")
	public boolean sub() {
		return service.sub();
	}

}
