package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.PunishmentSQLFunctions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ClearLogs extends BaseCommand {

    public ClearLogs() {
        super("clearlogs", new String[] {"clearlog"}, "clearlogs [@user | user_id]", "Archives logs from a specified users.", "Can only bulk delete records from users. Cannot bulk delete staff records.", 5);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 2) {

            event.getChannel().sendMessage("Command Layout: " + ConfigurationSQLFunctions.getSetting("Prefix") + "clearlogs [@user | user_id]\nNote: Only applies to users. Cannot bulk delete staff records.").queue();

        } else {

            String userId = args[1];
            userId = userId.replace("<@", "").replace(">", "");

            if (event.getAuthor().getId().equals(userId)) {

                event.getChannel().sendMessage("You cannot clear your own logs!").queue();

            } else {

                Member m = event.getGuild().retrieveMemberById(Long.parseLong(userId)).complete();

                String name;

                if (m != null)

                    name = m.getEffectiveName();

                else {

                    User u = event.getJDA().retrieveUserById(userId).complete();

                    if (u != null) {

                        name = u.getName();

                    } else {

                        name = "Unknown User";

                    }
                }
                PunishmentSQLFunctions.bulkArchive(userId);
                event.getChannel().sendMessage("Successfully cleared logs for " + name + "`["+userId+"]`").queue();

            }

        }

    }

}
