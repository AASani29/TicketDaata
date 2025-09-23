package com.ticketdaata.ticketservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory Message Broker for Testing Without External RabbitMQ
 * This simulates RabbitMQ functionality for development/testing
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "messaging.mode", havingValue = "inmemory", matchIfMissing = true)
public class InMemoryMessageBroker {

    private final Map<String, ConcurrentLinkedQueue<Object>> queues = new ConcurrentHashMap<>();
    private final Map<String, Consumer<Object>> listeners = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    /**
     * Simulated RabbitTemplate for sending messages
     */
    @Bean
    @Primary
    public InMemoryRabbitTemplate inMemoryRabbitTemplate() {
        return new InMemoryRabbitTemplate(this);
    }

    /**
     * Send message to a queue
     */
    public void sendMessage(String exchange, String routingKey, Object message) {
        String queueName = determineQueueName(exchange, routingKey);
        
        queues.computeIfAbsent(queueName, k -> new ConcurrentLinkedQueue<>()).offer(message);
        
        log.info("📤 [InMemory] Sent message to queue '{}': {}", queueName, message.getClass().getSimpleName());
        
        // Process message asynchronously
        executor.schedule(() -> processMessage(queueName), 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Register a message listener
     */
    public void registerListener(String queueName, Consumer<Object> listener) {
        listeners.put(queueName, listener);
        log.info("🔔 [InMemory] Registered listener for queue: {}", queueName);
    }

    /**
     * Process messages in queue
     */
    private void processMessage(String queueName) {
        ConcurrentLinkedQueue<Object> queue = queues.get(queueName);
        Consumer<Object> listener = listeners.get(queueName);
        
        if (queue != null && listener != null && !queue.isEmpty()) {
            Object message = queue.poll();
            if (message != null) {
                try {
                    log.info("📥 [InMemory] Processing message from queue '{}': {}", queueName, message.getClass().getSimpleName());
                    listener.accept(message);
                    log.info("✅ [InMemory] Message processed successfully");
                } catch (Exception e) {
                    log.error("❌ [InMemory] Error processing message: {}", e.getMessage());
                    // Re-queue message for retry (simple retry logic)
                    queue.offer(message);
                }
            }
        }
    }

    /**
     * Map exchange + routing key to queue name
     */
    private String determineQueueName(String exchange, String routingKey) {
        // Simple mapping logic for our specific use case
        if ("ticket.status.exchange".equals(exchange)) {
            return "ticket.status." + routingKey.split("\\.")[2] + ".queue";
        } else if ("ticket.reservation.exchange".equals(exchange)) {
            return "ticket." + routingKey.split("\\.")[1] + ".queue";
        } else if ("order.events.exchange".equals(exchange)) {
            return "order." + routingKey.split("\\.")[1] + ".queue";
        }
        return routingKey + ".queue";
    }

    /**
     * Get queue statistics for monitoring
     */
    public void printQueueStats() {
        log.info("📊 [InMemory] Queue Statistics:");
        queues.forEach((queueName, queue) -> {
            log.info("   Queue '{}': {} messages pending", queueName, queue.size());
        });
    }

    /**
     * Simulated RabbitTemplate implementation
     */
    public static class InMemoryRabbitTemplate {
        private final InMemoryMessageBroker broker;

        public InMemoryRabbitTemplate(InMemoryMessageBroker broker) {
            this.broker = broker;
        }

        public void convertAndSend(String exchange, String routingKey, Object message) {
            broker.sendMessage(exchange, routingKey, message);
        }
    }

    // Create required beans to satisfy Spring Boot Auto-configuration
    @Bean
    public TopicExchange ticketStatusExchange() {
        log.info("🔧 [InMemory] Created ticket status exchange");
        return new TopicExchange("ticket.status.exchange");
    }

    @Bean  
    public TopicExchange ticketReservationExchange() {
        log.info("🔧 [InMemory] Created ticket reservation exchange");
        return new TopicExchange("ticket.reservation.exchange");
    }

    @Bean
    public TopicExchange orderEventsExchange() {
        log.info("🔧 [InMemory] Created order events exchange");
        return new TopicExchange("order.events.exchange");
    }

    @Bean
    public Queue ticketStatusReservedQueue() {
        return new Queue("ticket.status.reserved.queue");
    }

    @Bean
    public Queue ticketStatusSoldQueue() {
        return new Queue("ticket.status.sold.queue");
    }

    @Bean
    public Queue ticketStatusReleasedQueue() {
        return new Queue("ticket.status.released.queue");
    }

    @Bean
    public Queue ticketReserveQueue() {
        return new Queue("ticket.reserve.queue");
    }

    @Bean
    public Queue ticketReleaseQueue() {
        return new Queue("ticket.release.queue");
    }

    @Bean
    public Queue ticketExpireQueue() {
        return new Queue("ticket.expire.queue");
    }
}