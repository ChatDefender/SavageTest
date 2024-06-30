package Handlers.SQLHandlers;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.session.ReadyEvent;

import java.sql.*;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class PunishmentManagement {

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
                    Role muteRole = event.getJDA().getRoleById(ConfigurationSettings.getSetting(resultSet.getString("GuildId"), SQLFunctions.Settings.MUTEDROLEID));

                    while (resultSet.next()) {

                        if (resultSet.getString("PunishmentType").equalsIgnoreCase("mute")) {
                            guild.removeRoleFromMember(user, muteRole).queue();
                        } else {
                            guild.unban(user).queue();
                        }
                    }

                }
            }

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

    public static String getPunishmentLogsForUser(String GuildId, String UserId, SQLFunctions.Punishments punType, boolean showAll) {

        String functionCall;

        if (showAll && punType.equals(SQLFunctions.Punishments.ALL)) {
            // if show all, we do not care about the isArchived
            functionCall = "{? = call punishment_log_management.GETPUNISHMENTLOGSFORUSER(?, ?)}";
        } else if (showAll) {
            // If show all, we do not care about the isArchived
            functionCall = "{? = call punishment_log_management.GETPUNISHMENTTYPELOGSFORUSER(?, ?, ?)}";
        } else if(punType.equals(SQLFunctions.Punishments.ALL)) {
            functionCall = "{? = call punishment_log_management.GETARCHIVEDPUNISHMENTLOGSFORUSER(?, ?)}";
        } else {
            functionCall = "{? = call punishment_log_management.GETARCHIVEDPUNISHMENTTYPELOGSFORUSER(?, ?, ?)}";
        }

        String rtnVal = null;

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall(functionCall)) {

                cstmt.registerOutParameter( 1, Types.CLOB);

                cstmt.setString(2, GuildId);
                cstmt.setString(3, UserId);

                int count = 0;
                for(int i = 0; i < functionCall.length(); i++) {

                    if (functionCall.charAt(i) == '?')
                        count++;

                }

                if (count == 4) {

                    cstmt.setString(4, punType.toString());

                }

                cstmt.execute();

                rtnVal = cstmt.getString(1);

            }

        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
            e.printStackTrace();
        }


        return rtnVal;
    }

    public static String getPunishmentLogsForStaff(String GuildId, String StaffId, SQLFunctions.Punishments punType, boolean showAll) {

        String functionCall;

        if (showAll && punType.equals(SQLFunctions.Punishments.ALL)) {
            // if show all, we do not care about the isArchived
            functionCall = "{? = call punishment_log_management.GETPUNISHMENTLOGSFORSTAFF(?, ?)}";
        } else if (showAll) {
            // If show all, we do not care about the isArchived
            functionCall = "{? = call punishment_log_management.GETPUNISHMENTTYPELOGSFORSTAFF(?, ?, ?)}";
        } else if(punType.equals(SQLFunctions.Punishments.ALL)) {
            functionCall = "{? = call punishment_log_management.GETARCHIVEDPUNISHMENTLOGSFORSTAFF(?, ?)}";
        } else {
            functionCall = "{? = call punishment_log_management.GETARCHIVEDPUNISHMENTTYPELOGSFORSTAFF(?, ?, ?)}";
        }

        String rtnVal = null;

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall(functionCall)) {

                cstmt.registerOutParameter( 1, Types.VARCHAR);

                cstmt.setString(2, GuildId);
                cstmt.setString(3, StaffId);

                int count = 0;
                for(int i = 0; i < functionCall.length(); i++) {

                    if (functionCall.charAt(i) == '?')
                        count++;

                }

                if (count == 4) {

                    cstmt.setString(4, punType.toString());

                }

                cstmt.execute();

                rtnVal = cstmt.getString(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return rtnVal;
    }

}
