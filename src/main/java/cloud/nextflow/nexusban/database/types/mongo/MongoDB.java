package cloud.nextflow.nexusban.database.types.mongo;

import cloud.nextflow.nexusban.database.types.exceptions.DatabaseConfigException;

public class MongoDB implements MongoType {
    public String uri;
    public String database;
    public String collection;

    public MongoDB(String uri, String database, String collection) throws DatabaseConfigException {
        if (uri == null) throw new DatabaseConfigException("The URI for MongoDB is not defined in the config");
        if (database == null) throw new DatabaseConfigException("The database for MongoDB is not defined in the config");
        if (collection == null) throw new DatabaseConfigException("The collection for MongoDB is not defined in the config");

        this.uri = uri;
        this.database = database;
        this.collection = collection;
    }

    public MongoDB getType() {
        return this;
    }

    public String getString() {
        return "MongoDB";
    }
}
