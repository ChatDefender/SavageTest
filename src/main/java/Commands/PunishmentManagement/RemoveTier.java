package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RemoveTier extends BaseCommand {

    public RemoveTier() {
        super("PunishmentManagement",
                "removetier", new String[] {}, "removetier [punishmentName] [punishmentTier]", "Removes a punishment tier.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        if (args.length != 2) {
            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
        } else {
            String guildId = event.getGuild().getId();
            PunishmentManagement.removePunishmentTier(
                    guildId,         // guildId
                    args[0],         // punishmentName
                    Integer.parseInt(args[1])  // punishmentTier
            );

            event.getChannel().sendMessage("Successfully removed punishment tier.").queue();
        }
    }
}
