package Events;

import Commands.Punishments.*;
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
                String prefix = "s!";

                // Triggers the command filtration section.
                if (message.getContentRaw().startsWith(prefix)) {

                    if (!event.getAuthor().getId().equals("286270602820452353")) {

                        event.getChannel().sendMessage("Only the bot owner can execute commands at this time.").queue();

                    } else {

                        String commandLine = message.getContentRaw().replace("s!", "");
                        String[] args = commandLine.split("\\s+");
                        String command = args[0];

                        switch (command.toLowerCase()) {
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
                            case "clearlogs":
                                break;
                            case "delban":
                                break;
                            case "delmute":
                                break;
                            case "delwarn":
                                break;
                            case "modlogs":
                                break;
                        }

                    }

                } else {

                    // we may do something here

                }

            }

        }

    }

}
