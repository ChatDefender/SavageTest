package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteRecord extends BaseCommand {

    public DeleteRecord() {
        super("PunishmentLogs",
                "deleterecord", new String[]{"delrec"}, "delrec {#PunishmentLogId}", "Deletes a record from the punishment logs database", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            // check if the log exists
            if (PunishmentLogManagement.doesPunishmentLogExist(event.getGuild().getId(), Integer.parseInt(args[0]))) {

                if (event.getAuthor().getId().equals(PunishmentLogManagement.getStaffIdFromLog(args[0])) || event.getAuthor().getId().equals(PunishmentLogManagement.getUserIdFromLog(args[0])) ) {

                    event.getChannel().sendMessage("You cannot delete your own punishment entry!").queue();

                } else {

                    PunishmentLogManagement.archivePunishmentLog(args[0]);
                    event.getChannel().sendMessage("Deleted record #" + args[0]).queue();

                }

            } else {

                event.getChannel().sendMessage("Could not find record with id number: " + args[0]).queue();


            }

        }

    }

}
