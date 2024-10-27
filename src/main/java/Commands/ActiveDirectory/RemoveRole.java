package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.verifyRole;

public class RemoveRole extends BaseCommand {

    public RemoveRole() {
        super("ActiveDirectory",
                "removerole",
                new String[] { },
                "removerole [group_name] [@role | role_id]",
                "Removes a group.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String roleId = verifyRole(event, args[1]);

            if (roleId != null) {

                ActiveDirectoryManagement.removeRole(event.getGuild().getId(), args[0], roleId);

                event.getChannel().sendMessage("Successfully removed role.").queue();

            } else {

                event.getChannel().sendMessage("That role could not be found.").queue();

            }

        }

    }

}
