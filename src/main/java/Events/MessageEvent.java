package Events;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // If the message is from a bot, we want to ignore it.
        if (!event.getMessage().getAuthor().isBot()) {

            // If the message is from a text channel, this is where we want to filter it by type
            if (event.isFromType(ChannelType.TEXT)) {

                // Temporary prefix. This will be updated by the user(s)
                String prefix = "s!";

                // Triggers the command filtration section.
                if (event.getMessage().getContentRaw().startsWith(prefix)) {

                    event.getChannel().sendMessage("congrats! you executed a command").queue();

                } else {

                    // we may do something here

                }

            }

        }

    }

}
