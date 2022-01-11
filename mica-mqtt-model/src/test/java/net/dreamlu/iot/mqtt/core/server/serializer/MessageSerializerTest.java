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

package net.dreamlu.iot.mqtt.core.server.serializer;

import net.dreamlu.iot.mqtt.core.server.enums.MessageType;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;

/**
 * DefaultMessageSerializer 测试
 *
 * @author L.cm
 */
public class MessageSerializerTest {

	@Test
	public void testConnect() {
		Message message = new Message();
		message.setMessageType(MessageType.CONNECT);
		message.setNode("node");
		message.setClientId("123");
		message.setUsername("userName");
		message.setPeerHost("127.0.0.1:1883");
		message.setTimestamp(System.currentTimeMillis());

		byte[] data = DefaultMessageSerializer.INSTANCE.serialize(message);
		Message message1 = DefaultMessageSerializer.INSTANCE.deserialize(data);
		System.out.println(message);
		System.out.println(message1);
		Assert.assertEquals(message, message1);
	}

	@Test
	public void testSubscribe() {
		Message message = new Message();
		message.setMessageType(MessageType.SUBSCRIBE);
		message.setNode("node");
		message.setClientId("123");
		message.setTopic("/abc/123/#");
		message.setQos(2);
		message.setPeerHost("127.0.0.1:1883");
		message.setTimestamp(System.currentTimeMillis());

		byte[] data = DefaultMessageSerializer.INSTANCE.serialize(message);
		Message message1 = DefaultMessageSerializer.INSTANCE.deserialize(data);
		System.out.println(message);
		System.out.println(message1);
		Assert.assertEquals(message, message1);
	}

	@Test
	public void testUnsubscribe() {
		Message message = new Message();
		message.setMessageType(MessageType.UNSUBSCRIBE);
		message.setNode("node");
		message.setClientId("123");
		message.setTopic("/abc/123/#");
		message.setPeerHost("127.0.0.1:1883");
		message.setTimestamp(System.currentTimeMillis());

		message.setMessageType(MessageType.UNSUBSCRIBE);
		byte[] data = DefaultMessageSerializer.INSTANCE.serialize(message);
		Message message1 = DefaultMessageSerializer.INSTANCE.deserialize(data);
		System.out.println(message);
		System.out.println(message1);
		Assert.assertEquals(message, message1);
	}

	@Test
	public void testUpStream() {
		Message message = new Message();
		message.setId(0xffff);
		message.setFromClientId("123");
		message.setFromUsername("name");
		message.setClientId("123");
		message.setUsername("userName");
		message.setNode("node");
		message.setMessageType(MessageType.UP_STREAM);
		message.setTopic("/mica/mqtt/123");
		message.setQos(1);
		message.setRetain(true);
		message.setDup(true);
		message.setPayload(ByteBuffer.wrap(new byte[]{1, 2, 3}));
		message.setPeerHost("127.0.0.1:1883");
		message.setTimestamp(System.currentTimeMillis());
		message.setPublishReceivedAt(System.currentTimeMillis());

		byte[] data = DefaultMessageSerializer.INSTANCE.serialize(message);
		Message message1 = DefaultMessageSerializer.INSTANCE.deserialize(data);
		System.out.println(message);
		System.out.println(message1);
		Assert.assertEquals(message, message1);
	}

	@Test
	public void testDownStream() {
		Message message = new Message();
		message.setId(0xffff);
		message.setClientId("123");
		message.setUsername("userName");
		message.setNode("node");
		message.setMessageType(MessageType.DOWN_STREAM);
		message.setTopic("/mica/mqtt/123");
		message.setQos(1);
		message.setRetain(true);
		message.setDup(true);
		message.setPayload(ByteBuffer.wrap(new byte[]{1, 2, 3}));
		message.setPeerHost("127.0.0.1:1883");
		message.setTimestamp(System.currentTimeMillis());
		message.setPublishReceivedAt(System.currentTimeMillis());

		byte[] data = DefaultMessageSerializer.INSTANCE.serialize(message);
		Message message1 = DefaultMessageSerializer.INSTANCE.deserialize(data);
		System.out.println(message);
		System.out.println(message1);
		Assert.assertEquals(message, message1);
	}

	@Test
	public void testDefaultMessageSerializer() {
		Message message = new Message();
		message.setMessageType(MessageType.CONNECT);
		byte[] data = DefaultMessageSerializer.INSTANCE.serialize(message);
		Message message1 = DefaultMessageSerializer.INSTANCE.deserialize(data);
		System.out.println(message);
		System.out.println(message1);
		Assert.assertEquals(message, message1);
	}

	@Test
	public void testFastJsonMessageSerializer() {
		Message message = new Message();
		message.setId(0xffff);
		message.setFromClientId("123");
		message.setFromUsername("name");
		message.setClientId("123");
		message.setUsername("userName");
		message.setNode("node");
		message.setMessageType(MessageType.UP_STREAM);
		message.setTopic("/mica/mqtt/123");
		message.setQos(1);
		message.setRetain(true);
		message.setDup(true);
		message.setPayload(ByteBuffer.wrap(new byte[]{1, 2, 3}));
		message.setPeerHost("127.0.0.1:1883");
		message.setTimestamp(System.currentTimeMillis());
		message.setPublishReceivedAt(System.currentTimeMillis());

		FastJsonMessageSerializer serializer = new FastJsonMessageSerializer();
		byte[] data = serializer.serialize(message);
		Message message1 = serializer.deserialize(data);
		System.out.println(message);
		System.out.println(message1);
		Assert.assertEquals(message, message1);
	}

}
