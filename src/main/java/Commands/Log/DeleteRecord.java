package Commands.Log;

import CustomerFunctions.ConfigurationSQLFunctions;
import CustomerFunctions.PunishmentSQLFunctions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteRecord {


    public void delRec(MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"delrec [#PunishmentLogId]").queue();

        } else {

            // check if the log exists
            if (PunishmentSQLFunctions.doesLogExists(Integer.parseInt(args[1]))) {

                // if it does exist, we can archive it
                PunishmentSQLFunctions.archiveLog(args[1]);
                event.getChannel().sendMessage("Deleted record #" + args[1]).queue();

            } else {

                // if the log does not exist
                event.getChannel().sendMessage("Could not find record with id number: " + args[1]).queue();


            }

        }

    }

}
