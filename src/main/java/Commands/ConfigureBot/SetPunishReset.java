package Commands.ConfigureBot;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class SetPunishReset extends BaseCommand {

    public SetPunishReset() {
        super("Configuration",
                "setpunishreset", new String[] {} , "setpunishreset [new time]", "When punishments should reset - default is 24h", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            long duration = functions.timeToMilliseconds(args[0]);

            System.out.println(duration);

            if (duration == -1) {

                event.getChannel().sendMessage("You provided an invalid time. Available times are as follows:\n" +
                        "#m - minutes, \n" +
                        "#h - hours, \n" +
                        "#d - days, \n" +
                        "#mon - months, \n" +
                        "#y - year, \n" +
                        "or 0 for permanent.").queue();

            } else {

                ConfigurationSettings.updateSettings(
                        event.getGuild().getId(),
                        SQLFunctions.Settings.PUNISHMENT_RESET,
                        ""+duration
                );

                event.getChannel().sendMessage("Successfully set the punish reset to "+ args[0] +".").queue();

            }

        }

    }

}
