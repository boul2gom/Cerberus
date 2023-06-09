package fr.boul2gom.cerberus.common.support.database;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoSupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSupport.class);

    private final MongoClient client;
    private final MongoDatabase database;

    public MongoSupport(CerberusAPI api) {
        LOGGER.info("Connecting asynchronously to MongoDB database...");

        final String host = api.getConfiguration().get(ConfigurationKey.MONGODB_HOST);
        final int port = api.getConfiguration().get(ConfigurationKey.MONGODB_PORT);
        final String username = api.getConfiguration().get(ConfigurationKey.MONGODB_USERNAME);
        final String password = api.getConfiguration().get(ConfigurationKey.MONGODB_PASSWORD);
        final String options = api.getConfiguration().get(ConfigurationKey.MONGODB_OPTIONS);

        final StringBuilder builder = new StringBuilder("mongodb://");
        if (username != null && password != null && !username.isEmpty() && !password.isEmpty()) {
            builder.append(username).append(":").append(password).append("@");
        }
        builder.append(host).append(":").append(port).append("/");
        if (options != null && !options.isEmpty()) {
            builder.append("?").append(options);
        }

        final ConnectionString connectionString = new ConnectionString(builder.toString());
        final MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();

        this.client = MongoClients.create(settings);

        final String database = api.getConfiguration().get(ConfigurationKey.MONGODB_DATABASE);
        this.database = this.client.getDatabase(database);
    }

    public MongoClient getClient() {
        return this.client;
    }

    public MongoCollection<Document> getCollection(String collection) {
        return this.database.getCollection(collection);
    }
}
