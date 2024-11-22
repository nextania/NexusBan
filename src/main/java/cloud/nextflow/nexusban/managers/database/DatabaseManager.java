package cloud.nextflow.nexusban.managers.database;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.database.DBUtils;
import cloud.nextflow.nexusban.database.DatabaseAPI;
import cloud.nextflow.nexusban.database.types.general.ConnectorType;
import cloud.nextflow.nexusban.database.types.general.DatabaseType;
import cloud.nextflow.nexusban.database.types.mongo.MongoConnector;
import cloud.nextflow.nexusban.database.types.mongo.MongoDB;
import cloud.nextflow.nexusban.database.types.sql.H2;
import cloud.nextflow.nexusban.database.types.sql.mysql.MariaDB;
import cloud.nextflow.nexusban.database.types.sql.SQLConnector;
import cloud.nextflow.nexusban.database.types.exceptions.DatabaseConfigException;
import cloud.nextflow.nexusban.database.types.exceptions.DatabaseException;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.config.ConfigManager;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class DatabaseManager extends NexusManager {
    private static DatabaseManager databaseManager;
    private DBUtils dbUtils;
    private DatabaseType databaseType;
    private ConnectorType connectorType = ConnectorType.SQL;
    private ConfigManager configManager;

    public DatabaseManager(NexusBan nexusBan) {
        super(nexusBan, "Database Manager");
    }

    @Override
    public void register() throws ManagerException {
        // load configuration
        databaseManager = this;
        configManager = ConfigManager.getConfigManager();
        databaseType = configManager.getDatabaseType();

        try {
            switch (databaseType) {
                case H2 -> {
                    H2 h2 = configManager.getH2Config();
                    SQLConnector sqlConnector;
                    try {
                        sqlConnector = DatabaseAPI.getHikariCP(h2, nexusBan.getLogger());
                    } catch (DatabaseException exception) {
                        throw new ManagerException("Error while initializing H2", exception);
                    }
                    dbUtils = new DBUtils(sqlConnector);
                }
                case MARIADB -> {
                    MariaDB mariaDB = configManager.getMariaDBConfig();
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
                    MongoDB mongoDB = configManager.getMongoDBConfig();
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

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
