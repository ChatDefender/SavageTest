package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.PunishmentSQLFunctions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearLogs extends BaseCommand {

    public ClearLogs() {
        super("clearlogs", new String[] {"clearlog"}, "clearlogs [@user | user_id]", "Can only bulk delete records from users. Cannot bulk delete staff records.", "");
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage("Command Layout: " + ConfigurationSQLFunctions.getSetting("Prefix") + "clearlogs [@user | user_id]\nNote: Only applies to users. Cannot bulk delete staff records.").queue();

        } else {

            String userId = args[1];
            userId = userId.replace("<@", "").replace(">", "");

            PunishmentSQLFunctions.bulkArchive(userId);
            event.getChannel().sendMessage("Successfully cleared logs for " + event.getGuild().retrieveMemberById(Long.parseLong(userId)).complete().getEffectiveName() + "`["+userId+"]`").queue();

        }

    }

}
