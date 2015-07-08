package net.vanron;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.platform.Verticle;

/**
 * Created by RV on 18/04/2015.
 */
public class AppVerticle extends Verticle {

    @Override
    public void start() {

	final EventBus bus = vertx.eventBus();
	bus.registerHandler("apps.myapp", new Handler<Message>() {
	    @Override
	    public void handle(Message message) {

		container.logger().info("AppVerticle.receiveBusMessage = " + message.body());
		bus.publish("client1", "Message retour");

	    }
	});

	container.logger().info("APP started !");
    }
}
