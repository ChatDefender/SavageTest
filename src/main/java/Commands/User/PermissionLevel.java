package Commands.User;

import CustomerFunctions.functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermissionLevel {
    public void permlvl(MessageReceivedEvent event, String[] args) {

        event.getChannel().sendMessage("Your permission level is: " + functions.getAuthorPermLevel(event)).queue();

    }

}
