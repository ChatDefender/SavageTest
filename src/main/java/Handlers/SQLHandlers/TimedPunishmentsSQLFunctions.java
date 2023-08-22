package Handlers.SQLHandlers;

import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.sql.*;
import java.util.Objects;

public class TimedPunishmentsSQLFunctions {

    static String url = "jdbc:sqlite:timer.db";

    public static void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS Timer (" +
                "TimerId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "PunishmentType TEXT NOT NULL," +
                "GuildId INTEGER NOT NULL," +
                "UserId INTEGER NOT NULL," +
                "EndTime INTEGER NOT NULL)";

        BasicSQLFunctions.runStmt(url, sql);

    }

    // Now we want to create a method that will select all that are overdue, if it's must, unmute, if its ban, unban

    public static void filter(ReadyEvent event) {

        String sql = "SELECT * FROM Timer WHERE EndTime >= " + System.currentTimeMillis();

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            // Perform the query to check if the row exists based on the primary key
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {

                if (resultSet.getString("PunishmentType").equalsIgnoreCase("mute")) {

                    Objects.requireNonNull(event.getJDA().getGuildById(resultSet.getString("GuildId"))).removeRoleFromMember(event.getJDA().retrieveUserById(resultSet.getString("UserID")).complete(), Objects.requireNonNull(event.getJDA().getRoleById(ConfigurationSQLFunctions.getSetting("MuteRoleId")))).complete();

                } else {

                    event.getJDA().getGuildById(resultSet.getString("GuildID")).unban(event.getJDA().retrieveUserById(resultSet.getString("UserId")).complete());

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void insertTime(String PunishmentType, String GuildId, String UserId, String EndTime) {

        String sql = "INSERT INTO Timer (PunishmentType, GuildId, UserId, EndTime) VALUES('"+PunishmentType+"', '"+GuildId+"', '"+UserId+"', '"+EndTime+"',)";

        BasicSQLFunctions.runStmt(url, sql);

    }

    public static boolean isCurrentlyMuted(String UserId) {

        String sql = "SELECT * FROM Timer WHERE UserId = " + UserId + " AND PunishmentType = 'mute'";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            // Perform the query to check if the row exists based on the primary key
            ResultSet resultSet = statement.executeQuery(sql);

            // Check if the ResultSet contains any rows
            return resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }

}
