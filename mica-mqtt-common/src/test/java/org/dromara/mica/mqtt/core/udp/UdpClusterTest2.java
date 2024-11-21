package org.dromara.mica.mqtt.core.udp;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author L.cm
 */
public class UdpClusterTest2 {

	public static void main(String[] args) throws IOException {
		UdpTestHandler udpTestHandler = new UdpTestHandler();
		UdpClusterConfig udpServerConf = new UdpClusterConfig("224.0.0.1", 12345, udpTestHandler, 5000);
		UdpCluster udpCluster = new UdpCluster(udpServerConf);
		udpCluster.start();

		byte[] buffer = "hello2".getBytes();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				udpCluster.send(buffer);
			}
		};

		Timer timer = new Timer();
		timer.schedule(timerTask, 1000, 1000);

	}

}
