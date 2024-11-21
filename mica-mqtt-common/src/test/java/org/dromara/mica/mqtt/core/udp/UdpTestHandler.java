package org.dromara.mica.mqtt.core.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Node;
import org.tio.core.udp.UdpPacket;
import org.tio.core.udp.intf.UdpHandler;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * @author tanyaowu
 */
public class UdpTestHandler implements UdpHandler {
	private static final Logger log = LoggerFactory.getLogger(UdpTestHandler.class);

	public UdpTestHandler() {
	}

	@Override
	public void handler(UdpPacket udpPacket, DatagramSocket datagramSocket) {
		byte[] data = udpPacket.getData();
		String msg = new String(data);
		Node remote = udpPacket.getRemote();

		System.out.printf("收到来自%s的消息:【%s】%n", remote, msg);
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length);
		try {
			datagramSocket.send(datagramPacket);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}
}
