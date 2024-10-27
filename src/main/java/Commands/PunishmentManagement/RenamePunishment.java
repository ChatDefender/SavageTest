package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RenamePunishment extends BaseCommand {

    public RenamePunishment() {
        super("renamepunishment", new String[] {"editpunishment"}, "renamepunishment [oldName] [newName]", "Renames a punishment.", "", Permission.ADMINISTRATOR);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {
        if (args.length != 2) {
            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
        } else {
            String guildId = event.getGuild().getId();
            PunishmentManagement.renamePunishment(
                    guildId,         // guildId
                    args[0],         // oldName
                    args[1]          // newName
            );

            event.getChannel().sendMessage("Successfully renamed punishment.").queue();
        }
    }
}
