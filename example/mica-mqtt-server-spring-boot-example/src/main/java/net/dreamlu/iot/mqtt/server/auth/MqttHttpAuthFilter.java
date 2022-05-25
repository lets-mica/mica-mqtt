package net.dreamlu.iot.mqtt.server.auth;

import net.dreamlu.iot.mqtt.core.server.http.api.code.ResultCode;
import net.dreamlu.iot.mqtt.core.server.http.api.result.Result;
import net.dreamlu.iot.mqtt.core.server.http.handler.HttpFilter;
import net.dreamlu.iot.mqtt.core.server.http.handler.MqttHttpRoutes;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 * 示例自定义 mqtt http 接口认证，请按照自己的需求和业务进行扩展
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
public class MqttHttpAuthFilter implements HttpFilter, InitializingBean {

	@Override
	public boolean filter(HttpRequest request) throws Exception {
		// 自行实现逻辑
		return true;
	}

	@Override
	public HttpResponse response(HttpRequest request) {
		// 认证不通过时的响应
		return Result.fail(request, ResultCode.E103);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		MqttHttpRoutes.addFilter(this);
	}
}
