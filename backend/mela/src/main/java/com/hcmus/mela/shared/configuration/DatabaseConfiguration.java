package com.hcmus.mela.shared.configuration;

import com.hcmus.mela.shared.utils.ProjectConstants;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableMongoRepositories(basePackages = {
        "com.hcmus.mela.exercise.repository",
        "com.hcmus.mela.lecture.repository",
        "com.hcmus.mela.ai.chatbot.repository",
        "com.hcmus.mela.history.repository",
        "com.hcmus.mela.auth.repository",
        "com.hcmus.mela.user.repository",
        "com.hcmus.mela.streak.repository",
        "com.hcmus.mela.review.repository",
        "com.hcmus.mela.suggestion.repository"
})
@RequiredArgsConstructor
public class DatabaseConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Bean
    @NonNull
    public MongoClient mongoClient() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .uuidRepresentation(org.bson.UuidRepresentation.STANDARD)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    @NonNull
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory(mongoClient, ProjectConstants.CONTENT_DATABASE_NAME);
    }

    @Bean
    @NonNull
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory,
                                                       MongoMappingContext mongoMappingContext) {
        DefaultDbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
        MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null)); // Remove _class field
        return mappingMongoConverter;
    }

    @Bean
    @NonNull
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory,
                                       MappingMongoConverter mappingMongoConverter) {
        return new MongoTemplate(mongoDatabaseFactory, mappingMongoConverter);
    }
}
