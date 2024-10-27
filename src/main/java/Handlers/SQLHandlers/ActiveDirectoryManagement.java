package Handlers.SQLHandlers;

import java.sql.*;

import static Handlers.SQLHandlers.SQLFunctions.conn;

public class ActiveDirectoryManagement {

    public static void createUnit(String guildId, String unitName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.create_unit(?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createRole(String guildId, String unitName, String discordRoleId) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.create_role(?, ?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());
                cstmt.setString(3, discordRoleId);

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createCommand(String guildId, String unitName, String commandName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.create_command(?, ?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());
                cstmt.setString(3, commandName.toUpperCase());

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeUnit(String guildId, String unitName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.remove_unit(?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeRole(String guildId, String unitName, String discordRoleId) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.remove_role(?, ?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());
                cstmt.setString(3, discordRoleId);

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeCommand(String guildId, String unitName, String commandName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.remove_command(?, ?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());
                cstmt.setString(3, commandName.toUpperCase());

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasPunishmentPermissions(String guildId, String roleIds, String punishment) {
        boolean hasPerms = false;

        // Split roleIds by commas to handle multiple roles
        String[] roleIdArray = roleIds.split(",");

        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) as counts FROM punish_perms WHERE guild_id = ? AND discord_role_id IN (");

        // Append placeholders for each role ID
        for (int i = 0; i < roleIdArray.length; i++) {
            sqlBuilder.append("?");
            if (i < roleIdArray.length - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(") AND punishment_name = UPPER(?)");

        String sql = sqlBuilder.toString();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, guildId);

            // Set each role ID in the PreparedStatement
            for (int i = 0; i < roleIdArray.length; i++) {
                stmt.setString(i + 2, roleIdArray[i].trim());
            }

            // Set the punishment parameter
            stmt.setString(roleIdArray.length + 2, punishment);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("counts");
                hasPerms = count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasPerms;
    }


    public static boolean hasCommandPermission(String guildId, String roleIds, String command) {
        boolean hasPerms = false;

        // Split roleIds by commas to handle multiple roles
        String[] roleIdArray = roleIds.split(",");
        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) as counts FROM cmd_perms WHERE guild_id = ? AND discord_role_id IN (");

        // Append placeholders for each role ID
        for (int i = 0; i < roleIdArray.length; i++) {
            sqlBuilder.append("?");
            if (i < roleIdArray.length - 1) {
                sqlBuilder.append(", ");
            }
        }
        sqlBuilder.append(") AND cmd_name = UPPER(?)");

        String sql = sqlBuilder.toString();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, guildId);

            // Set each role ID in the PreparedStatement
            for (int i = 0; i < roleIdArray.length; i++) {
                stmt.setString(i + 2, roleIdArray[i].trim());
            }

            // Set the command parameter
            stmt.setString(roleIdArray.length + 2, command);

            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("counts");
                hasPerms = count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hasPerms;
    }

    public static void createPunishment(String guildId, String unitName, String punishmentName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.create_punishment(?, ?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());
                cstmt.setString(3, punishmentName.toUpperCase());

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void removePunishment(String guildId, String unitName, String punishmentName) {

        try {

            SQLFunctions.verifyConnection();

            try (CallableStatement cstmt = conn.prepareCall("{ call GROUP_MANAGEMENT.remove_punishment(?, ?, ?) }")) {

                cstmt.setString(1, guildId);
                cstmt.setString(2, unitName.toUpperCase());
                cstmt.setString(3, punishmentName.toUpperCase());

                cstmt.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}