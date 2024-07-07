package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.executePunishment;

public class Warn extends BaseCommand {

    public Warn() {
        super("warn", new String[] {}, "warn {@user | userId} {reason}", "Warns a user who is misbehaving in the discord.", "", Permission.MANAGE_ROLES);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length <= 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String user = args[0];
            StringBuilder reason = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                reason.append(args[i]).append(" ");
            }
            String finalReason = reason.toString();

            executePunishment(SQLFunctions.Punishments.WARN, event, user, "0", finalReason);

        }

    }

}
