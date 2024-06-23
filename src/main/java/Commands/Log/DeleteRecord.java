package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteRecord extends BaseCommand {

    public DeleteRecord() {
        super("deleterecord", new String[]{"delrec"}, "delrec {#PunishmentLogId}", "Deletes a record from the punishment logs database", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            // check if the log exists
            if (PunishmentManagement.doesLogExists(Integer.parseInt(args[0]))) {

                if (event.getAuthor().getId().equals(PunishmentManagement.getRecordUserId(args[0]))) {

                    event.getChannel().sendMessage("You cannot delete your own punishment entry!").queue();

                } else {

                    PunishmentManagement.archiveLog(args[0]);
                    event.getChannel().sendMessage("Deleted record #" + args[0]).queue();

                }

            } else {

                event.getChannel().sendMessage("Could not find record with id number: " + args[0]).queue();


            }

        }

    }

}
