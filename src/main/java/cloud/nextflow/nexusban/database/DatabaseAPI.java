package cloud.nextflow.nexusban.database;

import cloud.nextflow.nexusban.database.types.mongo.MongoConnector;
import cloud.nextflow.nexusban.database.types.mongo.MongoDB;
import cloud.nextflow.nexusban.database.types.sql.H2;
import cloud.nextflow.nexusban.database.types.sql.SQLConnector;
import cloud.nextflow.nexusban.database.types.sql.mysql.MariaDB;
import cloud.nextflow.nexusban.database.types.exceptions.DatabaseException;
import cloud.nextflow.nexusban.database.types.sql.mysql.MySQL;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DatabaseAPI {
    private static Map<String, SQLConnector> hikariCP = new HashMap<>();
    private static Map<String, MongoConnector> mongoConnector = new HashMap<>();

    public static SQLConnector getHikariCP(H2 type, Logger logger) throws DatabaseException {
        if (hikariCP.containsKey(type.file)) {
            return hikariCP.get(type.file);
        } else {
            SQLConnector hikari = new SQLConnector(type, logger);
            hikari.initialize();
            hikariCP.put(type.file, hikari);
            return hikari;
        }
    }

    public static SQLConnector getHikariCP(MariaDB type, Logger logger) throws DatabaseException {
        if (hikariCP.containsKey(type.database)) {
            return hikariCP.get(type.database);
        } else {
            SQLConnector hikari = new SQLConnector(type, logger);
            hikari.initialize();
            hikariCP.put(type.database, hikari);
            return hikari;
        }
    }

    public static SQLConnector getHikariCP(MySQL type, Logger logger) throws DatabaseException {
        if (hikariCP.containsKey(type.database)) {
            return hikariCP.get(type.database);
        } else {
            SQLConnector hikari = new SQLConnector(type, logger);
            hikari.initialize();
            hikariCP.put(type.database, hikari);
            return hikari;
        }
    }

    public static MongoConnector getMongoConnector(MongoDB type, Logger logger) throws DatabaseException {
        if (hikariCP.containsKey(type.database)) {
            return mongoConnector.get(type.database);
        } else {
            MongoConnector mongo = new MongoConnector(type, logger);
            mongoConnector.put(type.database, mongo);
            return mongo;
        }
    }
}
