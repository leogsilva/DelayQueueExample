package br.com.devmedia.delayqueue;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.Test;

import com.yammer.metrics.reporting.MetricsServlet;

public class DelayQueueTest {

	
	@Test
	public void testDelayedEvent() throws Exception {
		DelayedEvent e = new DelayedEvent("simple", 1l, TimeUnit.SECONDS);
		Assert.assertTrue(e.getDelay(TimeUnit.MILLISECONDS) > 0);
		Thread.sleep(2000);
		Assert.assertTrue(e.getDelay(TimeUnit.MILLISECONDS) < 0);
	}

	@Test
	public void testDelayedQueue() throws Exception {
		URL indexUrl = DelayQueueTest.class.getResource("/web-app/index.html");
		String absolutePath = new File(indexUrl.toURI()).getParent();
		System.out.println(absolutePath);
		
		org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(5678);
		ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
		contextHandler.setContextPath("/");
		contextHandler.addServlet(new ServletHolder(new MetricsServlet()), "/metrics");
		contextHandler.addServlet(new ServletHolder(new SimpleThroughputControllerServlet()), "/service");
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setResourceBase(absolutePath);

		HandlerList handlerList = new HandlerList();
		handlerList.setHandlers(new Handler[] { contextHandler, resourceHandler });
		server.setHandler(handlerList);
		server.start();
		
//		CamelContext camelContext = new DefaultCamelContext();
//		camelContext.addRoutes(new RouteBuilder() {
//
//			@Override
//			public void configure() throws Exception {
//				from(
//						"jetty:http://0.0.0.0:5678/service1")
//						.process(new Processor() {
//
//							@Override
//							public void process(Exchange exchange)
//									throws Exception {
//								String id = exchange.getIn().getHeader("id",String.class);
//								exchange.getOut().setBody("<html><body>Ola "+ id + "</body></html>");
//							}
//
//						});
//			}
//
//		});
//		camelContext.start();
		// DelayQueue<DelayedEvent> queue = new DelayQueue<DelayedEvent>();
		// for(int i = 1 ; i <= 4; i++) {
		// DelayedEvent e = new DelayedEvent("task" + i, (long)i,
		// TimeUnit.SECONDS);
		// queue.offer(e);
		// }
		// DelayedEvent evt = null;
		// while ((evt = queue.poll(5, TimeUnit.SECONDS)) != null) {
		// System.out.println("Found: " + evt.getName());
		// }
		while (true) {
			Thread.sleep(1000);
		}
//		camelContext.stop();
	}
}
