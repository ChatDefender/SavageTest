package Handlers.SQLHandlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.sql.*;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class PunishmentLogManagement {

    public static void filterEndedPunishments(ReadyEvent event) {

        try {

            SQLFunctions.verifyConnection();

            String sql = "SELECT * FROM ENDED_PUNISHMENTS";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                ResultSet resultSet = stmt.executeQuery();

                while (resultSet.next()) {

                    Guild guild = event.getJDA().getGuildById(resultSet.getString("guild_id"));
                    User user = event.getJDA().retrieveUserById(resultSet.getString("user_id")).complete();
                    String punishmentType = resultSet.getString("punishment_type");
                    Role muteRole = event.getJDA().getRoleById(ConfigurationSettings.getSetting(guild.getId(), SQLFunctions.Settings.MUTE_ROLE_ID));

                    if (punishmentType.equalsIgnoreCase("mute")) {
                        guild.removeRoleFromMember(user, muteRole).queue();
                    } else {
                        guild.unban(user).queue();
                    }

                    insertPunishmentLog(guild.getId(), user.getId(), guild.getSelfMember().getId(), punishmentType.equalsIgnoreCase("mute") ? SQLFunctions.Punishments.UNMUTE : SQLFunctions.Punishments.UNBAN, "0", "Automated Punishment System Action - Time served");

                    markPunishmentAsServed(guild.getId(), resultSet.getString("punishment_log_id"));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void markPunishmentAsServed(String guildId, String punishmentLogId) {

        try {

            SQLFunctions.verifyConnection();

            String sql = "UPDATE punishment_logs SET is_served = 1 WHERE guild_id = ? AND punishment_log_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, guildId);
            stmt.setString(2, punishmentLogId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isUserCurrentlyMuted(String guildId, String userId) {

        boolean rtnVal = false;

        try  {

            SQLFunctions.verifyConnection();

            String sql = "SELECT count(*) FROM active_punishments WHERE user_id = ? AND punishment_type = 'MUTE' AND guild_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, userId);
            stmt.setString(2, guildId);

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

    public static int insertPunishmentLog(String guildId, String userId, String staffId, SQLFunctions.Punishments punishmentType, String duration, String reason) {

        reason = reason.replace("'", "U+0027").replace("\"", "U+0022");

        int rtnVal = 0;

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call PUNISHMENT_LOG_MANAGEMENT.insert_punishment(?, ?, ?, ?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.registerOutParameter(1, Types.INTEGER);

                cstmt.setString(2, guildId);
                cstmt.setString(3, staffId);
                cstmt.setString(4, userId);
                cstmt.setString(5, punishmentType.toString());
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

    public static boolean doesPunishmentLogExist(String guildId, int punishmentLogId) {

        boolean rtnVal = false;

        try {

            SQLFunctions.verifyConnection();

            String sql = "SELECT count(*) AS countOfLogs FROM punishment_logs WHERE punishment_log_id = ? and guild_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setInt(1, punishmentLogId);
            stmt.setString(2, guildId);

            ResultSet resultSet = stmt.executeQuery();

            rtnVal = resultSet.next() && resultSet.getInt(1) >= 1;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;

    }

    public static String getUserIdFromLog(String logId) {

        String rtnVal = null;

        try {

            SQLFunctions.verifyConnection();

            String sql = "SELECT user_id FROM punishment_logs WHERE punishment_log_id = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, logId);

            ResultSet resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                rtnVal = resultSet.getString("user_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;

    }

    public static int getPunishmentLogId(String guildId, String userId, SQLFunctions.Punishments punishmentType ) {

        int rtnVal = 0;

        try  {

            SQLFunctions.verifyConnection();

            String sql = "SELECT punishment_log_id FROM ACTIVE_PUNISHMENTS WHERE guild_id = ? and user_id = ? and punishment_type = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, guildId);
            stmt.setString(2, userId);
            stmt.setString(3, punishmentType.toString());

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                rtnVal = resultSet.getInt("punishment_log_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;

    }

    public static String getStaffIdFromLog(String logId) {

        String rtnVal = null;

        try  {

            SQLFunctions.verifyConnection();

            String sql = "SELECT staff_id FROM punishment_logs WHERE punishment_log_id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, logId);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                rtnVal = resultSet.getString("staff_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;

    }

    public static void archivePunishmentLog(String logId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_LOG_MANAGEMENT.archive(?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setInt(1, Integer.parseInt(logId));

                cstmt.execute();

            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

    }

    public static void bulkArchivePunishments(String guildId, int isStaff, String userId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_LOG_MANAGEMENT.bulk_archive(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, userId);
                cstmt.setInt(3, isStaff);

                cstmt.execute();

            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

    }

    public static void unarchivePunishmentLog(String logId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_LOG_MANAGEMENT.unarchive(?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setInt(1, Integer.parseInt(logId));

                cstmt.execute();

            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

    }

    public static void bulkUnarchivePunishments(String guildId, int isStaff, String userId) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_LOG_MANAGEMENT.bulk_unarchive(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, userId);
                cstmt.setInt(3, isStaff );

                cstmt.execute();

            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

    }

    public static String generatePunishmentLogsFile(String guildId, String userId, boolean isStaff, SQLFunctions.Punishments punishmentType, boolean isArchived) {

        String functionCall = "{ ? = call PUNISHMENT_LOG_MANAGEMENT.generatePunishmentLogsFile_function(?, ?, ?, ?, ?) }";
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall(functionCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, userId);
                cstmt.setString(3, guildId);
                cstmt.setInt(4, isStaff ? 1 : 0);
                cstmt.setString(5, !punishmentType.equals(SQLFunctions.Punishments.ALL) ? punishmentType.toString() : null);
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
