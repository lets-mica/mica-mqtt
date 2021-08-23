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

package net.dreamlu.iot.mqtt.core.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;

public class Node2 {
    public static void main(String[] args) throws Exception {
        int port = 12345;
        // 多播 ip 段 224.0.0.0 to 239.255.255.255
        MulticastSocket multicastSocket = new MulticastSocket(port);
        InetAddress group = InetAddress.getByName("224.0.0.1");
        multicastSocket.joinGroup(group);

        System.out.println("Multicast Receiver running at:" + multicastSocket.getLocalSocketAddress());

        String message = "Hello from node2";
        byte[] buffer = message.getBytes();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
                try {
                    multicastSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 1000);
//        mcSocket.leaveGroup(group);
//        mcSocket.close();
    }
}
