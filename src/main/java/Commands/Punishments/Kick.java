package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.executePunishment;

public class Kick extends BaseCommand {
    public Kick() {
        super("Punishment",
                "kick", new String[] {"remove", "buhbye"}, "kick {@user | userId} {reason}", "Removes a member from the server.", "", Permission.KICK_MEMBERS);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        // Verify text is provided in the arguments
        if (args.length < 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String user = args[0];
            StringBuilder reason = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            String finalReason = reason.toString();

            executePunishment(SQLFunctions.Punishments.KICK, event, user, "0", finalReason);


        }

    }

}