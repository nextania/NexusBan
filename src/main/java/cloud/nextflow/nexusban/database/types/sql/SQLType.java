package cloud.nextflow.nexusban.database.types.sql;

public interface SQLType {
    String user = "";
    String password = "";
    SQLType getType();
}
