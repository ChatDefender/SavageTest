package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeleteLogs extends BaseCommand {

    public DeleteLogs() {
        super("deletelogs", new String[] {}, "deletelogs {@user | user_id} {-staff}", "Archives logs from a specified users.", "Can only bulk delete records from users. Cannot bulk delete staff records.", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length < 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String userId = functions.getUserId(event, args[0]);

            if (userId != null) {

                User u = event.getJDA().retrieveUserById(userId).complete();

                if (event.getAuthor().getId().equals(u.getId())) {

                    event.getChannel().sendMessage("You cannot clear your own logs!").queue();

                } else {

                    String name = u.getName();

                    int isStaff = 0;

                    if (args.length == 2 ) {

                        isStaff = (args[1].equalsIgnoreCase("-s") || args[1].equalsIgnoreCase("-staff")) ? 1 : 0;

                    }

                    PunishmentLogManagement.bulkArchivePunishments(event.getGuild().getId(), isStaff, u.getId());

                    event.getChannel().sendMessage("Successfully cleared logs for " + name + "`[" + u.getId() + "]`").queue();

                }

            } else {

                event.getChannel().sendMessage("Invalid user provided").queue();

            }

        }

    }

}
