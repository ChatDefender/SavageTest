package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.PunishmentSQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteRecord extends BaseCommand {


    public DeleteRecord() {
        super("deleterecord", new String[]{"delrec"}, "delrec [#PunishmentLogId]", "Deletes a record from the punishment logs database", "", 5);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"delrec [#PunishmentLogId]").queue();

        } else {

            // check if the log exists
            if (PunishmentSQLFunctions.doesLogExists(Integer.parseInt(args[1]))) {

                if (event.getAuthor().getId().equals(PunishmentSQLFunctions.getRecordUserId(args[1]))) {

                    event.getChannel().sendMessage("You cannot delete your own punishment entry!").queue();

                } else if (functions.getMentionedUserPermLevel(event, PunishmentSQLFunctions.getStaffUserId(args[1])) >= functions.getAuthorPermLevel(event)) {

                    event.getChannel().sendMessage("You cannot delete a punishment entry from higher or equal to your current permission level!").queue();

                } else {

                    PunishmentSQLFunctions.archiveLog(args[1]);
                    event.getChannel().sendMessage("Deleted record #" + args[1]).queue();

                }

            } else {

                event.getChannel().sendMessage("Could not find record with id number: " + args[1]).queue();


            }

        }

    }

}
