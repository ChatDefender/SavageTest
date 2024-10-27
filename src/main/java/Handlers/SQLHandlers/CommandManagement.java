package Handlers.SQLHandlers;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class CommandManagement {

    public static int insertCommandLog(String guildId, String channelId, String userId, String command, int userHasPermission) {

        int rtnVal = 0;

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call COMMAND_MANAGEMENT.insert_cmd_log(?, ?, ?, ?, ?) }")) {

                cstmt.registerOutParameter(1, Types.NUMERIC);

                cstmt.setString(2, guildId);
                cstmt.setString(3, channelId);
                cstmt.setString(4, userId);
                cstmt.setString(5, command.toUpperCase());
                cstmt.setInt(6, userHasPermission);

                cstmt.execute();

                rtnVal = cstmt.getInt(1);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnVal;

    }

    public static void logCommandError(int cmdLogId, String commandFileName, String message) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call COMMAND_MANAGEMENT.cmd_log_err(?, ?, ?) }")) {

                cstmt.setInt(1, cmdLogId);
                cstmt.setString(2, commandFileName);
                cstmt.setString(3, message);

                cstmt.execute();

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
