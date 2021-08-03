package net.dreamlu.iot.mqtt.mica.task;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.MqttServer;

import java.nio.ByteBuffer;
import java.util.Date;

/**
 * @author wsq
 */
public class PublishAllTask implements Runnable {
    private MqttServer mqttServer;
    @Override
    public void run() {
        System.out.println("----task1 start--------" + new Date().toLocaleString());
        try {
            mqttServer.publishAll("/test/123", ByteBuffer.wrap("mica最牛皮".getBytes()), MqttQoS.EXACTLY_ONCE);
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("----3s later, task1 end--------" + new Date().toLocaleString());
    }
}
