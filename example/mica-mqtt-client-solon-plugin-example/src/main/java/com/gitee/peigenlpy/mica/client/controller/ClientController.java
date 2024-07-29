package com.gitee.peigenlpy.mica.client.controller;


import com.gitee.peigenlpy.mica.client.service.ClientService;
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
