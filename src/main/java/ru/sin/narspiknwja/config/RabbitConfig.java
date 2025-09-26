package ru.sin.narspiknwja.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
@RequiredArgsConstructor
public class RabbitConfig {
    public static final String queryReqQueue = "gtw.query.request.queue";

    public static final String historyReqQueue = "gtw.history.request.queue";

    private final String gtwExc = "gtw.exchange";

    private final String queryReqRoutingKey = "gtw.query.request.routing.key";
    private final String historyReqRoutingKey = "gtw.history.request.routing.key";

    @Bean
    public Queue queryRequestQueue() {
        return QueueBuilder.durable(queryReqQueue).build();
    }

    @Bean
    public Queue historyRequestQueue() {
        return QueueBuilder.durable(historyReqQueue).build();
    }

    @Bean
    public DirectExchange gtwExchange() {
        return new DirectExchange(gtwExc);
    }

    @Bean
    public Binding historyRequestBinding(Queue historyRequestQueue, DirectExchange gtwExchange) {
        return BindingBuilder.bind(historyRequestQueue)
                .to(gtwExchange)
                .with(historyReqRoutingKey);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        template.setReplyTimeout(5000);
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
