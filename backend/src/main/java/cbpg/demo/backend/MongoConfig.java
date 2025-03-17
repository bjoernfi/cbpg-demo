package cbpg.demo.backend;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories
public class MongoConfig {
    private @Value("${mongo.host}") String mongoHost;
    private @Value("${mongo.port}") Integer mongoPort;
    private @Value("${mongo.db}") String mongoDb;
    private @Value("${mongo.user}") String mongoUser;
    private @Value("${mongo.password}") String mongoPassword;

    @Bean
    public MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString(
                String.format("mongodb://%s:%s@%s:%d/%s?authSource=admin",
                        mongoUser, mongoPassword, mongoHost, mongoPort, mongoDb
                ));

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), mongoDb);
    }
}

