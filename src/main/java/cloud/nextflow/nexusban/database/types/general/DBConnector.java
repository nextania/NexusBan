package cloud.nextflow.nexusban.database.types.general;

import java.util.logging.Logger;

public abstract class DBConnector {
    protected Logger logger;

    public DBConnector(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    public abstract DatabaseType getDatabaseType();
}
