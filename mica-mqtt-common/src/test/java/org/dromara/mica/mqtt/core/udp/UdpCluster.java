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

package org.dromara.mica.mqtt.core.udp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Node;
import org.tio.core.udp.UdpPacket;
import org.tio.core.udp.UdpServer;
import org.tio.core.udp.task.UdpHandlerRunnable;
import org.tio.core.udp.task.UdpSendRunnable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingQueue;

public class UdpCluster {
	private static final Logger log = LoggerFactory.getLogger(UdpServer.class);
	private final LinkedBlockingQueue<UdpPacket> handlerQueue = new LinkedBlockingQueue<>();
	private final LinkedBlockingQueue<DatagramPacket> sendQueue = new LinkedBlockingQueue<>();
	private volatile boolean isStopped = false;
	private final MulticastSocket multicastSocket;
	private final byte[] readBuf;
	private final UdpHandlerRunnable udpHandlerRunnable;
	private final UdpSendRunnable udpSendRunnable;
	private final UdpClusterConfig clusterConfig;
	private final InetAddress group;
	private final int port;

	public UdpCluster(UdpClusterConfig clusterConfig) throws IOException {
		this.clusterConfig = clusterConfig;
		this.port = this.clusterConfig.getServerNode().getPort();
		this.multicastSocket = new MulticastSocket(port);
		this.readBuf = new byte[this.clusterConfig.getReadBufferSize()];
		this.udpHandlerRunnable = new UdpHandlerRunnable(this.clusterConfig.getUdpHandler(), handlerQueue, multicastSocket);
		this.udpSendRunnable = new UdpSendRunnable(sendQueue, this.clusterConfig, multicastSocket);
		this.multicastSocket.setSoTimeout(this.clusterConfig.getTimeout());
		this.group = InetAddress.getByName(this.clusterConfig.getServerNode().getIp());
		this.multicastSocket.joinGroup(this.group);
	}

	public void send(byte[] data) {
		DatagramPacket datagramPacket = new DatagramPacket(data, data.length, this.group, this.port);
		this.sendQueue.add(datagramPacket);
	}

	public void start() {
		startListen();
		startHandler();
		startSend();
	}

	private void startHandler() {
		Thread thread = new Thread(udpHandlerRunnable, "tio-udp-server-handler");
		thread.setDaemon(false);
		thread.start();
	}

	private void startListen() {
		Runnable runnable = () -> {
			String startLog = "started tio udp server: " + clusterConfig.getServerNode();
			if (log.isInfoEnabled()) {
				log.info(startLog);
			}
			while (!isStopped) {
				try {
					DatagramPacket datagramPacket = new DatagramPacket(readBuf, readBuf.length);
					multicastSocket.receive(datagramPacket);
					byte[] data = new byte[datagramPacket.getLength()];
					System.arraycopy(readBuf, 0, data, 0, datagramPacket.getLength());
					String remoteIp = datagramPacket.getAddress().getHostAddress();
					int remotePort = datagramPacket.getPort();
					Node remote = new Node(remoteIp, remotePort);
					UdpPacket udpPacket = new UdpPacket(data, remote);
					handlerQueue.put(udpPacket);
				} catch (Throwable e) {
					log.error(e.toString(), e);
				}
			}
		};

		Thread thread = new Thread(runnable, "tio-udp-server-listen");
		thread.setDaemon(false);
		thread.start();
	}

	private void startSend() {
		Thread thread = new Thread(udpSendRunnable, "tio-udp-client-send");
		thread.setDaemon(false);
		thread.start();
	}

	public void stop() {
		this.isStopped = true;
		this.multicastSocket.close();
		this.udpHandlerRunnable.stop();
	}

}
