package com.ticketdaata.ticketservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        log.info("MongoConfig: Database name requested: {}", databaseName);
        return databaseName;
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        log.info("MongoConfig: Creating MongoClient with URI: {}", mongoUri);
        log.info("MongoConfig: Connecting to database: {}", databaseName);

        try {
            MongoClient client = MongoClients.create(mongoUri);
            log.info("MongoConfig: MongoClient created successfully");

            // Test the connection
            client.getDatabase(databaseName).listCollectionNames().first();
            log.info("MongoConfig: Successfully connected to MongoDB Atlas database: {}", databaseName);

            return client;
        } catch (Exception e) {
            log.error("MongoConfig: Failed to create MongoClient or connect to database: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        log.info("MongoConfig: Creating MongoTemplate for database: {}", databaseName);
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}