package cloud.nextflow.nexusban.exceptions;

public class DatabaseConfigException extends DatabaseException {
    public DatabaseConfigException(Throwable cause) {
        super(cause);
    }

    public DatabaseConfigException(String message) {
        super(message);
    }

    public DatabaseConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
