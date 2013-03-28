package br.com.devmedia.delayqueue;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public final class DelayedEvent implements Delayed {

	private String name;
	private Long delay;
	private TimeUnit unit;

	public DelayedEvent(String name, Long delay, TimeUnit unit) {
		this.name = name;
		this.delay = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delay, unit);
		System.out.println(this.delay);
		this.unit = unit;
	}
	
	@Override
	public int compareTo(Delayed o) {
		if (o instanceof DelayedEvent) {
			DelayedEvent event = DelayedEvent.class.cast(o);
			return this.delay().compareTo(event.delay());
		} else 
			return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(delay - System.nanoTime() ,TimeUnit.NANOSECONDS);
	}
	
	private Long delay() {
		return this.delay;
	}
	
	public String getName() {
		return this.name;
	}
}