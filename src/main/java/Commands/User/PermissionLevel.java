package Commands.User;

import Commands.BaseCommand;
import Main.functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermissionLevel extends BaseCommand {
    public PermissionLevel() {
        super("permissionlevel", new String[] {"permlvl", "pl"}, "permissionlevel",  "Tells the user their permission level", "The bot owner will have the highest permission level\nPeople with the Permission.ADMINISTRATOR on discord will have a perm lvl of 15\nThe default permission level for commands is 14\nThe rest are set based on the active directory");
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        event.getChannel().sendMessage("Your permission level is: " + functions.getAuthorPermLevel(event)).queue();

    }

}
