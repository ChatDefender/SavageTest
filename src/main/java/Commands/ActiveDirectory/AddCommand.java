package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Handlers.SQLHandlers.PunishmentManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.verifyRole;

public class AddCommand extends BaseCommand {

    public AddCommand() {
        super("addcommand",
                new String[] {  },
                "addCommand [group_name] [@role | role_id] [command_name]",
                "Adds command to a role for a specified group. Note: group_name, and role_id should already exist.",
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

                String commandOrPunishmentValue = CommandHandler.getCommand(args[2]) != null ? CommandHandler.getCommand(args[2]).getName() : (PunishmentManagement.doesPunishmentExist(event.getGuild().getId(), args[2]) == 1 ? args[2] : "-1" ) ;

                if (!commandOrPunishmentValue.equals("-1")) {

                    int stat = ActiveDirectoryManagement.createRoleCommand(event.getGuild().getId(), args[0], roleId, commandOrPunishmentValue );

                    if (stat == 0) {

                        event.getChannel().sendMessage("Successfully added command " + commandOrPunishmentValue + " to the role " + event.getGuild().getRoleById(roleId).getName() + " that is within group " + args[0] + ".").queue();

                    } else if (stat == 1) {

                        event.getChannel().sendMessage("That command is already provided to the given role within the given group.").queue();

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

                    event.getChannel().sendMessage("Invalid command/punishment provided.").queue();

                }

            } else {

                event.getChannel().sendMessage("That role could not be found.").queue();

            }

        }

    }

}
