package Handlers.SQLHandlers;

import java.sql.*;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class PunishmentManagement {

    public static String createPunishment(String guildId, String punishmentName, String createUserId) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_MANAGEMENT.create_punishment(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.setString(1, guildId);
                cstmt.setString(2, punishmentName.toUpperCase());
                cstmt.setString(3, createUserId);

                cstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

    public static void deletePunishment(String guildId, String punishmentName) {

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_MANAGEMENT.delete_punishment(?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.setString(1, guildId);
                cstmt.setString(2, punishmentName.toUpperCase());

                cstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createPunishmentTier(String guildId, String punishmentName, String punishmentType, long duration) {

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_MANAGEMENT.create_tier(?, ?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.setString(1, guildId);
                cstmt.setString(2, punishmentName.toUpperCase());
                cstmt.setString(3, punishmentType.toUpperCase());
                cstmt.setLong(4, duration);

                cstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removePunishmentTier(String guildId, String punishmentName, int punishmentTier) {

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_MANAGEMENT.remove_tier(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.setString(1, guildId);
                cstmt.setString(2, punishmentName.toUpperCase());
                cstmt.setInt(3, punishmentTier);

                cstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void editPunishmentTier(String guildId, String punishmentName, int punishmentTier, String columnName, String newValue) {

        System.out.println(newValue);

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_MANAGEMENT.edit_tier(?, ?, ?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.setString(1, guildId);
                cstmt.setString(2, punishmentName.toUpperCase());
                cstmt.setInt(3, punishmentTier);
                cstmt.setString(4, columnName.toLowerCase());
                cstmt.setString(5, newValue);

                cstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void renamePunishment(String guildId, String oldName, String newName) {

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_MANAGEMENT.rename_punishment(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.setString(1, guildId);
                cstmt.setString(2, oldName.toUpperCase());
                cstmt.setString(3, newName.toUpperCase());

                cstmt.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int checkPunishmentExistence(String guildId, String punishmentName) {

        int rtnVal = 0;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call PUNISHMENT_MANAGEMENT.does_punishment_exist(?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName.toUpperCase());

                cstmt.execute();
                rtnVal = cstmt.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

    public static String getPunishmentTypeFromActive(String guildId, String userId) {

        String punishmentType = "";

        String sql = "select pt.punishment_type as punishment_type from punishment_active pa inner join punishment_tier pt on pa.guild_id = pt.guild_id and pa.punishment_id = pt.punishment_id and pa.punishment_tier = pt.punishment_tier where pa.guild_id = ? and pa.user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guildId);
            stmt.setString(2, userId); // Ideally, handle roleIds as a list/array

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                punishmentType = resultSet.getString("punishment_type");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishmentType;

    }

    public static void addActivePunishmentLog(String guildId, String userId, String punishmentName) {

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ call PUNISHMENT_MANAGEMENT.add_active_punishment(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.setString(1, guildId);
                cstmt.setString(2, userId);
                cstmt.setString(3, punishmentName.toUpperCase());

                cstmt.execute();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static String getDurationFromActive(String guildId, String userId) {
        String punishmentType = "";

        String sql = "select pt.punishment_time as punishment_type from punishment_active pa inner join punishment_tier pt on pa.guild_id = pt.guild_id and pa.punishment_id = pt.punishment_id and pa.punishment_tier = pt.punishment_tier where pa.guild_id = ? and pa.user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guildId);
            stmt.setString(2, userId); // Ideally, handle roleIds as a list/array

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                punishmentType = resultSet.getString("punishment_type");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return punishmentType;

    }
}
