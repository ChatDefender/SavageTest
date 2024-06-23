package Handlers.SQLHandlers;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class CommandManagement {

    public static int logCommand(String GuildId, String ChannelID, String UserID, String command, int user_has_permission) {

        int rtnval = 0;

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call COMMAND_MANAGEMENT.INSERT_CMD_LOG(?, ?, ?, ?, ?) }")) {

                cstmt.registerOutParameter(1, Types.NUMERIC);

                cstmt.setString(2, GuildId);
                cstmt.setString(3, ChannelID);
                cstmt.setString(4, UserID);
                cstmt.setString(5, command);
                cstmt.setInt(6, user_has_permission);

                cstmt.execute();

                rtnval = cstmt.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return rtnval;

    }

    public static void logCmdError(int cmdLogId, String commandFileName, String message) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call COMMAND_MANAGEMENT.CMD_LOG_ERR(?, ?, ?) }")) {

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
