package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class EditTier extends BaseCommand {

    public EditTier() {
        super("PunishmentManagement",
                "edittier", new String[] {}, "edittier [punishmentName] [punishmentTier] [columnName] [newValue]", "Edits a punishment tier.", "Valid column names are time and type.", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        if (args.length != 4) {
            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
        } else {

            String[] availableColumnNames = {"time", "type"};

            String suppliedColumn = args[2].toLowerCase();

            if (!Arrays.asList(availableColumnNames).contains(suppliedColumn)) {

                event.getChannel().sendMessage("Valid column names are time and type.").queue();

            } else {

                boolean errFree = true;

                String newValueFormat= "";

                if (args[2].equalsIgnoreCase("type")) {

                    String[] availablePunishments = {"kick", "ban", "mute", "warn"};

                    String suppliedPunishment = args[3].toLowerCase();

                    if (!Arrays.asList(availablePunishments).contains(suppliedPunishment)) {

                        event.getChannel().sendMessage("Valid punishment types are kick, ban, mute, and warn.").queue();

                        errFree = false;

                    }



                } else {

                    newValueFormat = String.valueOf(functions.timeToMilliseconds(args[3]));

                    if (newValueFormat.equals("-1")) {

                        event.getChannel().sendMessage("You provided an invalid time. Available times are as follows:\n" +
                                "#m - minutes, \n" +
                                "#h - hours, \n" +
                                "#d - days, \n" +
                                "#mon - months, \n" +
                                "#y - year, \n" +
                                "or 0 for permanent.").queue();

                        errFree = false;

                    }

                }

                if (errFree) {

                    newValueFormat = args[2].equalsIgnoreCase("type") ? args[3].toLowerCase() : String.valueOf(functions.timeToMilliseconds(args[3]));

                    String guildId = event.getGuild().getId();
                    PunishmentManagement.editPunishmentTier(
                            guildId,         // guildId
                            args[0],         // punishmentName
                            Integer.parseInt(args[1]),  // punishmentTier
                            "punishment_"+args[2].toLowerCase(),         // columnName
                            newValueFormat          // newValue
                    );

                    event.getChannel().sendMessage("Successfully edited tier.").queue();

                }

            }

        }
    }
}
