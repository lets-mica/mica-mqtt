package net.dreamlu.iot.mqtt.core.server;

/**
 * mqtt 服务端，认证处理器
 *
 * @author L.cm
 */
public interface IMqttAuthHandler {

	/**
	 * 认证
	 *
	 * @param clientId 客户端 ID
	 * @param userName 用户名
	 * @param password 密码
	 * @return 是否认证成功
	 */
	boolean authenticate(String clientId, String userName, String password);

}
