package com.lumastyle.eshop.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Registers business-related metrics so they are exported through Micrometer
 * and Prometheus. The counters track events such as order creation, failed
 * payments, sent e-mails or S3 operations. A timer measures how long it takes
 * to process an order, and a gauge captures the number of items currently in a
 * shopping cart.
 */
@Configuration
public class MetricsConfig {

    /**
     * Counter incremented whenever a new order is successfully created.
     */
    @Bean
    public Counter ordersCreatedCounter(MeterRegistry registry) {
        return Counter.builder("orders_created_total")
                .description("Total number of created orders")
                .register(registry);
    }

    /**
     * Counts payments that failed verification or processing.
     */
    @Bean
    public Counter ordersPaymentFailedCounter(MeterRegistry registry) {
        return Counter.builder("orders_payment_failed_total")
                .description("Number of failed payments")
                .register(registry);
    }

    /**
     * Measures how long the order creation and payment initialization take.
     */
    @Bean
    public Timer orderProcessingTimer(MeterRegistry registry) {
        return Timer.builder("order_processing_seconds")
                .description("Time to process order and payment")
                .publishPercentileHistogram()
                .register(registry);
    }

    /**
     * Counter for successfully sent e-mails.
     */
    @Bean
    public Counter emailsSentCounter(MeterRegistry registry) {
        return Counter.builder("emails_sent_total")
                .description("Emails successfully sent")
                .register(registry);
    }

    /**
     * Number of files uploaded to the S3 bucket.
     */
    @Bean
    public Counter s3UploadsCounter(MeterRegistry registry) {
        return Counter.builder("s3_uploads_total")
                .description("Files uploaded to S3")
                .register(registry);
    }

    /**
     * Counts objects removed from S3.
     */
    @Bean
    public Counter s3DeletesCounter(MeterRegistry registry) {
        return Counter.builder("s3_deletes_total")
                .description("Files deleted from S3")
                .register(registry);
    }

    /**
     * Total bytes transferred to S3 while uploading files.
     */
    @Bean
    public Counter s3TransferredBytesCounter(MeterRegistry registry) {
        return Counter.builder("s3_transferred_bytes")
                .description("Total bytes uploaded to S3")
                .baseUnit("bytes")
                .register(registry);
    }

    /**
     * Counts authentication attempts that failed due to invalid credentials.
     */
    @Bean
    public Counter authFailedCounter(MeterRegistry registry) {
        return Counter.builder("auth_failed_total")
                .description("Failed authentication attempts")
                .register(registry);
    }

    /**
     * Incremented when a provided JWT token is invalid or expired.
     */
    @Bean
    public Counter invalidTokenCounter(MeterRegistry registry) {
        return Counter.builder("invalid_token_total")
                .description("Invalid or expired JWT tokens")
                .register(registry);
    }

    /**
     * Gauge reporting the current total number of items in a user's cart.
     */
    @Bean
    public AtomicInteger cartItemsGauge(MeterRegistry registry) {
        AtomicInteger gauge = new AtomicInteger(0);
        Gauge.builder("cart_items_total", gauge, AtomicInteger::get)
                .description("Number of items in the current cart")
                .register(registry);
        return gauge;
    }
}

