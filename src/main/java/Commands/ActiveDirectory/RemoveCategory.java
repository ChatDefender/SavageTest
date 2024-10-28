package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class RemoveCategory extends BaseCommand {

    public RemoveCategory() {
        super(
                "ActiveDirectory",
                "removecategory",
                new String[] {  },
                "removecategory [unit_name] [category_name]",
                "Adds command to a role for a specified group.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String[] availableCategories = {"activedirectory","configuration","punishmentlogs","punishmentmanagement","punishment","user"};

            String suppliedCategory = args[1].toLowerCase();

            if (Arrays.asList(availableCategories).contains(suppliedCategory)) {

                CommandHandler.getCommands().forEach(command -> {

                    if (CommandHandler.getCommand(command).getCategory().equalsIgnoreCase(args[1])) {

                        ActiveDirectoryManagement.removeCommand(event.getGuild().getId(), args[0], command);

                    }

                });

                event.getChannel().sendMessage("Successfully removed commands from the specified category.").queue();
            } else {

                event.getChannel().sendMessage("Invalid category provided. Valid categories are activedirectory, configuration, punishmentlogs, punishmentmanagement, punishment, and user.").queue();

            }


        }

    }

}
