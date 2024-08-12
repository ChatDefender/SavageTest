package Commands.ConfigureBot;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class SetPunishReset extends BaseCommand {

    public SetPunishReset() {
        super("setpunishreset", new String[] {} , "setpunishreset [new time]", "Sets the time of which punishments should be restarted after their duration - default is 24 hours.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            long duration = functions.timeToMilliseconds(args[0]);

            if (duration == -1) {

                event.getChannel().sendMessage("You provided an invalid time. Available times are as follows:\n" +
                        "#m - minutes, \n" +
                        "#h - hours, \n" +
                        "#d - days, \n" +
                        "#mon - months, \n" +
                        "#y - year, \n" +
                        "or 0 for permanent.").queue();

            } else {

                ConfigurationSettings.setSetting(
                        event.getGuild().getId(),
                        SQLFunctions.Settings.PUNISH_RESET,
                        ""+duration
                );

                event.getChannel().sendMessage("Successfully set the punish reset to "+ args[0] +".").queue();

            }

        }

    }

}
