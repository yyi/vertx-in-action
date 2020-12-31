package chapter4.streamapis;

import io.vertx.core.Vertx;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;

import java.util.concurrent.atomic.AtomicInteger;

public class VertxStreams {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    OpenOptions opts = new OpenOptions().setRead(true).setCreate(false);
    vertx.fileSystem().open("build.gradle.kts", opts, ar -> {
      final AtomicInteger i = new AtomicInteger(0);
      if (ar.succeeded()) {
        AsyncFile file = ar.result();
        file.handler(buf -> {
          System.out.println("----------------------------------:" + i.incrementAndGet());
          System.out.print(buf);

        })
          .exceptionHandler(Throwable::printStackTrace)
          .endHandler(done -> {
            System.out.println("\n--- DONE");
            vertx.close();
          });
      } else {
        ar.cause().printStackTrace();
      }
    });
  }
}
