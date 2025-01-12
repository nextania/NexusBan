package cloud.nextflow.nexusban.database;

import cloud.nextflow.nexusban.database.types.general.ConnectorType;
import cloud.nextflow.nexusban.database.types.mongo.MongoConnector;
import cloud.nextflow.nexusban.database.types.sql.SQLConnector;

import cloud.nextflow.nexusban.database.types.exceptions.DatabaseException;
import cloud.nextflow.nexusban.types.Punishment;
import cloud.nextflow.nexusban.types.PunishmentType;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {
    private MongoConnector mongoConnector;
    private SQLConnector sqlConnector;
    private final ConnectorType type;

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

    public String createPunishment(PunishmentType punishmentType, String punishedUUID, String punisherUUID, String reason, long startDate, long endDate, String duration, boolean permanent) throws DatabaseException {
        Punishment toFindPunishment = findPunishment(punishedUUID, punishmentType);
        if (toFindPunishment != null) {
            if (toFindPunishment.isPermanent()) throw new DatabaseException("There already exists a permanent " +
                    punishmentType.name() + " for the user!");
            if (toFindPunishment.getEndDate() > toFindPunishment.getStartDate()) {
                throw new DatabaseException("There already exists a temporary " +
                        punishmentType.name() + " for the user!");
            } else {
                deletePunishment(toFindPunishment.getCaseID());
            }
        }
        String caseID = generateCaseID(12);
        if (type == ConnectorType.MONGO) {
            Document punishmentDocument = new Document()
                    .append("punishmentType", punishmentType.name())
                    .append("caseID", caseID)
                    .append("punishedUUID", punishedUUID)
                    .append("punisherUUID", punisherUUID)
                    .append("reason", reason)
                    .append("startDate", startDate)
                    .append("endDate", endDate)
                    .append("permanent", permanent)
                    .append("duration", duration);
            mongoConnector.getCollection().insertOne(punishmentDocument);
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("INSERT INTO punishments VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                preparedStatement.setString(1, punishmentType.name());
                preparedStatement.setString(2, caseID);
                preparedStatement.setString(3, punishedUUID);
                preparedStatement.setString(4, punisherUUID);
                preparedStatement.setString(5, reason);
                preparedStatement.setTimestamp(6, Timestamp.from(Instant.ofEpochMilli(startDate)));
                preparedStatement.setTimestamp(7, Timestamp.from(Instant.ofEpochMilli(endDate)));
                preparedStatement.setBoolean(8, permanent);
                preparedStatement.setString(9, duration);

                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                throw new DatabaseException("Error with inserting in SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, null);
            }
        }

        return caseID;
    }

    public Punishment findPunishment(String caseID) throws DatabaseException {
        Punishment punishment = null;
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("caseID", caseID);
            Document foundPunishment = mongoConnector.getCollection().find(document).first();
            if (foundPunishment == null) return null;
            PunishmentType punishmentType = PunishmentType.valueOf(foundPunishment.getString("punishmentType"));
            String punishedUUID = foundPunishment.getString("punishedUUID");
            punishment = convertToPunishment(punishedUUID, foundPunishment, punishmentType, caseID);
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE caseID = ?");
                preparedStatement.setString(1, caseID);

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    PunishmentType punishmentType = PunishmentType.valueOf(resultSet.getString("punishmentType"));
                    String punishedUUID = resultSet.getString("punishedUUID");
                    punishment = findPunishmentConverter(punishedUUID, caseID, punishmentType, resultSet);
                }
            } catch (SQLException exception) {
                throw new DatabaseException("Error with finding punishment in SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishment;
    }

    public Punishment findPunishment(String punishedUUID, PunishmentType punishmentType) throws DatabaseException {
        Punishment punishment = null;
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punishedUUID", punishedUUID)
                    .append("punishmentType", punishmentType.name());
            Document foundResult = mongoConnector.getCollection().find(document).first();
            if (foundResult == null) return null;
            String caseID = foundResult.getString("caseID");
            punishment = convertToPunishment(punishedUUID, foundResult, punishmentType, caseID);
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punishedUUID = ? AND punishmentType = ?");
                preparedStatement.setString(1, punishedUUID);
                preparedStatement.setString(2, punishmentType.name());

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String caseID = resultSet.getString("caseID");
                    punishment = findPunishmentConverter(punishedUUID, caseID, punishmentType, resultSet);
                }
            } catch (SQLException exception) {
                throw new DatabaseException("Error in finding a punishment with punishedUUID and punishmentType in SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishment;
    }

    private Punishment findPunishmentConverter(String punishedUUID, String caseID, PunishmentType punishmentType, ResultSet resultSet) throws DatabaseException, SQLException {
        Punishment punishment;
        String punisherUUID = resultSet.getString("punisherUUID");
        String reason = resultSet.getString("reason");
        Timestamp startDateTimestamp = resultSet.getTimestamp("startDate");
        if (resultSet.getBoolean("permanent")) {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason,
                    startDateTimestamp.toInstant().toEpochMilli(), false);
        } else {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason,
                    startDateTimestamp.toInstant().toEpochMilli(), resultSet.getTimestamp("endDate").toInstant().toEpochMilli(),
                    resultSet.getString("duration"), false);
        }
        return punishment;
    }

    public List<Punishment> getHistory(String punishedUUID) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punishedUUID", punishedUUID);
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).cursor()) {
                documentToPunishment(punishedUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punishedUUID = ?" +
                        " ORDER BY startDate DESC");
                preparedStatement.setString(1, punishedUUID);

                resultSet = preparedStatement.executeQuery();
                resultToPunishment(punishedUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getHistory(String punishedUUID, int limit) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punishedUUID", punishedUUID);
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).limit(limit).cursor()) {
                documentToPunishment(punishedUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punishedUUID = ?" +
                        " ORDER BY startDate DESC LIMIT ?");
                preparedStatement.setString(1, punishedUUID);
                preparedStatement.setInt(2, limit);

                resultSet = preparedStatement.executeQuery();
                resultToPunishment(punishedUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history with limit SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getStaffHistory(String punisherUUID) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punisherUUID", punisherUUID);
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).cursor()) {
                documentToPunishmentStaff(punisherUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punisherUUID = ?" +
                        " ORDER BY startDate DESC");
                preparedStatement.setString(1, punisherUUID);

                resultSet = preparedStatement.executeQuery();
                resultToPunishmentStaff(punisherUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getStaffHistory(String punisherUUID, int limit) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punisherUUID", punisherUUID);
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).limit(limit).cursor()) {
                documentToPunishmentStaff(punisherUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punisherUUID = ?" +
                        " ORDER BY startDate DESC LIMIT ?");
                preparedStatement.setString(1, punisherUUID);
                preparedStatement.setInt(2, limit);

                resultSet = preparedStatement.executeQuery();
                resultToPunishmentStaff(punisherUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history with limit SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getHistory(String punishedUUID, PunishmentType punishmentType) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punishedUUID", punishedUUID)
                    .append("punishmentType", punishmentType.name());
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).cursor()) {
                documentToPunishment(punishedUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punishedUUID = ?" +
                        " AND punishmentType = ? ORDER BY startDate DESC");
                preparedStatement.setString(1, punishedUUID);
                preparedStatement.setString(2, punishmentType.name());

                resultSet = preparedStatement.executeQuery();
                resultToPunishment(punishedUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getHistory(String punishedUUID, PunishmentType punishmentType, int limit) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punishedUUID", punishedUUID)
                    .append("punishmentType", punishmentType.name());
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).limit(limit).cursor()) {
                documentToPunishment(punishedUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punishedUUID = ?" +
                        " AND punishmentType = ? ORDER BY startDate DESC LIMIT ?");
                preparedStatement.setString(1, punishedUUID);
                preparedStatement.setString(2, punishmentType.name());
                preparedStatement.setInt(3, limit);

                resultSet = preparedStatement.executeQuery();
                resultToPunishment(punishedUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history with limit SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getStaffHistory(String punisherUUID, PunishmentType punishmentType) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punisherUUID", punisherUUID)
                    .append("punishmentType", punishmentType.name());
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).cursor()) {
                documentToPunishmentStaff(punisherUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punisherUUID = ?" +
                        " AND punishmentType = ? ORDER BY startDate DESC");
                preparedStatement.setString(1, punisherUUID);
                preparedStatement.setString(2, punishmentType.name());

                resultSet = preparedStatement.executeQuery();
                resultToPunishmentStaff(punisherUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getStaffHistory(String punisherUUID, PunishmentType punishmentType, int limit) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punisherUUID", punisherUUID)
                    .append("punishmentType", punishmentType.name());
            try (MongoCursor<Document> mongoCursor = mongoConnector.getCollection().find(document).sort(Sorts.descending("startDate")).limit(limit).cursor()) {
                documentToPunishmentStaff(punisherUUID, punishmentList, mongoCursor);
            }
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE punisherUUID = ?" +
                        " AND punishmentType = ? ORDER BY startDate DESC LIMIT ?");
                preparedStatement.setString(1, punisherUUID);
                preparedStatement.setString(2, punishmentType.name());
                preparedStatement.setInt(3, limit);

                resultSet = preparedStatement.executeQuery();
                resultToPunishmentStaff(punisherUUID, punishmentList, resultSet);
            } catch (SQLException exception) {
                throw new DatabaseException("Error in getting history with limit SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public List<Punishment> getAllPunishments(boolean permanent) throws DatabaseException {
        List<Punishment> punishmentList = new ArrayList<>();

        if (type == ConnectorType.MONGO) {
            Document findCondition = new Document()
                    .append("permanent", permanent);
            try (MongoCursor<Document> mongoCursor = permanent ? (mongoConnector.getCollection().find(findCondition).cursor()) : (
                    mongoConnector.getCollection().find(findCondition).sort(Sorts.descending("startDate")).cursor())) {
                while (mongoCursor.hasNext()) {
                    Document result = mongoCursor.next();
                    PunishmentType punishmentType = PunishmentType.valueOf(result.getString("punishmentType"));
                    String punishedUUID = result.getString("punishedUUID");
                    String caseID = result.getString("caseID");
                    Punishment punishment = convertToPunishment(punishedUUID, result, punishmentType, caseID);
                    punishmentList.add(punishment);
                }
            }

        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("SELECT * FROM punishments WHERE permanent = ? ORDER BY startDate DESC");
                preparedStatement.setBoolean(1, permanent);

                resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    PunishmentType punishmentType = PunishmentType.valueOf(resultSet.getString("punishmentType"));
                    String punishedUUID = resultSet.getString("punishedUUID");
                    String caseID = resultSet.getString("caseID");
                    Punishment punishment = convertToPunishment(punishedUUID, resultSet, punishmentType, caseID);
                    punishmentList.add(punishment);
                }
            } catch (SQLException exception) {
                throw new DatabaseException("Could not find all punishments in SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, resultSet);
            }
        }
        return punishmentList;
    }

    public void deleteExpiredPunishments() throws DatabaseException {
        if (type == ConnectorType.MONGO) {
            Bson deleteFilter = Filters.and(Filters.eq("permanent", false), Filters.lte("endDate", Instant.now().toEpochMilli()));
            mongoConnector.getCollection().deleteMany(deleteFilter);
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("DELETE FROM punishments WHERE permanent = false AND endDate <= NOW()");

                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                throw new DatabaseException("Error in deleting expired punishments in SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, null);
            }
        }
    }

    public void deletePunishment(String caseID) throws DatabaseException {
        if (findPunishment(caseID) == null) throw new DatabaseException("There is no punishment with that caseID");

        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("caseID", caseID);
            mongoConnector.getCollection().deleteOne(document);
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("DELETE FROM punishments WHERE caseID = ?");
                preparedStatement.setString(1, caseID);

                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                throw new DatabaseException("Can not delete punishment in SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, null);
            }
        }
    }

    public void deleteStaffPunishments(String punisherUUID) throws DatabaseException {
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punisherUUID", punisherUUID);
            mongoConnector.getCollection().deleteMany(document);
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("DELETE FROM punishments WHERE punisherUUID = ?");
                preparedStatement.setString(1, punisherUUID);

                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                throw new DatabaseException("Error in deleting staff punishments SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, null);
            }
        }
    }

    public void deletePunishments(String punishedUUID) throws DatabaseException {
        if (type == ConnectorType.MONGO) {
            Document document = new Document()
                    .append("punishedUUID", punishedUUID);
            mongoConnector.getCollection().deleteMany(document);
        } else if (type == ConnectorType.SQL) {
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                connection = sqlConnector.getHikariCP().getConnection();

                preparedStatement = connection.prepareStatement("DELETE FROM punishments WHERE punishedUUID = ?");
                preparedStatement.setString(1, punishedUUID);

                preparedStatement.executeUpdate();
            } catch (SQLException exception) {
                throw new DatabaseException("Error in deleting punishments SQL", exception);
            } finally {
                sqlConnector.closeConnections(preparedStatement, connection, null);
            }
        }
    }

    private void documentToPunishment(String punishedUUID, List<Punishment> punishmentList, MongoCursor<Document> mongoCursor) throws DatabaseException {
        while (mongoCursor.hasNext()) {
            Document foundDocument = mongoCursor.next();
            PunishmentType punishmentType = PunishmentType.valueOf(foundDocument.getString("punishmentType"));
            String caseID = foundDocument.getString("caseID");
            Punishment punishment = convertToPunishment(punishedUUID, foundDocument, punishmentType, caseID);
            punishmentList.add(punishment);
        }
    }

    private void resultToPunishment(String punishedUUID, List<Punishment> punishmentList, ResultSet resultSet) throws DatabaseException, SQLException {
        while (resultSet.next()) {
            PunishmentType punishmentType = PunishmentType.valueOf(resultSet.getString("punishmentType"));
            String caseID = resultSet.getString("caseID");
            Punishment punishment = convertToPunishment(punishedUUID, resultSet, punishmentType, caseID);
            punishmentList.add(punishment);
        }
    }

    private void documentToPunishmentStaff(String punisherUUID, List<Punishment> punishmentList, MongoCursor<Document> mongoCursor) throws DatabaseException {
        while (mongoCursor.hasNext()) {
            Document foundDocument = mongoCursor.next();
            PunishmentType punishmentType = PunishmentType.valueOf(foundDocument.getString("punishmentType"));
            String caseID = foundDocument.getString("caseID");
            Punishment punishment = convertToPunishmentStaff(punisherUUID, foundDocument, punishmentType, caseID);
            punishmentList.add(punishment);
        }
    }

    private void resultToPunishmentStaff(String punisherUUID, List<Punishment> punishmentList, ResultSet resultSet) throws DatabaseException, SQLException {
        while (resultSet.next()) {
            PunishmentType punishmentType = PunishmentType.valueOf(resultSet.getString("punishmentType"));
            String caseID = resultSet.getString("caseID");
            Punishment punishment = convertToPunishmentStaff(punisherUUID, resultSet, punishmentType, caseID);
            punishmentList.add(punishment);
        }
    }

    private Punishment convertToPunishment(String punishedUUID, Document foundDocument, PunishmentType punishmentType, String caseID) throws DatabaseException {
        Punishment punishment;
        String punisherUUID = foundDocument.getString("punisherUUID");
        String reason = foundDocument.getString("reason");
        long startDate = foundDocument.getLong("startDate");

        if (foundDocument.getBoolean("permanent")) {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    false);
        } else {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    foundDocument.getLong("endDate"), foundDocument.getString("duration"), false);
        }
        return punishment;
    }

    private Punishment convertToPunishment(String punishedUUID, ResultSet resultSet, PunishmentType punishmentType, String caseID) throws DatabaseException, SQLException {
        Punishment punishment;
        String punisherUUID = resultSet.getString("punisherUUID");
        String reason = resultSet.getString("reason");
        long startDate = resultSet.getTimestamp("startDate").toInstant().toEpochMilli();

        if (resultSet.getBoolean("permanent")) {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    false);
        } else {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    resultSet.getTimestamp("endDate").toInstant().toEpochMilli(),
                    resultSet.getString("duration"), false);
        }
        return punishment;
    }

    private Punishment convertToPunishmentStaff(String punisherUUID, Document foundDocument, PunishmentType punishmentType, String caseID) throws DatabaseException {
        Punishment punishment;
        String punishedUUID = foundDocument.getString("punishedUUID");
        String reason = foundDocument.getString("reason");
        long startDate = foundDocument.getLong("startDate");

        if (foundDocument.getBoolean("permanent")) {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    false);
        } else {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    foundDocument.getLong("endDate"), foundDocument.getString("duration"), false);
        }
        return punishment;
    }

    private Punishment convertToPunishmentStaff(String punisherUUID, ResultSet resultSet, PunishmentType punishmentType, String caseID) throws DatabaseException, SQLException {
        Punishment punishment;
        String punishedUUID = resultSet.getString("punishedUUID");
        String reason = resultSet.getString("reason");
        long startDate = resultSet.getTimestamp("startDate").toInstant().toEpochMilli();

        if (resultSet.getBoolean("permanent")) {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    false);
        } else {
            punishment = new Punishment(punishmentType, caseID, punishedUUID, punisherUUID, reason, startDate,
                    resultSet.getTimestamp("endDate").toInstant().toEpochMilli(),
                    resultSet.getString("duration"), false);
        }
        return punishment;
    }

    private String generateCaseID(int length) throws DatabaseException {
        String caseID = "";
        while (findPunishment(caseID) != null || caseID.isEmpty()) {
            String characterSet = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
            StringBuilder caseIDBuilder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                caseIDBuilder.append(characterSet.charAt((int) (Math.random() * characterSet.length())));
            }
            caseID = caseIDBuilder.toString();
        }
        return caseID;
    }
}
