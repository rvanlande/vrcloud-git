package net.vanron;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.sockjs.SockJSServer;
import org.vertx.java.platform.Verticle;

/**
 * Created by RV on 10/04/2015.
 */
public class BridgeVerticle extends Verticle {

    @Override
    public void start() {

	HttpServer httpServer = vertx.createHttpServer();
	httpServer.requestHandler(new Handler<HttpServerRequest>() {
	    @Override
	    public void handle(HttpServerRequest request) {

		container.logger().info("BRIDGE receive PROXY HTTP request : " + request.uri());
		if (request.path().equals("/")) {
		    request.response().sendFile("ui/index.html");
		} else {
		    request.response().sendFile("ui/" + request.uri());
		}
	    }
	});

	JsonObject config = new JsonObject().putString("prefix", "/bus");
	SockJSServer sockJSServer = vertx.createSockJSServer(httpServer);
	JsonArray noPermitted = new JsonArray();
	noPermitted.add(new JsonObject());
	sockJSServer.bridge(config, noPermitted, noPermitted);

	httpServer.listen(90);

	container.logger().info("BRIDGE started !");
    }
}
