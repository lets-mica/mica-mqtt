/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & dreamlu.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.core.api.form;

/**
 * 发布的模型
 *
 * @author L.cm
 */
public class PublishForm extends BaseForm {

	/**
	 * 以 , 分割的多个主题，使用此字段能够同时发布消息到多个主题
	 */
	private String topics;
	/**
	 * 消息正文
	 */
	private String payload;
	/**
	 * 消息正文使用的编码方式，目前仅支持 plain 与 base64 两种
	 */
	private String encoding;
	/**
	 * QoS 等级 0
	 */
	private int qos = 0;
	/**
	 * 是否为保留消息
	 */
	private boolean retain = false;

	public String getTopics() {
		return topics;
	}

	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public boolean isRetain() {
		return retain;
	}

	public void setRetain(boolean retain) {
		this.retain = retain;
	}
}
