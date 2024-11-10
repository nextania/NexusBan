package cloud.nextflow.nexusban.exceptions;

public class ManagerException extends Exception {
    public ManagerException(Throwable cause) {
        super(cause);
    }

    public ManagerException(String message) {
        super(message);
    }

    public ManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
