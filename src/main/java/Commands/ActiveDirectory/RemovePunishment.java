package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemovePunishment extends BaseCommand {

    public RemovePunishment() {
        super("ActiveDirectory",
                "removepunishment",
                new String[] {  },
                "removepunishment [unit_name] [punishment_name]",
                "Removes a punishment from a unit.",
                "",
                Permission.ADMINISTRATOR);
    }

    @Override
    public void run (MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            if (PunishmentManagement.checkPunishmentExistence(event.getGuild().getId(), args[1].toUpperCase()) > 0 ) {

                ActiveDirectoryManagement.removePunishment(event.getGuild().getId(), args[0], args[1].toLowerCase() );

                event.getChannel().sendMessage("Successfully removed punishment.").queue();

            } else {

                event.getChannel().sendMessage("That punishment does not exist.").queue();

            }

        }

    }

}
