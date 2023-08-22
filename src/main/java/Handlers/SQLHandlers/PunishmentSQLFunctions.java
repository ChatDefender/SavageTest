package Handlers.SQLHandlers;

import java.sql.*;
public class PunishmentSQLFunctions {

    static String url = "jdbc:sqlite:punishments.db";

    public static void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS PunishmentLogs (" +
                "PunishmentId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "PunishmentType TEXT NOT NULL," +
                "UserId INTEGER NOT NULL," +
                "StaffId INTEGER NOT NULL," +
                "Duration TEXT NOT NULL," +
                "Reason TEXT NOT NULL," +
                "DateExecuted TEXT DEFAULT (datetime('now'))," +
                "isArchived INTEGER DEFAULT (0))";

        BasicSQLFunctions.runStmt(url, sql);

    }

    public static void insertPunishment(String punType, String userId, String staffId, String duration, String reason) {

        String sql = "INSERT INTO PunishmentLogs (PunishmentType, UserId, StaffId, Duration, Reason) VALUES ('"+punType+"', '"+userId+"', '"+staffId+"', '"+duration+"', '"+reason.replace("\"", "\\\"").replace("'", "\\'")+"')";

        BasicSQLFunctions.runStmt(url, sql);

    }

    public static boolean doesLogExists(int primaryKey) {

        String sql = "SELECT * FROM PunishmentLogs WHERE PunishmentId = '"+primaryKey+"'";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            // Perform the query to check if the row exists based on the primary key
            ResultSet resultSet = statement.executeQuery(sql);

            // Check if the ResultSet contains any rows
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // return false if an error exists.
        return false;
    }

    public static void archiveLog(String logId) {

        String sql = "UPDATE PunishmentLogs SET isArchived = 1 WHERE PunishmentId = '"+logId+"'";

        BasicSQLFunctions.runQuery(url, sql);

    }

    public static void bulkArchive(String UserId) {

        String sql = "UPDATE PunishmentLogs Set isArchived = 1 WHERE UserId = '"+UserId+"'";

        BasicSQLFunctions.runStmt(url, sql);


    }


    public static void getPunishmentLogsForUser(String UserId, String punType, boolean isArchived) {

        int archived = isArchived ? 1 : 0;

        String sql;
        if (punType.equals("all")) {
            sql = "SELECT PunishmentId, PunishmentType, StaffId, Duration, Reason FROM PunishmentLogs WHERE UserID = '"+UserId+"' AND isArchived = '"+archived+"'";
        } else {
            sql = "SELECT PunishmentId, PunishmentType, StaffId, Duration, Reason FROM PunishmentLogs WHERE UserID = '"+UserId+"' AND PunishmentType = '"+punType+"' AND isArchived = '"+archived+"'";
        }

        BasicSQLFunctions.runQuery(url, sql);

    }

    public static void getPunishmentLogsForStaff(String UserId, String punType, boolean isArchived) {

        int archived = isArchived ? 1 : 0;

        String sql;
        if(punType.equals("all")) {
            sql = "SELECT PunishmentId, PunishmentType, UserId, Duration, Reason FROM PunishmentLogs WHERE StaffId = '"+UserId+"' AND isArchived = '"+archived+"'";
        } else {
            sql = "SELECT PunishmentId, PunishmentType, UserId, Duration, Reason FROM PunishmentLogs WHERE StaffId = '" + UserId + "' AND PunishmentType = '" + punType + "'AND isArchived = '" + archived + "'";
        }

        BasicSQLFunctions.runQuery(url, sql);

    }


    public static void recoverLog(String logId) {

        String sql = "UPDATE PunishmentLogs SET isArchived = 0 WHERE PunishmentId = '"+logId+"'";

        BasicSQLFunctions.runStmt(url, sql);

    }
}
