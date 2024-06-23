package Commands.Configuration.ConfigureBot;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.verifyRole;

public class SetMutedRole extends BaseCommand {


    public SetMutedRole() {
        super("setmutedrole", new String[] {} , "setmutedrole [@role | role_id]", "Sets the mute role.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String roleId = verifyRole(event, args[0]);

            if (roleId != null) {

                ConfigurationSettings.setSetting(
                        event.getGuild().getId(),
                        SQLFunctions.Settings.MUTEDROLEID,
                        roleId
                );

                event.getChannel().sendMessage("Successfully set the muted role id to "+ roleId +".").queue();

            } else {

                event.getChannel().sendMessage("There was a problem with setting the muted role. Please ensure you provide a valid role ID or mention a role.").queue();

            }

        }

    }

}
