package org.dromara.mica.mqtt.core.udp;

import org.tio.core.Node;
import org.tio.core.udp.UdpConf;
import org.tio.core.udp.intf.UdpHandler;

public class UdpClusterConfig extends UdpConf {
	private UdpHandler udpHandler;
	private int readBufferSize = 1024 * 1024;

	public UdpClusterConfig(String ip, int port, UdpHandler udpHandler, int timeout) {
		super(timeout);
		this.setUdpHandler(udpHandler);
		this.setServerNode(new Node(ip, port));
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public UdpHandler getUdpHandler() {
		return udpHandler;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}

	public void setUdpHandler(UdpHandler udpHandler) {
		this.udpHandler = udpHandler;
	}
}
