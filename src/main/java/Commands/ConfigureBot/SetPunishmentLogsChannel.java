package Commands.ConfigureBot;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.verifyChannel;

public class SetPunishmentLogsChannel extends BaseCommand {
    public SetPunishmentLogsChannel() {
        super("setpunishmentlogchannel", new String[] {"setpunlogchannel"} , "setpunishmentlogchannel [#channel | channel_id]", "Sets the puinishment log channel id.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String channelId = verifyChannel(event, args[0]);

            if (channelId != null) {

                ConfigurationSettings.updateSettings(
                        event.getGuild().getId(),
                        SQLFunctions.Settings.PUNISHMENT_LOG_ID,
                        channelId
                );

                event.getChannel().sendMessage("Successfully set the punishment log channel id to "+ channelId +".").queue();

            } else {

                event.getChannel().sendMessage("There was a problem with setting the punishment log channel. Please ensure you provide a valid Text Channel ID or mention a channel.").queue();

            }

        }

    }

}
