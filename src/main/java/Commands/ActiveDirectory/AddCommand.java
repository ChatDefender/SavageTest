package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddCommand extends BaseCommand {

    public AddCommand() {
        super(
                "ActiveDirectory",
                "addcommand",
                new String[] {  },
                "addCommand [group_name] [@role | role_id] [command_name]",
                "Adds command to a role for a specified group.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            if (CommandHandler.getCommand(args[1].toLowerCase()) != null) {

                ActiveDirectoryManagement.createCommand(event.getGuild().getId(), args[0], args[1].toLowerCase() );

                event.getChannel().sendMessage("Successfully added command.").queue();

            } else {

                event.getChannel().sendMessage("Invalid command provided.").queue();

            }

        }

    }

}
