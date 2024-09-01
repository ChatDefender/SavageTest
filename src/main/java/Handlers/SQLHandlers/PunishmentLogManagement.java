package Handlers.SQLHandlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.sql.*;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class PunishmentLogManagement {

    public static void filter(ReadyEvent event) {

        try {

            SQLFunctions.verifyConnection();

            String sql = "SELECT * FROM ENDED_PUNISHMENTS";

            // Perform the query to check if the row exists based on the primary key
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                ResultSet resultSet = stmt.executeQuery();

                while (resultSet.next()) {

                    Guild guild = event.getJDA().getGuildById(resultSet.getString("GuildId"));
                    User user = event.getJDA().retrieveUserById(resultSet.getString("UserID")).complete();
                    String punishmentType = resultSet.getString("PunishmentType");
                    Role muteRole = event.getJDA().getRoleById(ConfigurationSettings.getSetting(resultSet.getString("GuildId"), SQLFunctions.Settings.MUTEDROLEID));

                    while (resultSet.next()) {

                        if (punishmentType.equalsIgnoreCase("mute")) {
                            guild.removeRoleFromMember(user, muteRole).queue();
                        } else {
                            guild.unban(user).queue();
                        }

                        insertPunishment(guild.getId(), user.getId(), guild.getSelfMember().getId(), punishmentType.equalsIgnoreCase("mute") ? SQLFunctions.Punishments.UNMUTE : SQLFunctions.Punishments.UNBAN, "0", "Automated Punishment System Action - Time served");

                        markPunishmentAsServed(resultSet.getInt("PunishmentLogId"));

                    }

                }

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    private static void markPunishmentAsServed(int punishmentLogId) {

        try  {

            SQLFunctions.verifyConnection();

            String sql = "UPDATE punishment_logs SET is_served = 1 WHERE punishmentlogid = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, punishmentLogId);

            stmt.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

    public static void markPunishmentAsServed(String userId, SQLFunctions.Punishments punType) {

        try {

            SQLFunctions.verifyConnection();

            String sql = "UPDATE punishment_logs SET is_served = 1 WHERE UserId = ? AND PunishmentType = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            stmt.setString(2, String.valueOf(punType));

            stmt.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    public static boolean isCurrentlyMuted(String GuildId, String UserId) {

        boolean rtnVal = false;

        try  {

            SQLFunctions.verifyConnection();

            String sql = "SELECT count(*) FROM active_punishments WHERE UserId = ? AND PunishmentType = 'MUTE' and GuildId = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, UserId);
            stmt.setString(2, GuildId);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int isMuted = resultSet.getInt(1);
                rtnVal = isMuted >= 1;
            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return rtnVal;

    }

    public static int insertPunishment(String GuildId, String userId, String staffId, SQLFunctions.Punishments punType, String duration, String reason) {

        reason = reason.replace("'", "U+0027").replace("\"", "U+0022");

        int rtnVal = 0;

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_log_management.insert_punishment(?, ?, ?, ?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.registerOutParameter( 1, Types.INTEGER);

                cstmt.setString(2, GuildId);
                cstmt.setString(3, staffId);
                cstmt.setString(4, userId);
                cstmt.setString(5, punType.toString());
                cstmt.setString(6, duration);
                cstmt.setString(7, reason);

                cstmt.execute();

                rtnVal = cstmt.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return rtnVal;

    }

    public static boolean doesLogExists(int primaryKey) {

        boolean rtnVal = false;

        try {

            SQLFunctions.verifyConnection();

            String sql = "select count(*) as countOfLogs from punishment_logs where PunishmentId = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, primaryKey);

            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next())
                rtnVal = resultSet.getInt(1) >= 1;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;

    }

    public static String getRecordUserId(String logId) {


        String rtnVal = null;

        try {

            SQLFunctions.verifyConnection();

            String sql = "SELECT UserId FROM punishment_logs WHERE punishmentid = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, logId);

            // Perform the query to check if the row exists based on the primary key
            ResultSet resultSet = pstmt.executeQuery();

            rtnVal = resultSet.getString("UserId");

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return rtnVal;

    }

    public static String getStaffUserId(String logId) {

        String rtnVal = null;

        try  {

            SQLFunctions.verifyConnection();

            String sql = "SELECT StaffId FROM punishment_logs WHERE PunishmentLogId = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, logId);

            ResultSet resultSet = stmt.executeQuery();

            rtnVal = resultSet.getString("StaffId");

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return rtnVal;

    }

    public static void archiveLog(String logId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call punishment_log_management.archive(?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setString(1, logId);

                cstmt.execute();

            }

        } catch (SQLException se) {

            se.printStackTrace();

        }

    }

    public static void bulkArchive(String GuildId, int isStaff, String UserId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call punishment_log_management.bulk_archive(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setInt(1, isStaff);
                cstmt.setString(2, GuildId);
                cstmt.setString(3, UserId);


                cstmt.execute();

            }

        } catch (SQLException se) {

            se.printStackTrace();

        }

    }

    public static void unarchive(String logId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call punishment_log_management.unarchive(?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setString(1, logId);

                cstmt.execute();

            }

        } catch (SQLException se) {

            se.printStackTrace();

        }

    }

    public static void bulkUnarchive(String GuildId, int isStaff, String UserId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call punishment_log_management.bulk_unarchive(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setInt(1, isStaff);
                cstmt.setString(2, GuildId);
                cstmt.setString(3, UserId);


                cstmt.execute();

            }

        } catch (SQLException se) {

            se.printStackTrace();

        }

    }

    public static String getPunishmentLogs(String GuildId, String userId, boolean isStaff, SQLFunctions.Punishments punType, boolean isArchived) {
        String functionCall;
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            functionCall = "{? = call generatePunishmentLogsFile_function(?, ?, ?, ?, ?)}";

            try (CallableStatement cstmt = conn.prepareCall(functionCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, userId);
                cstmt.setString(3, GuildId);
                cstmt.setInt(4, isStaff ? 1 : 0);
                cstmt.setString(5, !punType.equals(SQLFunctions.Punishments.ALL) ? punType.toString() : null);
                cstmt.setInt(6, isArchived ? 1 : 0);

                cstmt.execute();
                rtnVal = cstmt.getString(1);

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }
}
