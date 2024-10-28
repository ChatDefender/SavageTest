package Handlers.SQLHandlers;

import Main.functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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

    public static String getPunishmentTiers(String guildId, String punishment_name) {

        try {
            // Establish a connection to the database
            SQLFunctions.verifyConnection();

            // Base query
            String sql = "SELECT * from punishment_list WHERE guild_id = ?";

            // Add punishment name filter if provided
            if (punishment_name != null && !punishment_name.isEmpty()) {
                sql += " AND name = UPPER(?)";
            }

            PreparedStatement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            stmt.setString(1, guildId);

            // Set the punishment name parameter if it's included
            if (punishment_name != null && !punishment_name.isEmpty()) {
                stmt.setString(2, punishment_name);
            }

            ResultSet resultSet = stmt.executeQuery();

            // Step 1: Calculate max width of each column
            int maxGuildId = "Guild ID".length();
            int maxPunishmentId = "Punishment ID".length();
            int maxName = "Name".length();
            int maxPunishmentTier = "Punishment Tier".length();
            int maxPunishmentType = "Punishment Type".length();
            int maxTimeInMs = "Time (ms)".length();

            // First pass through the result set to find the longest values
            while (resultSet.next()) {
                String guildIdVal = resultSet.getString("guild_id");
                String punishmentId = resultSet.getString("punishment_id");
                String name = resultSet.getString("name");
                String punishmentTier = resultSet.getString("punishment_tier");
                String punishmentType = resultSet.getString("punishment_type");
                String timeInMs = resultSet.getString("time_in_ms");

                maxGuildId = Math.max(maxGuildId, guildIdVal.length());
                maxPunishmentId = Math.max(maxPunishmentId, punishmentId.length());
                maxName = Math.max(maxName, name.length());
                maxPunishmentTier = Math.max(maxPunishmentTier, punishmentTier.length());
                maxPunishmentType = Math.max(maxPunishmentType, punishmentType.length());
                maxTimeInMs = Math.max(maxTimeInMs, timeInMs.length());
            }

            // Step 2: Build the dynamic format string with column separators
            String formatString = String.format("| %%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds | %%-%ds |\n",
                    maxGuildId, maxPunishmentId, maxName, maxPunishmentTier, maxPunishmentType, maxTimeInMs);

            // Step 3: Build the header and separator line
            StringBuilder table = new StringBuilder();
            table.append(String.format(formatString,
                    "Guild ID", "Punishment ID", "Name", "Punishment Tier", "Punishment Type", "Time (ms)"));
            table.append(functions.repeat('-', maxGuildId + maxPunishmentId + maxName + maxPunishmentTier + maxPunishmentType + maxTimeInMs + 19) + "\n");

            // Step 4: Second pass through the result set to format each row
            resultSet.beforeFirst(); // Move back to the beginning of the ResultSet
            while (resultSet.next()) {
                String guildIdVal = resultSet.getString("guild_id");
                String punishmentId = resultSet.getString("punishment_id");
                String name = resultSet.getString("name");
                String punishmentTier = resultSet.getString("punishment_tier");
                String punishmentType = resultSet.getString("punishment_type");
                String timeInMs = resultSet.getString("time_in_ms");

                table.append(String.format(formatString,
                        guildIdVal, punishmentId, name, punishmentTier, punishmentType, timeInMs));
            }

            return table.toString();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";
    }

}
