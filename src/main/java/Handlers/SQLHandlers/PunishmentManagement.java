package Handlers.SQLHandlers;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class PunishmentManagement {

    public static String createPunishment(String guildId, String punishmentName, int createUserId) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.create_punishment(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);
                cstmt.setInt(4, createUserId);

                cstmt.execute();
                rtnVal = cstmt.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

    public static String deletePunishment(String guildId, String punishmentName) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.delete_punishment(?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);

                cstmt.execute();
                rtnVal = cstmt.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

    public static String createTier(String guildId, String punishmentName, String punishmentType, int duration) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.create_tier(?, ?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);
                cstmt.setString(4, punishmentType);
                cstmt.setInt(5, duration);

                cstmt.execute();
                rtnVal = cstmt.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

    public static String removeTier(String guildId, String punishmentName, int punishmentTier) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.remove_tier(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);
                cstmt.setInt(4, punishmentTier);

                cstmt.execute();
                rtnVal = cstmt.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

    public static String editTier(String guildId, String punishmentName, int punishmentTier, String columnName, String newValue) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.edit_tier(?, ?, ?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);
                cstmt.setInt(4, punishmentTier);
                cstmt.setString(5, columnName);
                cstmt.setString(6, newValue);

                cstmt.execute();
                rtnVal = cstmt.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

    public static String renamePunishment(String guildId, String oldName, String newName) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.rename_punishment(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, oldName);
                cstmt.setString(4, newName);

                cstmt.execute();
                rtnVal = cstmt.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;
    }

}
