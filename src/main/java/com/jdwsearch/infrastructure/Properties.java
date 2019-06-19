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
                                                                                     .orElse("localhost"))
                                                                  .port(variables.get("QUEUE_PORT", Integer::parseInt)
                                                                                 .orElse(5672))
                                                                  .username(variables.get("QUEUE_USER")
                                                                                     .orElse("admin"))
                                                                  .password(variables.get("QUEUE_PASSWORD")
                                                                                     .orElse("admin"))
                                                                  .name(variables.get("QUEUE_NAME")
                                                                                 .orElse("TestQueue"))
                                                                  .sslEnabled(variables.get("QUEUE_SSL_ENABLED", Boolean::parseBoolean)
                                                                                       .orElse(false))
                                                                  .build())
                                            .build();
        log.info("using environment variables to define configuration {}", result);
        return result;
    }

}
