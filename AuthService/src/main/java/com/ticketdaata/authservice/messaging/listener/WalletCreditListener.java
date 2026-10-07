package com.ticketdaata.authservice.messaging.listener;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ticketdaata.authservice.config.RabbitMQConfig;
import com.ticketdaata.authservice.entity.User;
import com.ticketdaata.authservice.messaging.dto.OrderCompletedMessage;
import com.ticketdaata.authservice.repository.UserRepository;

@Component
public class WalletCreditListener {

    private static final Logger log = LoggerFactory.getLogger(WalletCreditListener.class);

    private final UserRepository userRepository;

    public WalletCreditListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.WALLET_CREDIT_QUEUE)
    public void handleOrderCompleted(OrderCompletedMessage message) {
        log.info("Received order completed event for wallet credit: orderId={}, sellerId={}, amount={}",
                message.getOrderId(), message.getSellerId(), message.getTotalAmount());

        if (message.getSellerId() == null || message.getTotalAmount() == null) {
            log.warn("Order completed event missing sellerId or totalAmount, skipping credit: {}", message.getOrderId());
            return;
        }

        userRepository.findByUsername(message.getSellerId()).ifPresentOrElse(user -> {
            BigDecimal current = user.getBalance() != null ? user.getBalance() : BigDecimal.ZERO;
            user.setBalance(current.add(message.getTotalAmount()));
            userRepository.save(user);
            log.info("Credited {} to seller {}, new balance: {}", message.getTotalAmount(), message.getSellerId(), user.getBalance());
        }, () -> log.warn("Could not credit wallet: no user found with username {}", message.getSellerId()));
    }
}
