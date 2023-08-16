package Events;

import Commands.Configuration.ActiveDirectory;
import Commands.Configuration.ConfigureBot;
import Commands.Punishments.*;
import Commands.Log.*;
import Commands.User.PermissionLevel;
import CustomerFunctions.ConfigurationSQLFunctions;
import CustomerFunctions.functions;
import net.dv8tion.jda.api.entities.Message;
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

                    if (functions.getAuthorPermLevel(event) >= functions.getCommandPermLvl(command)) {

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
                            case "permlvl":
                                new PermissionLevel().permlvl(event, args);

                        }

                    }

                }

            }

        }

    }

}
