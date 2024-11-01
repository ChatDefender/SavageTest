package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DeletePunishment extends BaseCommand {

    public DeletePunishment() {
        super("PunishmentManagement",
                "deletepunishment", new String[] {}, "deletepunishment [punishmentName]", "Deletes an existing punishment.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        if (args.length != 1) {
            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
        } else {
            String guildId = event.getGuild().getId();
            PunishmentManagement.deletePunishment(
                    guildId,       // guildId
                    args[0]        // punishmentName
            );

            event.getChannel().sendMessage("Successfully deleted punishment.").queue();

        }

    }

}
