package Commands.Log;

import CustomerFunctions.ConfigurationSQLFunctions;
import CustomerFunctions.PunishmentSQLFunctions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RecoverRecord {


    public void recRec(MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"recrec [#PunishmentLogId]").queue();

        } else {

            // check if the log exists
            if (PunishmentSQLFunctions.doesLogExists(Integer.parseInt(args[1]))) {

                // if it does exist, we can archive it
                PunishmentSQLFunctions.recoverLog(args[1]);
                event.getChannel().sendMessage("Recovered record #" + args[1]).queue();

            } else {

                // if the log does not exist
                event.getChannel().sendMessage("Could not find the record with id number: " + args[1]).queue();


            }

        }

    }
}
