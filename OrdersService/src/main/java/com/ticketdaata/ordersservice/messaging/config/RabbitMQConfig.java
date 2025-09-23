package com.ticketdaata.ordersservice.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange names
    public static final String TICKET_EXCHANGE = "ticket.exchange";
    public static final String ORDER_EXCHANGE = "order.exchange";

    // Queue names for Order Service
    public static final String ORDER_STATUS_QUEUE = "order.status.queue";
    public static final String ORDER_EXPIRATION_QUEUE = "order.expiration.queue";
    public static final String TICKET_STATUS_UPDATE_QUEUE = "ticket.status.update.queue";
    
    // Queue names for Ticket Service (to send messages)
    public static final String TICKET_RESERVATION_QUEUE = "ticket.reservation.queue";

    // Routing keys
    public static final String TICKET_RESERVE_ROUTING_KEY = "ticket.reserve";
    public static final String TICKET_RELEASE_ROUTING_KEY = "ticket.release";
    public static final String TICKET_SOLD_ROUTING_KEY = "ticket.sold";
    public static final String TICKET_STATUS_UPDATE_ROUTING_KEY = "ticket.status.update";
    
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";
    public static final String ORDER_COMPLETED_ROUTING_KEY = "order.completed";
    public static final String ORDER_CANCELLED_ROUTING_KEY = "order.cancelled";
    public static final String ORDER_EXPIRED_ROUTING_KEY = "order.expired";
    public static final String ORDER_EXPIRATION_ROUTING_KEY = "order.expiration";

    // Message converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Exchanges
    @Bean
    public TopicExchange ticketExchange() {
        return new TopicExchange(TICKET_EXCHANGE);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    // Queues for Order Service to process
    @Bean
    public Queue ticketStatusUpdateQueue() {
        return QueueBuilder.durable(TICKET_STATUS_UPDATE_QUEUE).build();
    }

    @Bean
    public Queue orderExpirationQueue() {
        return QueueBuilder.durable(ORDER_EXPIRATION_QUEUE).build();
    }

    // Queues for publishing order status updates
    @Bean
    public Queue orderStatusQueue() {
        return QueueBuilder.durable(ORDER_STATUS_QUEUE).build();
    }

    // Bindings for Order Service to receive ticket status updates
    @Bean
    public Binding ticketStatusUpdateBinding() {
        return BindingBuilder.bind(ticketStatusUpdateQueue())
                .to(ticketExchange())
                .with(TICKET_STATUS_UPDATE_ROUTING_KEY);
    }

    // Binding for order expiration
    @Bean
    public Binding orderExpirationBinding() {
        return BindingBuilder.bind(orderExpirationQueue())
                .to(orderExchange())
                .with(ORDER_EXPIRATION_ROUTING_KEY);
    }

    // Bindings for publishing order status
    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(orderExchange())
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding orderCompletedBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(orderExchange())
                .with(ORDER_COMPLETED_ROUTING_KEY);
    }

    @Bean
    public Binding orderCancelledBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(orderExchange())
                .with(ORDER_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public Binding orderExpiredBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(orderExchange())
                .with(ORDER_EXPIRED_ROUTING_KEY);
    }
}