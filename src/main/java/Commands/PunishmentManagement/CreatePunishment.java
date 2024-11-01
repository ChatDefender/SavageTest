package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CreatePunishment extends BaseCommand {

    public CreatePunishment() {
        super("PunishmentManagement",
                "createpunishment", new String[] {"addpunishment"}, "createpunishment [punishmentName]", "Creates a new punishment.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String guildId = event.getGuild().getId();
            String createUserId = event.getAuthor().getId();
            PunishmentManagement.createPunishment(
                    guildId,       // guildId
                    args[0],       // punishmentName
                    createUserId   // createUserId
            );

            event.getChannel().sendMessage("Successfully created punishment.").queue();

        }

    }

}
