package Handlers.SQLHandlers;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class ConfigurationSettings {

    public static void updateSettings(String guildId, SQLFunctions.Settings setting, String newValue) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call CONFIGURATION_MANAGEMENT.update_settings(?, ?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, setting.toString());
                cstmt.setString(3, newValue);

                cstmt.execute();
            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

    }

    public static String getSetting(String guildId, SQLFunctions.Settings setting) {

        String rtnVal = "";

        try {

            SQLFunctions.verifyConnection();

            String functionCall = "{ ? = call CONFIGURATION_MANAGEMENT.get_setting(?, ?) }";

            try (CallableStatement cstmt = conn.prepareCall(functionCall)) {

                cstmt.registerOutParameter(1, Types.VARCHAR);
                cstmt.setString(2, guildId);
                cstmt.setString(3, setting.toString());

                cstmt.execute();

                rtnVal = cstmt.getString(1);

            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

        return rtnVal;
    }

    public static void verifyGuildSetting(String guildId) {

        try {

            SQLFunctions.verifyConnection();
            String procCall = "{ call CONFIGURATION_MANAGEMENT.verify_guild_setting(?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setString(1, guildId);

                cstmt.execute();

            }

        } catch (SQLException se) {
            se.printStackTrace();
        }

    }

    public static void removeMutedRole(String guildId, String roleId) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call CONFIGURATION_MANAGEMENT.remove_muted_role(?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, roleId);
                cstmt.execute();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
