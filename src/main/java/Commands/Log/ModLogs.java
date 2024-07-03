package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import Main.*;

public class ModLogs extends BaseCommand {

    public ModLogs() {

        super("modlogs", new String[] {"modlog"}, "modlog {@user | user id} {-u(ser) | -s(taff)} {punishment type} {-a(rchived) | -sa(show all)}", "Retrieves records from the punishment log database.", "\nQueries the database for punishments\n-u is the user punished\n-s is the staff member responsible for the punishents\n-a will search only archived entries", Permission.ADMINISTRATOR);

    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length < 3) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
            String userId = args[0];
            userId = userId.replace("<@", "").replace(">", "");

            // determine if it is staff or is user
            boolean isUser = args[1].equalsIgnoreCase("-u") || args[1].equalsIgnoreCase("-user");
            boolean isStaff = args[1].equalsIgnoreCase("-s") || args[1].equalsIgnoreCase("-staff");

            // determine the punishment type
            SQLFunctions.Punishments punType;

            String suppliedPunType = args[2].toLowerCase();

            switch (suppliedPunType) {
                case "mute":
                    punType = SQLFunctions.Punishments.MUTE;
                    break;

                case "ban":
                    punType = SQLFunctions.Punishments.BAN;
                    break;

                case "warn":
                    punType = SQLFunctions.Punishments.WARN;
                    break;

                case "kick":
                    punType = SQLFunctions.Punishments.KICK;
                    break;

                case "all":
                    punType = SQLFunctions.Punishments.ALL;
                    break;

                default:
                    punType = SQLFunctions.Punishments.ALL;

            }

            // determine show all or archive

            boolean showAll = false;
            if (args.length == 4) {

                showAll = args[3].equalsIgnoreCase("-sa") || args[3].equalsIgnoreCase("-showall");

            }

            if (isUser) {

                event.getChannel().sendMessage("```"+ PunishmentLogManagement.getPunishmentLogsForUser(event.getGuild().getId(), userId, punType, showAll) +"```").queue();

            } else if (isStaff) {

                event.getChannel().sendMessage("```" + PunishmentLogManagement.getPunishmentLogsForStaff(event.getGuild().getId(), userId, punType, showAll)).queue();

            } else {

                event.getChannel().sendMessage("Please specify a -user or -staff records to look up!").queue();

            }


        }

    }

}
