package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveGroup extends BaseCommand {

    public RemoveGroup() {
        super("removegroup",
                new String[] {  },
                "removegroup [group_name]",
                "Removes a group.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            boolean isDeleted = ActiveDirectoryManagement.removeGroup(event.getGuild().getId(), args[0]);

            if (isDeleted) {

                event.getChannel().sendMessage("Successfully removed group " + args[0] + ".").queue();

            } else {

                event.getChannel().sendMessage("Group " + args[0] + " does not exist and therefore cannot be deleted.").queue();

            }

        }

    }

}
