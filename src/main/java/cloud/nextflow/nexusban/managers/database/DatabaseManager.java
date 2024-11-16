package cloud.nextflow.nexusban.managers.database;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.database.DBUtils;
import cloud.nextflow.nexusban.database.DatabaseAPI;
import cloud.nextflow.nexusban.database.types.general.ConnectorType;
import cloud.nextflow.nexusban.database.types.general.DatabaseType;
import cloud.nextflow.nexusban.database.types.mongo.MongoConnector;
import cloud.nextflow.nexusban.database.types.mongo.MongoDB;
import cloud.nextflow.nexusban.database.types.sql.H2;
import cloud.nextflow.nexusban.database.types.sql.MariaDB;
import cloud.nextflow.nexusban.database.types.sql.SQLConnector;
import cloud.nextflow.nexusban.exceptions.DatabaseConfigException;
import cloud.nextflow.nexusban.exceptions.DatabaseException;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class DatabaseManager extends NexusManager {

    private DBUtils dbUtils;
    private DatabaseType databaseType;
    private ConnectorType connectorType = ConnectorType.SQL;

    public DatabaseManager(NexusBan nexusBan) {
        super(nexusBan, "Database Manager");
    }

    @Override
    public void register() throws ManagerException {
        // load configuration
        String databaseTypeString = nexusBan.getConfig().getString("type");
        if (databaseTypeString == null) throw new ManagerException("Type of database must be specified in config file");
        try {
            databaseType = DatabaseType.valueOf(databaseTypeString.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ManagerException("Invalid database type!", exception);
        }

        try {
            switch (databaseType) {
                case H2 -> {
                    String filename = nexusBan.getConfig().getString("h2.file");
                    String username = nexusBan.getConfig().getString("h2.username");
                    String password = nexusBan.getConfig().getString("h2.password");
                    H2 h2 = new H2(filename, username, password);
                    SQLConnector sqlConnector;
                    try {
                        sqlConnector = DatabaseAPI.getHikariCP(h2, nexusBan.getLogger());
                    } catch (DatabaseException exception) {
                        throw new ManagerException("Error while initializing H2", exception);
                    }
                    dbUtils = new DBUtils(sqlConnector);
                    nexusBan.getLogger().info("Loaded");
                }
                case MARIADB -> {
                    String host = nexusBan.getConfig().getString("mariadb.host");
                    String username = nexusBan.getConfig().getString("mariadb.username");
                    String password = nexusBan.getConfig().getString("mariadb.password");
                    String database = nexusBan.getConfig().getString("mariadb.database");
                    int port = nexusBan.getConfig().getInt("mariadb.port");
                    MariaDB mariaDB = new MariaDB(host, port, database, username, password);
                    SQLConnector sqlConnector;
                    try {
                        sqlConnector = DatabaseAPI.getHikariCP(mariaDB, nexusBan.getLogger());
                    } catch (DatabaseException exception) {
                        throw new ManagerException("Error while initializing MariaDB", exception);
                    }
                    dbUtils = new DBUtils(sqlConnector);
                }
                case MONGODB -> {
                    connectorType = ConnectorType.MONGO;
                    String uri = nexusBan.getConfig().getString("mongodb.uri");
                    String database = nexusBan.getConfig().getString("mongodb.database");
                    String collection = nexusBan.getConfig().getString("mongodb.collection");
                    MongoDB mongoDB = new MongoDB(uri, database, collection);
                    MongoConnector mongoConnector;
                    try {
                        mongoConnector = DatabaseAPI.getMongoConnector(mongoDB, nexusBan.getLogger());
                    } catch (DatabaseException exception) {
                        throw new ManagerException("Error while initializing MongoDB", exception);
                    }
                    dbUtils = new DBUtils(mongoConnector);
                }
            }
        } catch (DatabaseConfigException exception) {
            throw new ManagerException("Config of database not defined properly!", exception);
        }
    }

    public DBUtils getDbUtils() {
        return dbUtils;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public ConnectorType getConnectorType() {
        return connectorType;
    }
}
