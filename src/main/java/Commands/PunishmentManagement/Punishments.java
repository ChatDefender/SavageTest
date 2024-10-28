package Commands.PunishmentManagement;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentManagement;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Punishments extends BaseCommand {

    public Punishments() {

        super("PunishmentManagement",
                "punishments", new String[] {""}, "punishments (punishment name)", "Retrieves the punishment tiers for all or a specified punishment", "", Permission.ADMINISTRATOR);

    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        String guildId = event.getGuild().getId();
        String punishmentName = "";

        switch (args.length) {
            case 1: // Only the punishment provided
                punishmentName = args[0].toUpperCase();
                break;
        }

        try {

            File tempFile = File.createTempFile("punishments_", ".txt");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(PunishmentManagement.getPunishmentTiers(guildId, punishmentName));
            }

            // Step 2: Send the file in the channel
            event.getChannel().sendMessage("Please review the attached for a list of available punishments").queue();
            event.getChannel().sendFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(tempFile, "punishments.txt")).queue();

            // Step 3: Clean up temporary file if necessary
            tempFile.deleteOnExit();

        } catch (IOException e) {
            e.printStackTrace();
            event.getChannel().sendMessage("An error occurred while generating the output file.").queue();
        }

    }

}
