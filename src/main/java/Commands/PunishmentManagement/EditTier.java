package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class EditTier extends BaseCommand {

    public EditTier() {
        super("edittier", new String[] {}, "edittier [punishmentName] [punishmentTier] [columnName] [newValue]", "Edits a punishment tier.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        if (args.length != 4) {
            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
        } else {

            String newValueFormat = args[2].equalsIgnoreCase("punishment_type") ? args[3] : String.valueOf(functions.timeToMilliseconds(args[3]));

            String guildId = event.getGuild().getId();
            String result = PunishmentManagement.editTier(
                    guildId,         // guildId
                    args[0],         // punishmentName
                    Integer.parseInt(args[1]),  // punishmentTier
                    args[2],         // columnName
                    newValueFormat          // newValue
            );

            event.getChannel().sendMessage("Edit Tier Result: " + result).queue();
        }
    }
}
