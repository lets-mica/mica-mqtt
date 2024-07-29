package com.gitee.peigenlpy.mica.server.controller;


import com.gitee.peigenlpy.mica.server.service.ServerService;
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
