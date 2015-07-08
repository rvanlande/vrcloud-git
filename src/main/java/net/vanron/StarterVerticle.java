package net.vanron;

import org.vertx.java.platform.Verticle;

/**
 * Created by RV on 10/04/2015.
 */
public class StarterVerticle extends Verticle {

    @Override
    public void start() {
	container.deployVerticle("net.vanron.ProxyVerticle");
	container.deployVerticle("net.vanron.BridgeVerticle");
	container.deployVerticle("net.vanron.AppVerticle");
    }
}
