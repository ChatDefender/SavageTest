package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.verifyRole;

public class AddRole extends BaseCommand {

    public AddRole() {
        super("addrole",
                new String[] {  },
                "addrole [group_name] [@role | role_id]",
                "Adds a role to an already existing group.",
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

                ActiveDirectoryManagement.createRole(event.getGuild().getId(), args[0], roleId);

                event.getChannel().sendMessage("Successfully added role.").queue();

            } else {

                event.getChannel().sendMessage("That role could not be found.").queue();

            }

        }

    }

}
