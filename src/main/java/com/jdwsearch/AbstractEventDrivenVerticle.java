package com.jdwsearch;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.vertx.amqpbridge.AmqpBridgeOptions;
import io.vertx.reactivex.amqpbridge.AmqpBridge;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.jdwsearch.infrastructure.Properties;
import com.jdwsearch.infrastructure.Properties.QueueProperties;
import com.jdwsearch.infrastructure.Properties.ServerProperties;

import java.util.concurrent.CompletableFuture;

import static java.util.Arrays.asList;
import static lombok.AccessLevel.PROTECTED;

@Slf4j
@RequiredArgsConstructor(access = PROTECTED)
public abstract class AbstractEventDrivenVerticle extends AbstractVerticle {

    private final Properties properties;
    private final String apiSpecificationFilePath;
    private final CompletableFuture<HttpServer> serverFuture = new CompletableFuture<>();
    private final CompletableFuture<AmqpBridge> bridgeFuture = new CompletableFuture<>();

    protected abstract void onStart(final AmqpBridge amqpBridge, final OpenAPI3RouterFactory routerFactory);

    @Override
    public final Completable rxStart() {
        return createBridge(properties.getQueue())
                .flatMap(bridge -> OpenAPI3RouterFactory.rxCreate(vertx, apiSpecificationFilePath)
                                                        .doOnSuccess(routerFactory -> onStart(bridge, routerFactory)))
                .map(OpenAPI3RouterFactory::getRouter)
                .flatMap(router -> startServer(router, properties.getServer()))
                .ignoreElement();
    }

    private Single<HttpServer> startServer(final Router router, final ServerProperties serverProperties) {
        return vertx.createHttpServer()
                    .requestHandler(router)
                    .rxListen(serverProperties.getPort(), serverProperties.getHostname())
                    .doOnSuccess(serverFuture::complete)
                    .doOnError(serverFuture::completeExceptionally)
                    .doOnSuccess(startedServer -> log.info("server listening on port {}", startedServer.actualPort()));
    }

    private Single<AmqpBridge> createBridge(final QueueProperties queueProperties) {
        return AmqpBridge.create(vertx, new AmqpBridgeOptions().setSsl(queueProperties.getSslEnabled()))
                         .rxStart(queueProperties.getHostname(),
                                  queueProperties.getPort(),
                                  queueProperties.getUsername(),
                                  queueProperties.getPassword())
                         .doOnSuccess(bridgeFuture::complete)
                         .doOnError(bridgeFuture::completeExceptionally);
    }

    @Override
    public final Completable rxStop() {
        return Completable.merge(asList(Single.fromFuture(serverFuture)
                                              .flatMapCompletable(HttpServer::rxClose),
                                        Single.fromFuture(bridgeFuture)
                                              .flatMapCompletable(AmqpBridge::rxClose)));
    }

}
