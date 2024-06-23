package Handlers.SQLHandlers;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class ActiveDirectoryManagement {

    public static int createGroup(String guildId, String groupName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call GROUP_MANAGEMENT.CREATE_GROUP(?, ?) }")) {

                cstmt.registerOutParameter(1, Types.NUMERIC);

                cstmt.setString(2, guildId);
                cstmt.setString(3, groupName);

                cstmt.execute();

                return cstmt.getInt(1);

            }


        } catch (SQLException e) {

            e.printStackTrace();

            return 2;
        }

    }


    public static int createRole(String guildId, String groupName, String roleId) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call GROUP_MANAGEMENT.CREATE_ROLE(?, ?, ?) }")) {

                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.setString(2, guildId);
                cstmt.setString(3, groupName);
                cstmt.setString(4, roleId);

                cstmt.execute();

                return cstmt.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

            return 3;

        }

    }

    public static int createRoleCommand(String guildId, String groupName, String roleId, String commandName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call GROUP_MANAGEMENT.CREATE_ROLE_COMMAND(?, ?, ?, ?) }")) {

                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.setString(2, guildId);
                cstmt.setString(3, groupName);
                cstmt.setString(4, roleId);
                cstmt.setString(5, commandName);

                cstmt.execute();

                return cstmt.getInt(1);
            }

        } catch (SQLException e) {

            e.printStackTrace();

            return 5;

        }

    }

    public static boolean removeGroup(String guildId, String groupName) {

        boolean isDeleted = false;

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call GROUP_MANAGEMENT.REMOVE_GROUP(?, ?) }")) {

                cstmt.registerOutParameter(1, Types.NUMERIC);
                cstmt.setString(2, guildId);
                cstmt.setString(3, groupName);

                cstmt.execute();

                isDeleted = cstmt.getInt(1) == 1;

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return isDeleted;

    }

    public static int removeRole(String guildId, String groupName, String roleId) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call GROUP_MANAGEMENT.REMOVE_ROLE(?, ?, ?) }")) {

                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.setString(2, guildId);
                cstmt.setString(3, groupName);
                cstmt.setString(4, roleId);

                cstmt.execute();

                return cstmt.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

            return 5;

        }

    }

    public static int removeRoleCommand(String guildId, String groupName, String roleId, String commandName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call GROUP_MANAGEMENT.REMOVE_ROLE_COMMAND(?, ?, ?, ?) }")) {

                cstmt.registerOutParameter(1, Types.INTEGER);
                cstmt.setString(2, guildId);
                cstmt.setString(3, groupName);
                cstmt.setString(4, roleId);
                cstmt.setString(5, commandName);

                cstmt.execute();

                return cstmt.getInt(1);

            }

        } catch (SQLException e) {

            e.printStackTrace();

            return 5;

        }

    }

    public static void verifyRoles(String roleId) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.VERIFY_ROLES(?) }")) {

                cstmt.setString(1, roleId);

                cstmt.execute();

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    public static boolean hasPermission(String GuildId, String roleIds, String command) {

        boolean hasPerms = false;

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ ? = call GROUP_MANAGEMENT.HAS_PERMISSION(?, ?, ?) }")) {

                cstmt.registerOutParameter(1, Types.NUMERIC);
                cstmt.setString(2, GuildId);
                cstmt.setString(3, roleIds);
                cstmt.setString(4, command);

                cstmt.execute();

                hasPerms = cstmt.getInt(1) == 1;

            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return hasPerms;

    }

}
