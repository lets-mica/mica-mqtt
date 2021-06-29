package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;

/**
 * 服务端 pub 管理
 *
 * @author L.cm
 */
public interface IMqttPublishManager {

	/**
	 * 添加发布过程存储
	 *
	 * @param messageId      messageId
	 * @param pendingPublish MqttPendingPublish
	 */
	void addPendingPublish(int messageId, MqttPendingPublish pendingPublish);

	/**
	 * 获取发布过程存储
	 *
	 * @param messageId messageId
	 * @return MqttPendingPublish
	 */
	MqttPendingPublish getPendingPublish(int messageId);

	/**
	 * 删除发布过程中的存储
	 *
	 * @param messageId messageId
	 */
	void removePendingPublish(int messageId);

	/**
	 * 添加发布过程存储
	 *
	 * @param messageId          messageId
	 * @param pendingQos2Publish MqttPendingQos2Publish
	 */
	void addPendingQos2Publish(int messageId, MqttPendingQos2Publish pendingQos2Publish);

	/**
	 * 获取发布过程存储
	 * @param messageId messageId
	 * @return MqttPendingQos2Publish
	 */
	MqttPendingQos2Publish getPendingQos2Publish(int messageId);

	/**
	 * 删除发布过程中的存储
	 * @param messageId messageId
	 */
	void removePendingQos2Publish(int messageId);
}
