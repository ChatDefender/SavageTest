package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class AddGroup extends BaseCommand {

    public AddGroup() {
        super("addgroup",
                new String[] { "creategroup" },
                "addgroup [group_name]",
                "Creates a new group for permissions.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String groupName = args[0];

            if (groupName.contains("<@") || groupName.contains("<@&") || groupName.contains("<#")) {

                event.getChannel().sendMessage("Group names cannot be mentioned users, roles, or channels.").queue();

            } else if (groupName.length() > 26) {

                event.getChannel().sendMessage("Group names cannot exceed 25 characters.").queue();

            } else if (!groupName.matches("[a-zA-Z]+$")) {

                event.getChannel().sendMessage("Group names cannot contain special characters").queue();

            } else {

                int stat = ActiveDirectoryManagement.createGroup(event.getGuild().getId(), args[0]);

                if (stat == 0) {

                    event.getChannel().sendMessage("Successfully created group " + args[0] + ".").queue();

                } else if (stat == 1) {

                    event.getChannel().sendMessage("That group already exists!").queue();

                } else if (stat == 2) {

                    event.getChannel().sendMessage("There was an issue with that request").queue();

                }


            }

        }

    }

}
