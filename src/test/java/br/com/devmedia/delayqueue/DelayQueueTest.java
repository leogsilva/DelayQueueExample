package br.com.devmedia.delayqueue;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.client.Address;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpExchange;
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

		HttpClient client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.start();
		HttpExchange exchange = new HttpExchange();
		exchange.setMethod("GET");
		exchange.setURL("http://localhost:5678/service?id=4321");
		client.send(exchange);
		int state = exchange.waitForDone();
		Assert.assertEquals(HttpExchange.STATUS_COMPLETED, state);
		Thread.sleep(10000);
		server.stop();
	}
}
