package cloud.nextflow.nexusban.database.types.sql;

import cloud.nextflow.nexusban.database.types.general.DatabaseType;
import cloud.nextflow.nexusban.database.types.general.DBConnector;
import cloud.nextflow.nexusban.database.types.exceptions.DatabaseException;
import cloud.nextflow.nexusban.database.types.sql.mysql.MariaDB;
import cloud.nextflow.nexusban.database.types.sql.mysql.MySQL;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class SQLConnector extends DBConnector {
    private final HikariDataSource hikariCP;
    private final DatabaseType databaseType;

    public SQLConnector(H2 type, Logger logger) throws DatabaseException {
        super(logger);
        databaseType = DatabaseType.H2;
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMinimumIdle(20);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        hikariConfig.setJdbcUrl("jdbc:h2:./" + type.file);
        hikariConfig.addDataSourceProperty("user", type.user);
        hikariConfig.addDataSourceProperty("password", type.password);
        this.hikariCP = new HikariDataSource(hikariConfig);
        if (!this.hikariCP.isClosed()) {
            logger.info("Connected to H2 DB");
        } else {
            throw new DatabaseException("Failed to connect to H2 database. Are the credentials correct?");
        }
        this.initialize();
    }

    public SQLConnector(MariaDB type, Logger logger) throws DatabaseException {
        super(logger);
        databaseType = DatabaseType.MARIADB;
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMinimumIdle(20);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + type.host + ":" + type.port + "/" + type.database);
        hikariConfig.addDataSourceProperty("user", type.user);
        hikariConfig.addDataSourceProperty("password", type.password);
        this.hikariCP = new HikariDataSource(hikariConfig);
        if (!this.hikariCP.isClosed()) {
            logger.info("Connected to MariaDB");
        } else {
            throw new DatabaseException("Failed to connect to MariaDB database. Are credentials correct?");
        }
        this.initialize();
    }

    public SQLConnector(MySQL type, Logger logger) throws DatabaseException {
        super(logger);
        databaseType = DatabaseType.MYSQL;
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMinimumIdle(20);
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        // fix this to reflect mysql jdbc driver
        hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" + type.host + ":" + type.port + "/" + type.database);
        hikariConfig.addDataSourceProperty("user", type.user);
        hikariConfig.addDataSourceProperty("password", type.password);
        this.hikariCP = new HikariDataSource(hikariConfig);
        if (!this.hikariCP.isClosed()) {
            logger.info("Connected to MySQL");
        } else {
            throw new DatabaseException("Failed to connect to MySQL database. Are credentials correct?");
        }
        this.initialize();
    }

    public void closeConnections(PreparedStatement preparedStatement, Connection connection, ResultSet resultSet) throws DatabaseException {
        try {
            if (connection == null) return;
            if (!connection.isClosed()) {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
                connection.close();
            }
        } catch (SQLException exception) {
            throw new DatabaseException("Error when closing connections!", exception);
        }
    }

    public void initialize() throws DatabaseException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getHikariCP().getConnection();
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS sc_ (itemstack LONGBLOB, uuid MEDIUMTEXT)");
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DatabaseException("Error initializing the database", exception);
        } finally {
            closeConnections(preparedStatement, connection, null);
        }
    }

    public HikariDataSource getHikariCP() {
        return this.hikariCP;
    }

    @Override
    public DatabaseType getDatabaseType() {
        return databaseType;
    }
}
