package br.com.devmedia.delayqueue;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricName;
public class SimpleThroughputControllerServlet extends HttpServlet {

	public enum QueueManager {
		INSTANCE;
		
		private final Counter incomeRequests = Metrics.newCounter(new MetricName(SimpleThroughputControllerServlet.class,
				"incomeRequests"));

		private DelayQueue<DelayedEvent> queue = new DelayQueue<DelayedEvent>();
		private AtomicLong messageCounter = new AtomicLong(1);
		private AtomicLong delay = new AtomicLong(0);
		private ExecutorService consumerQueue = Executors.newSingleThreadExecutor();

		private QueueManager() {
			consumerQueue.submit(new Runnable() {

				public void run() {
					DelayedEvent de = null;
					try {
						while ((de = queue.take()) != null) {
							Calendar cal = Calendar.getInstance();
							System.out.println("Entrando: " + cal.getTime() + "--> " + de.getName());
							if (queue.size() == 1) {
								delay.set(0);							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		}
		
		public void offer(String id) {
			incomeRequests.inc();
			DelayedEvent event = new DelayedEvent(id, delay(), TimeUnit.SECONDS);
			queue.offer(event);

		}

		public synchronized Long delay() {
			if (messageCounter.get() % 2 == 0) {
				messageCounter.set(1);
				return delay.incrementAndGet();
			} else {
				messageCounter.incrementAndGet();
				return delay.get();
			}
		}
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -8462263352694372726L;
	private final Meter requests = Metrics.newMeter(SimpleThroughputControllerServlet.class, "requests", "requests", TimeUnit.SECONDS);

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		QueueManager.INSTANCE.offer("start");
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		requests.mark();
		String id = req.getParameter("id");
		QueueManager.INSTANCE.offer(id);
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
}
