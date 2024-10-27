package Main;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class functions {

    private static final String CREDENTIALS_FILE = "config.json"; // Relative path to the config file

    public static String getCredential(String key) {
        try {
            // Determine the working directory
            String currentDir = System.getProperty("user.dir");

            // Construct paths for both development and production environments
            Path devPath = Paths.get(currentDir, CREDENTIALS_FILE); // Directly in the project root in development
            Path prodPath = Paths.get(currentDir, "..", CREDENTIALS_FILE); // One level up from where the .jar resides in production

            // Use the path that actually exists
            Path configPath = Files.exists(devPath) ? devPath : prodPath;

            // Read the content of the credentials file
            String content = new String(Files.readAllBytes(configPath));

            // Parse JSON content into a JSONObject
            JSONObject jsonObject = new JSONObject(content);

            // Return the value associated with the provided key
            return jsonObject.getString(key);
        } catch (IOException e) {
            System.err.println("Error reading credentials file: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Error processing credentials file: " + e.getMessage());
            return null;
        }
    }

    public static long timeToMilliseconds(String duration) {

        Pattern pattern = Pattern.compile("(\\d+)(mon|dur|yr|d|h|m)");
        Matcher matcher = pattern.matcher(duration.trim());

        System.out.println("Input Duration: " + duration);

        if (!matcher.find()) {
            System.out.println("No match found.");
            return -1;
        }

        long timeValue = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        System.out.println("Parsed: " + timeValue + " " + unit);

        switch (unit) {
            case "m":
                return timeValue * 60L * 1000L;
            case "h":
                return timeValue * 60L * 60L * 1000L;
            case "d":
                return timeValue * 24L * 60L * 60L * 1000L;
            case "mon":
                return timeValue * 30L * 24L * 60L * 60L * 1000L;
            case "yr":
                return timeValue * 365L * 24L * 60L * 60L * 1000L;
            case "dur":
                return timeValue;
            default:
                return -1;
        }
    }


    public static String buildHelpBlock(String commandNameOrAliases) {

        StringBuilder sb = new StringBuilder();

        BaseCommand command = CommandHandler.getCommand(commandNameOrAliases.toLowerCase());
        sb.append("```Command Help: ").append(command.getName()).append("\n");
        sb.append("Name: ").append(command.getName()).append("\n");

        sb.append("Alias: ");
        for (String s : command.getAliases()) {

            sb.append(s).append(", ");

        }

        sb.append("\n");
        sb.append("Usage: ").append(command.getUsage()).append("\n");
        sb.append("Short Description: ").append(command.getShortDescription()).append("\n");
        sb.append("Long Description: ").append(command.getLongDescription()).append("\n");
        sb.append("```");

        return sb.toString();

    }

    public static String getUserId(MessageReceivedEvent event, String args) {

        args = args.replace("<@", "").replace(">", "");

        return verifyMember(event, args);

    }

    public static String verifyChannel(MessageReceivedEvent event, String args) {

        try {

            // if the role is mentioned, remove the symbols.
            args = args.replace("<#", "").replace(">", "");

            // now try to retrieve the role based on the string itself.
            TextChannel tc = event.getGuild().getTextChannelById(args);

            // if the role exists, then return the id.
            if (tc != null) {

                return tc.getId();

            }

            return null;

        } catch (Exception e) {

            return null;

        }

    }

    public static String verifyMember(MessageReceivedEvent event, String args) {

        try {

            Member m = event.getGuild().retrieveMemberById(args.replace("<@", "").replace(">", "")).complete();

            if (m != null) {

                return m.getId();

            }

            return null;

        } catch (Exception e) {

            return null;

        }

    }

    public static String verifyRole(MessageReceivedEvent event, String args) {

        try {

            // if the role is mentioned, remove the symbols.
            args = args.replace("<@&", "").replace(">", "");

            // now try to retrieve the role based on the string itself.
            Role r = event.getGuild().getRoleById(args);

            // if the role exists, then return the id.
            if (r != null) {

                return r.getId();

            }

            return null;

        } catch (Exception e) {

            return null;

        }
    }

    public static boolean hasCmdPerms(Member member, String GuildId, String commandName) {

        boolean hasPerms = false;

        hasPerms = (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR));

        if (!hasPerms) {

            StringBuilder sb = new StringBuilder();

            member.getRoles().forEach(r -> sb.append(r.getId()).append(","));

            hasPerms = ActiveDirectoryManagement.hasCommandPermission(GuildId, sb.toString(), commandName);

        }

        if (!hasPerms) {

            hasPerms = member.hasPermission(CommandHandler.getPermissionLevel(commandName));

        }

        return hasPerms;

    }

    public static boolean hasPunishPerms(Member member, String GuildId, String punishmentName) {

        boolean hasPerms = false;

        hasPerms = (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR));

        if (!hasPerms) {

            StringBuilder sb = new StringBuilder();

            member.getRoles().forEach(r -> sb.append(r.getId()).append(","));

            hasPerms = ActiveDirectoryManagement.hasPunishmentPermissions(GuildId, sb.toString(), punishmentName);

        }

        return hasPerms;

    }

    public static void punishUser(SQLFunctions.Punishments punishment, MessageReceivedEvent event, Member member, String reason) {

        CountDownLatch latch = new CountDownLatch(1);

        switch (punishment) {
            case BAN:
                event.getGuild().ban(member.getUser(), 0, TimeUnit.SECONDS).reason(reason).queue(
                        success -> {
                            event.getChannel().sendMessage("Successfully banned " + member.getEffectiveName() + " `[" + member.getId() + "]`").queue();
                            latch.countDown();
                        },
                        error -> {
                            event.getChannel().sendMessage("Failed to ban " + member.getEffectiveName() + " `[" + member.getId() + "]`. Reason: " + error.getMessage()).queue();
                            latch.countDown();
                        }
                );
                break;

            case MUTE:
                String muteRoleId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.MUTE_ROLE_ID);

                if (member.getRoles().contains(event.getGuild().getRoleById(muteRoleId))) {
                    event.getChannel().sendMessage("User " + member.getNickname() + " `[" + member.getId() + "]` is already muted.").queue();
                    latch.countDown();
                } else {
                    event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(muteRoleId)).queue(
                            success -> {
                                event.getChannel().sendMessage("Successfully muted " + member.getEffectiveName() + " `[" + member.getId() + "]`").queue();
                                latch.countDown();
                            },
                            error -> {
                                event.getChannel().sendMessage("There was an error in muting " + member.getNickname() + " `[" + member.getId() + "]`\nError: " + error.getMessage()).queue();
                                latch.countDown();
                            }
                    );
                }
                break;

            case KICK:
                event.getGuild().kick(member).queue(
                        success -> {
                            event.getChannel().sendMessage("Successfully kicked " + member.getNickname() + " `[" + member.getId() + "]`.").queue();
                            latch.countDown();
                        },
                        error -> {
                            event.getChannel().sendMessage("There was an error kicking " + member.getNickname() + " `[" + member.getId() + "]`. Error Message: " + error.getMessage()).queue();
                            latch.countDown();
                        }
                );
                break;

            case WARN:
                // Set to true since WARN is considered successful
                latch.countDown();
                break;

            default:
                latch.countDown();
                break;
        }

        try {
            latch.await(); // Wait until latch is counted down (operation completes)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void executePunishment(SQLFunctions.Punishments pun, MessageReceivedEvent event, String user, String duration, String reason) {

        System.out.println(pun);

        // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
        String userId = functions.getUserId(event, user);

        if (userId == null) {

            event.getChannel().sendMessage("I cannot find the mentioned user!").queue();

        } else {

            // Now that we have an ID, verify it's a guild member
            Member member = event.getGuild().retrieveMemberById(userId).complete();

            if (member.isOwner()) {

                event.getChannel().sendMessage("I cannot punish the owner of the server.").queue();

            } else {

                long timeInMs = functions.timeToMilliseconds(duration);

                System.out.println(timeInMs + " " + duration);

                if (timeInMs == -1 && !duration.equals("0") ) {

                    event.getChannel().sendMessage("You provided an invalid time. Available times are as follows:  \n#m - minutes, \n#h - hours, \n#d - days, \n#mon - months, \n#y - year, \nor 0 for permanent.").queue();

                } else {

                    event.getJDA().retrieveUserById(member.getId()).complete().openPrivateChannel().complete().sendMessage("```\nPUNISHMENT EXECUTED IN  " + member.getGuild().getName() + "\nPUNISHMENT TYPE: " + pun + "\nModerator: " + event.getAuthor().getName() + "\nDuration: " + duration + "\nReason: " + reason + "\n```").queue();

                    int id = PunishmentLogManagement.insertPunishmentLog(event.getGuild().getId(), userId, event.getAuthor().getId(), pun, String.valueOf(timeInMs), reason);

                    String punishmentLogChannelId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PUNISHMENT_LOG_ID);

                    if (punishmentLogChannelId != null) {

                        TextChannel tc = event.getGuild().getTextChannelById(punishmentLogChannelId);
                        if (tc != null) {

                            tc.sendMessage("```\nPUNISHMENT EXECUTED: " + pun + " " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " + event.getAuthor().getName() + "\nReason: " + reason + "\nPunishmentId: " + id + "```").queue();

                        } else {

                            event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();

                        }

                    }

                    punishUser(pun, event, member, reason);

                }

            }

        }

    }

    // Helper method to repeat a character
    public static String repeat(char c, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            builder.append(c);
        }
        return builder.toString();
    }


}