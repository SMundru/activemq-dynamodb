package com.jdwsearch.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static lombok.AccessLevel.PACKAGE;

@Slf4j
@RequiredArgsConstructor(access = PACKAGE)
final class EnvironmentVariables {

    EnvironmentVariables() {
        this(System.getenv());
    }

    private final Map<String, String> environment;

    Optional<String> get(final String variableName) {
        return get(variableName, identity());
    }

    <T> Optional<T> get(final String variableName, final Function<String, T> valueTransformer) {
        try {
            return Optional.of(environment)
                           .map(env -> env.get(variableName))
                           .map(valueTransformer);
        } catch (Exception exception) {
            log.error("fetching environment variable '" + variableName + "' failed", exception);
            return Optional.empty();
        }
    }

}
