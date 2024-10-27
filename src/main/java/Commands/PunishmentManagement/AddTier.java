package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Arrays;

public class AddTier extends BaseCommand {

    public AddTier() {
        super("addtier", new String[] {}, "addtier [punishmentName] [punishmentType] [duration]", "Creates a new punishment tier.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        
        if (!(args.length >= 2)) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String[] validPunishments = {"BAN", "MUTE", "WARN", "KICK"};

            String suppliedPunishment = args[1].toUpperCase();

            if (!Arrays.asList(validPunishments).contains(suppliedPunishment)) {

                event.getChannel().sendMessage("Only valid punishments are ban, mute, kick, warn.").queue();

            } else {

                String[] noTimedPunishments = {"WARN", "KICK"};

                if(!Arrays.asList(noTimedPunishments).contains(suppliedPunishment) && args.length != 3) {

                    event.getChannel().sendMessage("For mute and ban, a time is required. Use 0 for permanent.").queue();

                } else {

                    long duration = Arrays.asList(noTimedPunishments).contains(suppliedPunishment) ? 0 : functions.timeToMilliseconds(args[2]);

                    if (duration == -1) {

                        event.getChannel().sendMessage("You provided an invalid time. Available times are as follows:\n" +
                                "#m - minutes, \n" +
                                "#h - hours, \n" +
                                "#d - days, \n" +
                                "#mon - months, \n" +
                                "#y - year, \n" +
                                "or 0 for permanent.").queue();

                    } else {

                        String guildId = event.getGuild().getId();
                        PunishmentManagement.createPunishmentTier(
                                guildId,         // guildId
                                args[0],         // punishmentName
                                args[1],         // punishmentType
                                duration         // duration
                        );

                        event.getChannel().sendMessage("Successfully created tier").queue();

                    }

                }

            }

        }

    }

}