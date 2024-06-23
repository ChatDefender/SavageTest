package Handlers.SQLHandlers;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class ConfigurationSettings {

    public static void setSetting(String guildId, SQLFunctions.Settings setting, String newValue) {

        try {

            SQLFunctions.verifyConnection();

            String procCall = "{ call configuration_management.update_settings(?, ?, ?) }";

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

            String functionCall = "{ ? = call configuration_management.get_setting(?, ?)}";

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

    public static void verifySettings(String guildId) {

        try {

            SQLFunctions.verifyConnection();
            String procCall = "{ call configuration_management.verify_settings(?) }";

            try (CallableStatement cstmt = conn.prepareCall(procCall)) {

                cstmt.setString(1, guildId);

                cstmt.execute();

            }

        } catch (SQLException se) {

            se.printStackTrace();

        }

    }

    public static void removeMutedRole(String GuildId, String RoleId) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call configuration_management.remove_muted_role(?, ?) }")) {

                cstmt.setString(1, GuildId);
                cstmt.setString(2, RoleId);
                cstmt.execute();

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

}
