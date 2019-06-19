package com.jdwsearch;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import com.jdwsearch.infrastructure.Properties;

import static com.jdwsearch.infrastructure.LoggerConfiguration.configureLogger;
@Slf4j
class EventDrivenServiceApplication {

    static {
        configureLogger();
    }

    public static void main(String... args) {
        Vertx vertx = Vertx.vertx();
        Properties properties = Properties.fromEnvironmentVariables();
        vertx.rxDeployVerticle(new ExampleResourceEventDrivenVerticle(properties))
             .subscribe(verticleId -> log.info("verticle deployed successfully (ID: {})", verticleId),
                        verticleDeployingError -> {
                            log.error("failed to deploy verticle", verticleDeployingError);
                            vertx.rxClose()
                                 .subscribe(() -> log.info("vertx closed successfully"),
                                            vertxClosingError -> log.error("vertx failed to close", vertxClosingError));
                        });
    }
}