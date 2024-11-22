package cloud.nextflow.nexusban.database.types.sql.mysql;

import cloud.nextflow.nexusban.database.types.exceptions.DatabaseConfigException;
import cloud.nextflow.nexusban.database.types.sql.SQLType;

public class MySQL implements SQLType {
    public String host;
    public int port;
    public String database;
    public String user = "";
    public String password = "";

    public MySQL(String host, int port, String database, String user, String password) throws DatabaseConfigException {
        if (host == null) throw new DatabaseConfigException("The host for MySQL isn't defined in the config");
        if (port == 0) throw new DatabaseConfigException("The port for MySQL isn't defined in the config");
        if (database == null) throw new DatabaseConfigException("The database for MySQL isn't defined in the config");
        if (user == null) throw new DatabaseConfigException("The username for MySQL isn't defined in the config");
        if (password == null) throw new DatabaseConfigException("The password for MySQL isn't defined in the config");

        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    public SQLType getType() {
        return this;
    }

    @Override
    public String toString() {
        return "MySQL";
    }
}
