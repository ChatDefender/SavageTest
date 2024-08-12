package Handlers.SQLHandlers;

import org.json.JSONObject;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class PunishmentManagement {

    public static String createPunishment(String guildId, String punishmentName, String createUserId) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.create_punishment(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);
                cstmt.setString(4, createUserId);

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

    public static String createTier(String guildId, String punishmentName, String punishmentType, long duration) {
        String rtnVal = null;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.create_tier(?, ?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);
                cstmt.setString(4, punishmentType);
                cstmt.setLong(5, duration);

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

    public static int doesPunishmentExist(String guildId, String punishmentName) {

        punishmentName = punishmentName.toUpperCase();

        int rtnVal = 0;

        try {
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.does_punishment_exist(?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, punishmentName);

                cstmt.execute();
                rtnVal = cstmt.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;

    }

    public static JSONObject addActivePunishment(String guildId, String userId, String punishmentName) {

        punishmentName = punishmentName.toUpperCase(); // Ensure punishment name is uppercase

        JSONObject resultJson = new JSONObject();

        try {
            // Assuming you have a method that verifies and returns a valid DB connection
            SQLFunctions.verifyConnection();

            String procCall = "{ ? = call punishment_management.add_active_punishment(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {
                // Register the return type (CLOB in this case)
                cstmt.registerOutParameter(1, Types.CLOB);

                // Set the input parameters for the PL/SQL function
                cstmt.setString(2, guildId);
                cstmt.setString(3, userId);
                cstmt.setString(4, punishmentName);

                // Execute the function
                cstmt.execute();

                // Get the return value (CLOB)
                String jsonResult = cstmt.getString(1);

                // Convert the JSON string to JSONObject
                resultJson = new JSONObject(jsonResult);

            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultJson.put("errorMessage", "Error: " + e.getMessage()); // Return the error message if an exception occurs
        }

        return resultJson;
    }

}
