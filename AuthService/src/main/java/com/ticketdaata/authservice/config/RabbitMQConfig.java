package com.ticketdaata.authservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Binds to the order.exchange/order.completed routing key that OrdersService
 * already publishes to (see OrdersService's own RabbitMQConfig) - no changes
 * needed on the publishing side, this just adds a new consumer queue.
 */
@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_COMPLETED_ROUTING_KEY = "order.completed";
    public static final String WALLET_CREDIT_QUEUE = "wallet.credit.queue";

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

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue walletCreditQueue() {
        return QueueBuilder.durable(WALLET_CREDIT_QUEUE).build();
    }

    @Bean
    public Binding walletCreditBinding() {
        return BindingBuilder.bind(walletCreditQueue())
                .to(orderExchange())
                .with(ORDER_COMPLETED_ROUTING_KEY);
    }
}
