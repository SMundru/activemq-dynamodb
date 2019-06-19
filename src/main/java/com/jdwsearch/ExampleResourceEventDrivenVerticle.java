package com.jdwsearch;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.util.CollectionUtils;
import com.jdwsearch.infrastructure.Properties;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.validation.ValidationException;
import io.vertx.reactivex.amqpbridge.AmqpBridge;
import io.vertx.reactivex.core.eventbus.Message;
import io.vertx.reactivex.core.eventbus.MessageProducer;
import io.vertx.reactivex.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;

@Slf4j
final class ExampleResourceEventDrivenVerticle extends AbstractEventDrivenVerticle {

    private final static String API_SPECIFICATION_FILE_PATH = "src/main/resources/api/search.yaml";
    private final static String OPERATION_ID = "search";

    private final String queueName;

    private final AmazonDynamoDBAsync client;

    ExampleResourceEventDrivenVerticle(final Properties properties) {
        super(properties, API_SPECIFICATION_FILE_PATH);
        queueName = properties.getQueue()
                .getName();
        this.client = AmazonDynamoDBAsyncClientBuilder.standard().withRegion(Regions.EU_WEST_2).build();
    }

    @Override
    protected void onStart(AmqpBridge amqpBridge, OpenAPI3RouterFactory routerFactory) {
        final MessageProducer<JsonObject> producer = amqpBridge.createProducer(queueName);
        amqpBridge.<JsonObject>createConsumer(queueName).handler(this::saveSearch);

        routerFactory.mountOperationToEventBus(OPERATION_ID, "search-history.post");

        routerFactory.addHandlerByOperationId(OPERATION_ID, context -> {
            final String user = context.request()
                    .getParam("user");

            final String site = context.request()
                    .getParam("site");

            String term = context.getBodyAsJson().getString("term");

            JsonObject eventPayload = new JsonObject().put("body", new JsonObject().put("user", user).put("site", site).put("term", term));
            producer.send(eventPayload);

            context.response()
                    .putHeader(CONTENT_TYPE, APPLICATION_JSON.getMimeType())
                    .setStatusCode(HttpResponseStatus.CREATED.code())
                    .end(new JsonObject().put("Success", true)
                            .encode());
        });

        routerFactory.addFailureHandlerByOperationId(OPERATION_ID, context -> {
            Throwable failure = context.failure();
            if (failure instanceof ValidationException) {
                context.response()
                        .putHeader(CONTENT_TYPE, TEXT_PLAIN.getMimeType())
                        .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                        .end(failure.getMessage());
            } else {
                log.error("handling resource failed", failure);

                context.response()
                        .putHeader(CONTENT_TYPE, TEXT_PLAIN.getMimeType())
                        .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                        .end(failure.getMessage());
            }
        });
    }

    private void saveSearch(Message<JsonObject> message) {
        JsonObject body = message.body().getJsonObject("body");
        log.info("received message {}", body.encode());

        PrimaryKey primaryKey = new PrimaryKey();
        String user = body.getString("user");
        primaryKey.addComponent("User", user);
        String site = body.getString("site");
        primaryKey.addComponent("Site", site);

        Item item = new Item().withPrimaryKey(primaryKey);

        String term = body.getString("term");
        DynamoDB dynamoDB = new DynamoDB(client);
        Table searchHistory = dynamoDB.getTable("SearchHistory");
        Item searchHistoryItem = searchHistory.getItem("User", user, "Site", site);
        if (searchHistoryItem != null) {
            List<Object> terms = searchHistoryItem.getList("Terms");
            if (CollectionUtils.isNullOrEmpty(terms)) {
                searchHistoryItem.withList("Terms", Collections.singletonList(term));
            } else {
                terms.add(term);
                searchHistoryItem.withList("Terms", terms);
            }
        } else {
            item.withList("Terms", Collections.singletonList(term));
            searchHistoryItem = item;
        }
        searchHistory.putItem(searchHistoryItem);
    }

}
