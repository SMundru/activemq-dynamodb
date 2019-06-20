package com.jdwsearch.infrastructure;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Getter
@ToString
@Builder(access = PACKAGE)
@RequiredArgsConstructor(access = PRIVATE)
public final class Properties {

    @Getter
    @ToString
    @Builder(access = PACKAGE)
    @RequiredArgsConstructor(access = PRIVATE)
    public final static class QueueProperties {
        private final String hostname;
        private final int port;
        private final String username;
        private final String password;
        private final String name;
        private final Boolean sslEnabled;
    }
    @Getter
    @ToString
    @Builder(access = PACKAGE)
    @RequiredArgsConstructor(access = PRIVATE)
    public final static class ServerProperties {
        private final String hostname;
        private final int port;
    }

    private final ServerProperties server;
    private final QueueProperties queue;

    public static Properties fromEnvironmentVariables() {
        return fromEnvironmentVariables(new EnvironmentVariables());
    }

    static Properties fromEnvironmentVariables(final EnvironmentVariables variables) {
        final Properties result = Properties.builder()
                                            .server(ServerProperties.builder()
                                                                    .hostname(variables.get("SERVER_HOST")
                                                                                       .orElse("localhost"))
                                                                    .port(variables.get("SERVER_PORT", Integer::parseInt)
                                                                                   .orElse(3000))
                                                                    .build())
                                            .queue(QueueProperties.builder()
                                                                  .hostname(variables.get("QUEUE_HOST")
                                                                                     .orElse("b-5b520fd3-887f-4097-85b6-caaf6921e023-1.mq.eu-west-2.amazonaws.com"))
                                                                  .port(variables.get("QUEUE_PORT", Integer::parseInt)
                                                                                 .orElse(5671))
                                                                  .username(variables.get("QUEUE_USER")
                                                                                     .orElse("search"))
                                                                  .password(variables.get("QUEUE_PASSWORD")
                                                                                     .orElse("SearchSquadNBrown"))
                                                                  .name(variables.get("QUEUE_NAME")
                                                                                 .orElse("Search-History-Queue"))
                                                                  .sslEnabled(variables.get("QUEUE_SSL_ENABLED", Boolean::parseBoolean)
                                                                                       .orElse(true))
                                                                  .build())
                                            .build();
        log.info("using environment variables to define configuration {}", result);
        return result;
    }

}
