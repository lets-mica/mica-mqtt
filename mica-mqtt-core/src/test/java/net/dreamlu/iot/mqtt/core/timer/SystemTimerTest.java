package net.dreamlu.iot.mqtt.core.timer;

import net.dreamlu.iot.mqtt.core.util.timer.SystemTimer;
import net.dreamlu.iot.mqtt.core.util.timer.TimingWheelThread;

public class SystemTimerTest {

	public static void main(String[] args) throws InterruptedException {
		SystemTimer systemTimer = new SystemTimer("timer");

		TimingWheelThread timingWheelThread = new TimingWheelThread(systemTimer);
		timingWheelThread.start();

		System.out.println(System.currentTimeMillis());
		systemTimer.add(new DelayedOperation(5000));
		systemTimer.add(new DelayedOperation(7000));
		systemTimer.add(new DelayedOperation(10000));
		systemTimer.add(new DelayedOperation(14000));
		System.out.println(System.nanoTime());

		Thread.sleep(100000L);
	}
}
