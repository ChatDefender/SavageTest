package Events;

import Commands.Configuration.ActiveDirectory;
import Commands.Configuration.ConfigureBot;
import Commands.Punishments.*;
import Commands.Log.*;
import CustomerFunctions.ConfigurationSQLFunctions;
import Main.Main;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Classifiers
        Message message = event.getMessage();
        User author = event.getAuthor();

        // If the message is from a bot, we want to ignore it.
        if (!author.isBot()) {

            // If the message is from a text channel, this is where we want to filter it by type
            if (event.isFromType(ChannelType.TEXT)) {

                // Temporary prefix. This will be updated by the user(s)
                String prefix = ConfigurationSQLFunctions.getSetting("Prefix");

                // Triggers the command filtration section.
                if (message.getContentRaw().startsWith(prefix)) {

                    String commandLine = message.getContentRaw().replace(prefix, "");
                    String[] args = commandLine.split("\\s+");
                    String command = args[0];

                    if (hasPermissions(event, command)) {

                        switch (command.toLowerCase()) {
                            case "ad":
                                new ActiveDirectory().activeDirectory(event, args);
                                break;
                            case "ban":
                                new Ban().banUser(event, args);
                                break;
                            case "mute":
                                new Mute().muteUser(event, args);
                                break;
                            case "unban":
                                new Unban().unbanUser(event, args);
                                break;
                            case "unmute":
                                new Unmute().unmuteUser(event, args);
                                break;
                            case "warn":
                                new Warn().warnUser(event, args);
                                break;
                            case "kick":
                                new Kick().kickUser(event, args);
                                break;
                            case "delrec":
                                new DeleteRecord().delRec(event, args);
                                break;
                            case "recrec":
                                new RecoverRecord().recRec(event, args);
                                break;
                            case "clearlog":
                                new ClearLogs().deleteLogs(event, args);
                                break;
                            case "modlog":
                                new ModLogs().getLogs(event, args);
                                break;
                            case "config":
                                new ConfigureBot().config(event, args);


                        }

                    }

                }

            }

        }

    }

    public static boolean hasPermissions(MessageReceivedEvent event, String command) {

        Member m = event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete();

        boolean hasPerm = false;
        boolean isStaff = false;
        String staff = null;

        for (String s : Main.staffRoles.keySet()) {

            for (Long l : Main.staffRoles.get(s)) {

                if (m.getRoles().isEmpty()) {

                    for (Role r : m.getRoles()) {

                        if (r.getIdLong() == l) {

                            isStaff = true;
                            staff = s;
                            break;

                        }

                    }

                }

            }

        }

        if (isStaff) {

            for (String c : Main.staffCommands.get(staff)) {

                if (command.equalsIgnoreCase(c)) {

                    hasPerm = true;
                    break;

                }

            }

        }

        if (event.getAuthor().getId().equals("286270602820452353") || event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete().hasPermission(Permission.ADMINISTRATOR)) {

            hasPerm = true;

        }

        return hasPerm;

    }

}
