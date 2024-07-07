package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static Main.functions.executePunishment;

public class Mute extends BaseCommand {

    public Mute() {
        super("mute", new String[] {"silence", "bequiet", "shush"}, "mute {@user | userId} {duration} {reason}", "Mutes a specified user for a specified duration", "\nAvailable times are in (m)inutes, (h)ours, (d)ays, (mon)ths, (y)ear, or 0 for indefinite.\nUsed when someone is being naughty in the chatty, or tickles rose on the wrong day", Permission.MANAGE_ROLES);    }

    public void run(MessageReceivedEvent event, String[] args) {


        // Verify text is provided in the arguments
        if (args.length <= 3) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {



            String muteRoleId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.MUTEDROLEID);

            if (muteRoleId.isEmpty()) {

                event.getChannel().sendMessage("The mute role needs to be configured. Run `"+ ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PREFIX)+"config muteroleid [@mutedRole | mute role id]`").queue();

            } else if (event.getGuild().getRoleById(muteRoleId) == null) {

                event.getChannel().sendMessage("I could not find the mute role. Please reset it using the setmutedrole command").queue();

            } else {

                String user = args[0];
                String duration = args[1];
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    reason.append(args[i]).append(" ");
                }
                String finalReason = reason.toString();

                executePunishment(SQLFunctions.Punishments.MUTE, event, user, duration, finalReason);

            }

        }

    }

}
