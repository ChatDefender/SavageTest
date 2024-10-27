package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveGroup extends BaseCommand {

    public RemoveGroup() {
        super("removeunit",
                new String[] {  },
                "removeunit [group_name]",
                "Removes a unit.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            ActiveDirectoryManagement.removeUnit(event.getGuild().getId(), args[0]);

            event.getChannel().sendMessage("Successfully removed group " + args[0] + ".").queue();


        }

    }

}
