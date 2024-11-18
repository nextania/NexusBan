package cloud.nextflow.nexusban.managers.config;

import cloud.nextflow.nexusban.NexusBan;
import cloud.nextflow.nexusban.database.types.general.DatabaseType;
import cloud.nextflow.nexusban.database.types.mongo.MongoDB;
import cloud.nextflow.nexusban.database.types.sql.H2;
import cloud.nextflow.nexusban.database.types.sql.MariaDB;
import cloud.nextflow.nexusban.exceptions.DatabaseConfigException;
import cloud.nextflow.nexusban.exceptions.ManagerException;
import cloud.nextflow.nexusban.managers.types.NexusManager;

public class ConfigManager extends NexusManager {
    private static ConfigManager configManager;

    public ConfigManager(NexusBan nexusBan) {
        super(nexusBan, "Config Manager");
    }

    @Override
    public void register() throws ManagerException {
        configManager = this;
    }

    public DatabaseType getDatabaseType() throws ManagerException {
        DatabaseType databaseType;
        String databaseTypeString = nexusBan.getConfig().getString("type");
        if (databaseTypeString == null) throw new ManagerException("Type of database must be specified in config file");
        try {
            databaseType = DatabaseType.valueOf(databaseTypeString.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new ManagerException("Invalid database type!", exception);
        }
        return databaseType;
    }

    public H2 getH2Config() throws DatabaseConfigException {
        String filename = nexusBan.getConfig().getString("h2.file");
        if (filename == null || filename.isBlank()) throw new DatabaseConfigException("H2 filepath not defined in config");
        String username = nexusBan.getConfig().getString("h2.username");
        if (username == null || username.isBlank()) throw new DatabaseConfigException("H2 username not defined in config");
        String password = nexusBan.getConfig().getString("h2.password");
        if (password == null || password.isBlank()) throw new DatabaseConfigException("H2 password not defined in config");
        return new H2(filename, username, password);
    }

    public MariaDB getMariaDBConfig() throws DatabaseConfigException {
        String host = nexusBan.getConfig().getString("mariadb.host");
        if (host == null || host.isBlank()) throw new DatabaseConfigException("MariaDB host not defined in config");
        String username = nexusBan.getConfig().getString("mariadb.username");
        if (username == null || username.isBlank()) throw new DatabaseConfigException("MariaDB username not defined in config");
        String password = nexusBan.getConfig().getString("mariadb.password");
        if (password == null || password.isBlank()) throw new DatabaseConfigException("MariaDB password not defined in config");
        String database = nexusBan.getConfig().getString("mariadb.database");
        if (database == null || database.isBlank()) throw new DatabaseConfigException("MariaDB database not defined in config");
        int port = nexusBan.getConfig().getInt("mariadb.port");
        if (port <= 0) throw new DatabaseConfigException("MariaDB port not defined or invalid in config");
        return new MariaDB(host, port, database, username, password);
    }

    public MongoDB getMongoDBConfig() throws DatabaseConfigException {
        String uri = nexusBan.getConfig().getString("mongodb.uri");
        if (uri == null || uri.isBlank()) throw new DatabaseConfigException("MongoDB uri is not defined in config");
        String database = nexusBan.getConfig().getString("mongodb.database");
        if (database == null || database.isBlank()) throw new DatabaseConfigException("MongoDB database is not defined in config");
        String collection = nexusBan.getConfig().getString("mongodb.collection");
        if (collection == null || collection.isBlank()) throw new DatabaseConfigException("MongoDB collection is not defined in config");
        return new MongoDB(uri, database, collection);
    }

    public boolean getVerboseMode() {
        return nexusBan.getConfig().getBoolean("verbose");
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
