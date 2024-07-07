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
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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

        Pattern pattern = Pattern.compile("(\\d+)([m|h|d|mon|yr])");
        Matcher matcher = pattern.matcher(duration.trim());

        if (!matcher.matches()) {

            return -1;

        }

        long timeValue = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

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

            Member m = event.getGuild().retrieveMemberById(args).complete();

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

    public static boolean hasPermissions(Member member, String GuildId, String commandName) {

        boolean hasPerms;

        hasPerms = (member.isOwner() || member.hasPermission(Permission.ADMINISTRATOR));

        if (!hasPerms) {

            StringBuilder sb = new StringBuilder();

            member.getRoles().forEach(r -> sb.append(r.getId()).append(":"));

            hasPerms = ActiveDirectoryManagement.hasPermission(GuildId, sb.toString(), commandName);

        }

        if (!hasPerms) {

            hasPerms = member.hasPermission(CommandHandler.getPermissionLevel(commandName));

        }

        return hasPerms;

    }

    public static boolean punishUser(SQLFunctions.Punishments punishment, MessageReceivedEvent event, Member member, String reason) {

        AtomicBoolean successful = new AtomicBoolean(false);

        switch (punishment) {

            case BAN:

                event.getGuild().ban(member.getUser(), 0, TimeUnit.SECONDS).reason(reason).queue(
                        success -> {
                            event.getChannel().sendMessage("Successfully banned " + member.getEffectiveName() + " `[" + member.getId() + "]`").queue();
                            successful.set(true);
                            },
                        error -> event.getChannel().sendMessage("Failed to ban " + member.getEffectiveName() + " `[" + member.getId() + "]`. Reason: " + error.getMessage() ).queue()
                );
                break;

            case MUTE:

                String muteRoleId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.MUTEDROLEID);

                if (member.getRoles().contains(event.getGuild().getRoleById(muteRoleId))) {

                    event.getChannel().sendMessage("User " + member.getNickname() + " `["+member.getId()+"]` is already muted.").queue();

                } else {

                    event.getGuild().addRoleToMember(member, event.getGuild().getRoleById(muteRoleId)).queue(

                            success -> {
                                event.getChannel().sendMessage("Successfully muted "+ member.getNickname() + " `["+member.getId()+"]`").queue();
                                successful.set(true);
                            },
                            error -> event.getChannel().sendMessage("There was an error in muting "+ member.getNickname() + " `["+member.getId()+"]`\nError: " + error.getMessage()).queue()

                    );

                }

                break;

            case KICK:

                event.getGuild().kick(member).queue(

                        success -> {
                            event.getChannel().sendMessage("Successfully kicked " + member.getNickname() + " `[" + member.getId() + "].`").queue();
                            successful.set(true);
                        },
                        error -> event.getChannel().sendMessage("There was an error kicking "+ member.getNickname() + " `[" + member.getId() + "].` Error Message" + error.getMessage()).queue()

                );

                break;

            case WARN:

                break;

            default:

                break;


        }

        return successful.get();


    }

    public static void executePunishment(SQLFunctions.Punishments pun, MessageReceivedEvent event, String user, String duration, String reason) {

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

                if (timeInMs == -1) {

                    event.getChannel().sendMessage("You provided an invalid time. Available times are as follows:  \n#m - minutes, \n#h - hours, \n#d - days, \n#mon - months, \n#y - year, \nor 0 for permanent.").queue();

                } else {

                    boolean shouldLog = punishUser(pun, event, member, reason);

                    if (shouldLog) {

                        PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                        if (pc.canTalk()) {

                            pc.sendMessage("```\nPUNISHMENT EXECUTED IN  " + member.getGuild().getName() + "\nPUNISHMENT TYPE: " + pun + "\nModerator: " + event.getAuthor().getName() + "\nDuration: " + duration + "\nReason: " + reason + "\n```").queue();

                        } else {

                            event.getChannel().sendMessage("Failed to send user a private message: User is not accepting private messages").queue();

                        }


                        int id = PunishmentLogManagement.insertPunishment(event.getGuild().getId(), userId, event.getAuthor().getId(), pun, String.valueOf(timeInMs), reason);

                        String punishmentLogChannelId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PUNISHMENTLOGID);

                        if (punishmentLogChannelId != null) {

                            TextChannel tc = event.getGuild().getTextChannelById(punishmentLogChannelId);
                            if (tc != null && tc.canTalk()) {

                                tc.sendMessage("```\nPUNISHMENT EXECUTED: " + pun + " " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " + event.getAuthor().getName() + "\nReason: " + reason + "\nPunishmentId: " + id + "```").queue();

                            } else {

                                event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();

                            }

                        }
                    }
                }

            }

        }

    }

    public static void muteUser() {


    }

    public static void kickUser() {


    }

    public static void warnUser() {


    }





}