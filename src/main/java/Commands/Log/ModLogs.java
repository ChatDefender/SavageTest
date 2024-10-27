package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import Main.*;

public class ModLogs extends BaseCommand {

    public ModLogs() {

        super("modlogs", new String[] {"modlog", "punlog"}, "modlog [@user | user id] [-user | -staff] [punishment type] (-archived)", "Retrieves records from the punishment log database.", "\nQueries the database for punishments\n-u is the user punished\n-s is the staff member responsible for the punishents\n-a will search only archived entries", Permission.ADMINISTRATOR);

    }
//
//    @Override
//    public void run(MessageReceivedEvent event, String[] args) {
//
//        if (args.length < 3) {
//
//            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
//
//        } else {
//
//            /// Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
//            String userId = args[0];
//            userId = userId.replace("<@", "").replace(">", "");
//
//            // Determine if it is staff or user
//            boolean isUser = args[1].equalsIgnoreCase("-u") || args[1].equalsIgnoreCase("-user");
//            boolean isStaff = args[1].equalsIgnoreCase("-s") || args[1].equalsIgnoreCase("-staff");
//
//            // Determine the punishment type
//            SQLFunctions.Punishments punType;
//
//            String suppliedPunType = args[2].toLowerCase();
//
//            switch (suppliedPunType) {
//                case "mute":
//                    punType = SQLFunctions.Punishments.MUTE;
//                    break;
//                case "ban":
//                    punType = SQLFunctions.Punishments.BAN;
//                    break;
//                case "warn":
//                    punType = SQLFunctions.Punishments.WARN;
//                    break;
//                case "kick":
//                    punType = SQLFunctions.Punishments.KICK;
//                    break;
//                case "unmute":
//                    punType = SQLFunctions.Punishments.UNMUTE;
//                    break;
//                case "unban":
//                    punType = SQLFunctions.Punishments.UNBAN;
//                    break;
//                default:
//                    punType = SQLFunctions.Punishments.ALL;
//                    break;
//            }
//
//            // Determine whether to show all or archived logs
//            boolean isArchived = false;
//            if (args.length == 4) {
//                isArchived = args[3].equalsIgnoreCase("-a") || args[3].equalsIgnoreCase("-archive");
//            }
//
//            if (isUser || isStaff) {
//                // Use the unified method to get punishment logs
//                String result = PunishmentLogManagement.(
//                        event.getGuild().getId(),
//                        userId,
//                        isStaff, // Use isStaff to determine if it's for a staff member
//                        punType,
//                        isArchived
//                );
//
//                // Send the result as a message
//                if (result.startsWith("PL_")) {
//
//
//
//                } else {
//
//                    event.getChannel().sendMessage(result).queue();
//
//                }
//            } else {
//                event.getChannel().sendMessage("Please specify whether to look up -user or -staff records!").queue();
//            }
//
//
//        }
//
//    }

}
