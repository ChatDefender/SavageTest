package Handlers.SQLHandlers;

import Main.functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    public static void verifyRoles(String roleIds) {
        // Split roleIds by commas to handle multiple roles
        String[] roleIdArray = roleIds.split(",");
        StringBuilder settingSqlBuilder = new StringBuilder("UPDATE setting SET mute_role_id = NULL WHERE mute_role_id NOT IN (");
        StringBuilder unitRolesBuilder = new StringBuilder("DELETE FROM unit_role WHERE discord_role_id NOT IN (");

        // Append placeholders for each role ID
        for (int i = 0; i < roleIdArray.length; i++) {
            settingSqlBuilder.append("?");
            unitRolesBuilder.append("?");
            if (i < roleIdArray.length - 1) {
                settingSqlBuilder.append(", ");
                unitRolesBuilder.append(", ");
            }
        }
        settingSqlBuilder.append(")");
        unitRolesBuilder.append(")");

        String settingSql = settingSqlBuilder.toString();
        String unitRolesSql = unitRolesBuilder.toString();

        SQLFunctions.verifyConnection();

        try (PreparedStatement settingStmt = conn.prepareStatement(settingSql);
             PreparedStatement unitRolesStmt = conn.prepareStatement(unitRolesSql)) {

            // Set each role ID in the PreparedStatement for settingSql
            for (int i = 0; i < roleIdArray.length; i++) {
                settingStmt.setString(i + 1, roleIdArray[i].trim());
            }

            // Execute the update for setting
            settingStmt.executeUpdate();

            // Set each role ID in the PreparedStatement for unitRoles
            for (int i = 0; i < roleIdArray.length; i++) {
                unitRolesStmt.setString(i + 1, roleIdArray[i].trim());
            }

            // Execute the update for unitRoles
            unitRolesStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getPunishmentPermissions(MessageReceivedEvent event, String guildId, String punishName) {

        try {
            // Verify or establish connection to the database
            SQLFunctions.verifyConnection();

            // Base query with placeholders
            String sql = "SELECT unit_name, discord_role_id, punishment_name from punish_perms where guild_id = ? ";

            if (!punishName.isEmpty()) {

                sql = sql + " and punishment_name = upper('" + punishName + "')";

            }

            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, guildId);

            ResultSet resultSet = stmt.executeQuery();

            // Step 1: Calculate max width of each column
            int maxUnitName = "Unit Name".length();
            int maxRoleName = "Role Name".length();
            int maxPunishmentName = "Punishment Name".length();

            // First pass through the result set to find the longest values
            while (resultSet.next()) {
                String unitName = resultSet.getString("unit_name");
                String roleName = event.getGuild().getRoleById(resultSet.getString("discord_role_id")).getName();
                String punishmentName = resultSet.getString("punishment_name");

                maxUnitName = Math.max(maxUnitName, unitName.length());
                maxRoleName = Math.max(maxRoleName, roleName.length());
                maxPunishmentName = Math.max(maxPunishmentName, punishmentName.length());
            }

            // Step 2: Build the dynamic format string with column separators
            String formatString = String.format("| %%-%ds | %%-%ds | %%-%ds |\n",
                    maxUnitName, maxRoleName, maxPunishmentName);

            // Step 3: Build the header and separator line
            StringBuilder table = new StringBuilder();
            table.append(String.format(formatString,
                    "Unit Name", "Role Name", "Punishment Name"));
            table.append(functions.repeat('-', maxUnitName + maxRoleName + maxPunishmentName + 10) + "\n");

            // Step 4: Second pass through the result set to format each row
            resultSet.beforeFirst(); // Move back to the beginning of the ResultSet
            while (resultSet.next()) {
                String unitName = resultSet.getString("unit_name");
                String roleName = event.getGuild().getRoleById(resultSet.getString("discord_role_id")).getName();
                String punishmentName = resultSet.getString("punishment_name");

                table.append(String.format(formatString,
                        unitName, roleName, punishmentName));
            }

            return table.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getCommandPermissions(MessageReceivedEvent event, String guildId, String cmdName) {

        try {
            // Verify or establish connection to the database
            SQLFunctions.verifyConnection();

            // Base query with placeholders
            String sql = "SELECT discord_role_id, unit_name, cmd_category, cmd_name from cmd_perms where guild_id = ? ";

            if (!cmdName.isEmpty()) {

                sql = sql + " and cmd_name = upper('"+cmdName+"')";

            }

            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, guildId);

            ResultSet resultSet = stmt.executeQuery();

            // Step 1: Calculate max width of each column
            int maxRoleName = "Role Name".length();
            int maxUnitName = "Unit Name".length();
            int maxCommandCategory = "Command Category".length();
            int maxCommandName = "Command Name".length();

            // First pass through the result set to find the longest values
            while (resultSet.next()) {
                String roleName = event.getGuild().getRoleById(resultSet.getString("discord_role_id")).getName();
                String unitName = resultSet.getString("unit_name");
                String commandCategory = resultSet.getString("cmd_category");
                String commandName = resultSet.getString("cmd_name");

                maxRoleName = Math.max(maxRoleName, roleName.length());
                maxUnitName = Math.max(maxUnitName, unitName.length());
                maxCommandCategory = Math.max(maxCommandCategory, commandCategory.length());
                maxCommandName = Math.max(maxCommandName, commandName.length());
            }

            // Step 2: Build the dynamic format string with column separators
            String formatString = String.format("| %%-%ds | %%-%ds | %%-%ds | %%-%ds |\n",
                    maxRoleName, maxUnitName, maxCommandCategory, maxCommandName);

            // Step 3: Build the header and separator line
            StringBuilder table = new StringBuilder();
            table.append(String.format(formatString,
                    "Role Name", "Unit Name", "Command Category", "Command Name"));
            table.append(functions.repeat('-', maxRoleName + maxUnitName + maxCommandCategory + maxCommandName + 13) + "\n");

            // Step 4: Second pass through the result set to format each row
            resultSet.beforeFirst(); // Move back to the beginning of the ResultSet
            while (resultSet.next()) {
                String roleName = event.getGuild().getRoleById(resultSet.getString("discord_role_id")).getName();
                String unitName = resultSet.getString("unit_name");
                String commandCategory = resultSet.getString("cmd_category");
                String commandName = resultSet.getString("cmd_name");

                table.append(String.format(formatString,
                        roleName, unitName, commandCategory, commandName ));
            }

            return table.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";

    }
}