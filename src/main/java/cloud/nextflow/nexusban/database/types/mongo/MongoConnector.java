package cloud.nextflow.nexusban.database.types.mongo;

import cloud.nextflow.nexusban.database.types.general.DatabaseType;
import cloud.nextflow.nexusban.database.types.general.DBConnector;
import cloud.nextflow.nexusban.exceptions.DatabaseException;
import com.mongodb.MongoClientException;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.logging.Logger;

public class MongoConnector extends DBConnector {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoConnector(MongoDB type, Logger logger) throws DatabaseException {
        super(logger);
        try {
            mongoClient = MongoClients.create(type.uri);
            database = mongoClient.getDatabase(type.database);
            this.collection = database.getCollection(type.collection);
        } catch (MongoClientException exception) {
            throw new DatabaseException("Failed to connect to the MongoDB database", exception);
        }
        logger.info("Connected to the MongoDB database.");
    }

    public MongoClient getClient() {
        return this.mongoClient;
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public MongoCollection<Document> getCollection() {
        return this.collection;
    }

    public void setCollection(String collection) throws DatabaseException {
        try {
            this.collection = database.getCollection(collection);
        } catch (MongoException exception) {
            throw new DatabaseException("Error while setting collection", exception);
        }
    }

    public void closeConnection() {
        this.mongoClient.close();
    }

    @Override
    public DatabaseType getDatabaseType() {
        return DatabaseType.MONGODB;
    }
}
