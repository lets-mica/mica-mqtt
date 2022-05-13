package net.dreamlu.iot.mqtt.core.timer;

import net.dreamlu.iot.mqtt.core.util.timer.SystemTimer;

public class SystemTimerTest {
    public static void main(String[] args) {
        SystemTimer systemTimer = new SystemTimer("timer");
        System.out.println(System.currentTimeMillis());
        systemTimer.add(new DelayedOperation(5000));
        systemTimer.add(new DelayedOperation(7000));
        systemTimer.add(new DelayedOperation(10000));
        systemTimer.add(new DelayedOperation(14000));
        System.out.println(System.nanoTime());
        boolean flag = true;
        while (flag) {
            boolean b = systemTimer.advanceClock(200);
            if (b) {
                System.out.println(systemTimer.size());
            }
        }
    }
}
