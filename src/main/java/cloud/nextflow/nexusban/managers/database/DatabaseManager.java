package cloud.nextflow.nexusban.managers.database;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.database.DBUtils;
import cloud.nextflow.nexusban.database.DatabaseAPI;
import cloud.nextflow.nexusban.database.types.general.ConnectorType;
import cloud.nextflow.nexusban.database.types.general.DatabaseType;
import cloud.nextflow.nexusban.database.types.mongo.MongoDB;
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
        try {
            databaseType = DatabaseType.valueOf(databaseTypeString);
        } catch (IllegalArgumentException e) {
            throw new ManagerException("Invalid database type!");
        }

        if (databaseType == DatabaseType.MONGODB) {
            connectorType = ConnectorType.MONGO;
            String uri = nexusBan.getConfig().getString("mongodb.uri");
            String database = nexusBan.getConfig().getString("mongodb.database");
            String collection = nexusBan.getConfig().getString("mongodb.collection");
            MongoDB mongoDB = new MongoDB(uri, database, collection);
            try {
                DatabaseAPI.getMongoConnector(mongoDB, nexusBan.getLogger());
            } catch (DatabaseException exception) {
                throw new ManagerException("Error while initializing MongoDB", exception);
            }
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
