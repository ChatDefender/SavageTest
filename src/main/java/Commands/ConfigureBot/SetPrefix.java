package Commands.ConfigureBot;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;


public class SetPrefix extends BaseCommand {

    public SetPrefix() {
        super("Configuration",
                "setprefix", new String[] {} , "setprefix [new prefix]", "Sets the prefix.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            ConfigurationSettings.updateSettings(
                    event.getGuild().getId(),
                    SQLFunctions.Settings.PREFIX,
                    args[0]
            );

            event.getChannel().sendMessage("Successfully set the prefix to "+ args[0] +".").queue();

        }

    }

}
