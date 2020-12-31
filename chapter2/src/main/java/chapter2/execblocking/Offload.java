package chapter2.execblocking;

import io.vertx.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Offload extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(Offload.class);

  @Override
  public void start() {
    vertx.setPeriodic(5000, id -> {
      Context context = vertx.getOrCreateContext();
      context.put("key", new Random().nextInt());
      logger.info("Tick {}", (Object) context.get("key"));

      vertx.executeBlocking(this::blockingCode, this::resultHandler);
    });
  }

  private void blockingCode(Promise<String> promise) {
    logger.info("Blocking code running");
    try {
      Context context = vertx.getOrCreateContext();
      Thread.sleep(4000);
      logger.info("Done!" + context.get("key"));
      promise.complete("Ok!");
    } catch (InterruptedException e) {
      promise.fail(e);
    }
  }

  private void resultHandler(AsyncResult<String> ar) {
    if (ar.succeeded()) {
      Context context = vertx.getOrCreateContext();
      logger.info("Blocking code result: {} {}", ar.result(), context.get("key"));
    } else {
      logger.error("Woops", ar.cause());
    }
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DeploymentOptions opts = new DeploymentOptions()
      .setInstances(5);
    vertx.deployVerticle("chapter2.execblocking.Offload", opts);
  }
}
