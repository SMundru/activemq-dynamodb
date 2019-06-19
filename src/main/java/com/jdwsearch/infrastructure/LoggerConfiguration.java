package com.jdwsearch.infrastructure;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.slf4j.LoggerFactory;

import static ch.qos.logback.classic.Level.INFO;

public final class LoggerConfiguration {

    private final static String LOG_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} %highlight(%-5level) [%cyan(%-30.30logger{10})] %msg%n";

    public static void configureLogger() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder logEncoder = new PatternLayoutEncoder();
        logEncoder.setContext(loggerContext);
        logEncoder.setPattern(LOG_PATTERN);
        logEncoder.start();

        ConsoleAppender<ILoggingEvent> logConsoleAppender = new ConsoleAppender<>();
        logConsoleAppender.setContext(loggerContext);
        logConsoleAppender.setName("console");
        logConsoleAppender.setEncoder(logEncoder);
        logConsoleAppender.start();

        Logger log = loggerContext.getLogger("ROOT");
        log.detachAndStopAllAppenders();
        log.setAdditive(false);
        log.setLevel(INFO);
        log.addAppender(logConsoleAppender);
    }

}
