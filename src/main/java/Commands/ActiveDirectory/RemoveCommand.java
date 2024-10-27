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

        if (args.length != 2) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            ActiveDirectoryManagement.removeCommand(event.getGuild().getId(), args[0], CommandHandler.getCommand(args[1].toLowerCase()).getName());

            event.getChannel().sendMessage("Successfully removed command.").queue();

        }
    }
}
