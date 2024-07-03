package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RecoverRecord extends BaseCommand {


    public RecoverRecord() {
        super("recoverrecord", new String[] {"recrec"}, "recrec {#PunishmentLogId}", "Recovers a deleted record from the punishment log database", "", Permission.ADMINISTRATOR);
    }
    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            // check if the log exists
            if (PunishmentLogManagement.doesLogExists(Integer.parseInt(args[0]))) {

                // if it does exist, we can archive it
                PunishmentLogManagement.unarchive(args[0]);
                event.getChannel().sendMessage("Recovered record #" + Integer.parseInt(args[0])).queue();

            } else {

                // if the log does not exist
                event.getChannel().sendMessage("Could not find the record with id number: " + args[0]).queue();


            }

        }

    }

}
