package Events;

import Handlers.CommandHandler;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Main.functions;
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

                    // Get the command and args
                    String commandLine = message.getContentRaw().replace(prefix, "");
                    String[] args = commandLine.split("\\s+");
                    String command = args[0];

                    // If the user has permission to run it, they can
                    if (functions.getAuthorPermLevel(event) >= functions.getCommandPermLvl(command)) {

                        // Run the command
                        CommandHandler.executeCommand(command.toLowerCase(), event, args);

                    }

                }

            }

        }

    }

}
