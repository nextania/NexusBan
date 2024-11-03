package cloud.nextflow.nexusban.database;

import cloud.nextflow.nexusban.database.types.ConnectorType;
import cloud.nextflow.nexusban.database.types.mongo.MongoConnector;
import cloud.nextflow.nexusban.database.types.sql.SQLConnector;

import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBUtils {
    private MongoConnector mongoConnector;
    private SQLConnector sqlConnector;
    private ConnectorType type;

    public DBUtils(SQLConnector connector) {
        this.sqlConnector = connector;
        this.type = ConnectorType.SQL;
    }

    public DBUtils(MongoConnector connector) {
        this.mongoConnector = connector;
        this.type = ConnectorType.MONGO;
    }

    public ConnectorType getType() {
        return type;
    }

    // Example of insert method
    public void insertExample(String username, String email) {
        if (type == ConnectorType.MONGO) {
            Document insertDocument = new Document()
                    .append("username", username)
                    .append("email", email);
            mongoConnector.getCollection().insertOne(insertDocument);
        } else if (type == ConnectorType.SQL) {
            Connection connection;
            PreparedStatement preparedStatement;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("INSERT INTO users VALUES (?, ?)");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);

                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }
}
