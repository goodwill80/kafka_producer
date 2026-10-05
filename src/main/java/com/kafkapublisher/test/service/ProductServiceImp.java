package com.kafkapublisher.test.service;

import com.kafkapublisher.test.vo.CreateProductRestModel;
import com.kafkapublisher.test.vo.kafka.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class ProductServiceImp implements ProductService {

    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImp(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProduct(CreateProductRestModel productRestModel) {
        String productId = UUID.randomUUID().toString();
        // TODO: Persist ProductDetails into database before publishing an Event

        ProductCreatedEvent productCreatedEvent =
                new ProductCreatedEvent(productId, productRestModel.getTitle(), productRestModel.getPrice(), productRestModel.getQuantity());

        CompletableFuture<SendResult<String, ProductCreatedEvent>> future =
                kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent);

        future.whenComplete((result, exception) -> {
            if(exception != null) {
                LOGGER.error("*********** Failed to send message: {}", exception.getMessage());
            } else {
                LOGGER.info("*********** Message sent successfully: {}", result.getRecordMetadata());
            }
        });
        // SendResult<String, ProductCreatedEvent> result = kafkaTemplate.send("product-created-events-topic", productId, productCreatedEvent).get();
        // future.join(); // This makes kafka synchronous, and will wait until the completable future completes
        LOGGER.info("********** Returning product id");
        return productId;
    }
}
