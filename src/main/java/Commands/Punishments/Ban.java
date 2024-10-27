package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.executePunishment;

public class Ban extends BaseCommand {

    public Ban() {
        super("ban", new String[]{"banish", "banana", "bananahammer"}, "ban {@user | userId} {duration} {reason}", "Bans a user from the discord server", "Available times are in (m)inutes, (h)ours, (d)ays, (mon)ths, (y)ear, or 0 for permanent.", Permission.BAN_MEMBERS);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length <= 2) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String user = args[0];
            String duration = args[1];
            StringBuilder reason = new StringBuilder();
            for (int i = 2; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            String finalReason = reason.toString();

            executePunishment(SQLFunctions.Punishments.BAN, event, user, duration, finalReason);


        }

    }

}

