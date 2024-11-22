package cloud.nextflow.nexusban.database.types.sql;

import cloud.nextflow.nexusban.database.types.exceptions.DatabaseConfigException;

public class H2 implements SQLType {
    public String file;
    public String user;
    public String password;

    public H2(String file, String user, String password) throws DatabaseConfigException {
        if (file == null) throw new DatabaseConfigException("The file for H2 is not defined in the config.");
        if (user == null) throw new DatabaseConfigException("The username for H2 is not defined in the config.");
        if (password == null) throw new DatabaseConfigException("The password for H2 is not defined in the config.");

        this.file = file;
        this.user = user;
        this.password = password;
    }

    @Override
    public SQLType getType() {
        return this;
    }

    public String toString() {
        return "H2";
    }
}
