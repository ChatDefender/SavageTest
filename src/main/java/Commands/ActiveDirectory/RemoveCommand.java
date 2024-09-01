package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.verifyRole;

public class RemoveCommand extends BaseCommand {

    public RemoveCommand() {
        super("removecommand",
                new String[] {  },
                "removecommand [group_name] [@role | role_id] [command_name]",
                "Removes a command from a role within a group.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 3) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String roleId = verifyRole(event, args[1]);

            if (roleId != null) {

                int stat = ActiveDirectoryManagement.removeRoleCommand(event.getGuild().getId(), args[0], roleId, CommandHandler.getCommand(args[2].toLowerCase()).getName());

                if (stat == 0) {

                    event.getChannel().sendMessage("Successfully removed command " + CommandHandler.getCommand(args[2]).getName() + " to the role " + event.getGuild().getRoleById(roleId).getName() + " that is within group " + args[0] + ".").queue();

                } else if (stat == 1) {

                    event.getChannel().sendMessage("That command is not part of the given role within the given group.").queue();

                } else if (stat == 2) {

                    event.getChannel().sendMessage("That role is not part of the provided group.").queue();

                } else if (stat == 3) {

                    event.getChannel().sendMessage("That group does not exist.").queue();

                } else if (stat == 4) {

                    event.getChannel().sendMessage("That command does not exist.").queue();

                } else if (stat == 5) {

                    event.getChannel().sendMessage("There was an issue with that request.").queue();

                }

            } else {

                event.getChannel().sendMessage("That role could not be found.").queue();

            }
        }
    }
}
