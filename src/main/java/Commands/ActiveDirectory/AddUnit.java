package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AddUnit extends BaseCommand {

    public AddUnit() {
        super("addunit",
                new String[] { "createunit" },
                "addunit [unit_name]",
                "Creates a unit for permission management",
                "A unit is considered a \"group\". A unit can handle multiple roles and commands. This allows multiple roles to have access to the samee commands",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String groupName = args[0];

            if (!event.getMessage().getMentions().getChannels().isEmpty()
                    || !event.getMessage().getMentions().getRoles().isEmpty()
                    || !event.getMessage().getMentions().getMembers().isEmpty()) {

                event.getChannel().sendMessage("Unit names cannot be mentioned users, roles, or channels.").queue();

            } else if (groupName.length() > 26) {

                event.getChannel().sendMessage("Unit names cannot exceed 25 characters.").queue();

            } else if (!groupName.matches("[a-zA-Z]+$")) {

                event.getChannel().sendMessage("Unit names cannot contain special characters").queue();

            } else {

                ActiveDirectoryManagement.createUnit(event.getGuild().getId(), args[0]);

                event.getChannel().sendMessage("Successfully created group " + args[0] + ".").queue();

            }

        }

    }

}
