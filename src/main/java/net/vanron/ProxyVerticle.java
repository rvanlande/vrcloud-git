package net.vanron;

import org.vertx.java.core.Handler;
import org.vertx.java.core.MultiMap;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.core.sockjs.SockJSSocket;
import org.vertx.java.core.streams.Pump;
import org.vertx.java.platform.Verticle;

import java.util.Iterator;
import java.util.Map;

public class ProxyVerticle extends Verticle {

    public void start() {


	HttpServer httpServer = vertx.createHttpServer();
	httpServer.requestHandler(new Handler<HttpServerRequest>() {
	    @Override
	    public void handle(final HttpServerRequest request) {

		container.logger().info("PROXY receive CLIENT HTTP request = " + request.uri());

		HttpClient client = vertx.createHttpClient().setHost("localhost").setPort(90);
		HttpClientRequest clientRequest = client.get(request.path(), new Handler<HttpClientResponse>() {
		    @Override
		    public void handle(HttpClientResponse response) {

			container.logger().info("PROXY receive APP HTTP request = " + request.uri());

			Pump.createPump(response, request.response().setChunked(true)).start();
			response.endHandler(new Handler<Void>() {
			    @Override
			    public void handle(Void aVoid) {
				request.response().end();
			    }
			});
		    }
		});
		clientRequest.end();
	    }
	});

	SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
	JsonObject config = new JsonObject().putString("prefix", "/bus");
	sockJSServer.installApp(config, new Handler<SockJSSocket>() {
	    @Override
	    public void handle(final SockJSSocket sockJSSocket) {

		container.logger().info("PROXY init CLIENT->PROXY sockjs-socket = " + sockJSSocket.uri());

		HttpClient client = vertx.createHttpClient().setHost("localhost").setPort(90);
		client.connectWebsocket("/bus/websocket", new Handler<WebSocket>() {

		    @Override
		    public void handle(WebSocket webSocket) {

			container.logger().info("PROXY init PROXY->BRIDGE websocket = " + sockJSSocket.uri());


			// redirection socket CLIENT->PROXY vers socket PROXY->BRIDGE
			Pump.createPump(sockJSSocket, webSocket).start();
			// redirection socket BRIDGE->PROXY vers socket PROXY->CLIENT
			Pump.createPump(webSocket, sockJSSocket).start();
		    }
		});
	    }
	});

	httpServer.listen(80);
	container.logger().info("PROXY started !");

    }
}
