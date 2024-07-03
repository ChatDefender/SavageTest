package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreateTier extends BaseCommand {

    public CreateTier() {
        super("createtier", new String[] {}, "createtier [punishmentName] [punishmentType] [duration]", "Creates a new punishment tier.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        if (args.length != 3) {
            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
        } else {
            String guildId = event.getGuild().getId();
            String result = PunishmentManagement.createTier(
                    guildId,         // guildId
                    args[0],         // punishmentName
                    args[1],         // punishmentType
                    Integer.parseInt(args[2])  // duration
            );

            event.getChannel().sendMessage("Create Tier Result: " + result).queue();
        }
    }
}
