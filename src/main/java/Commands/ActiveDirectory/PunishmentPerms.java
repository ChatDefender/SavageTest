package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import Handlers.SQLHandlers.PunishmentManagement;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PunishmentPerms extends BaseCommand {

    public PunishmentPerms() {

        super("ActiveDirectory",
                "punishmentperms", new String[] {}, "punishmentperms", "Retrieves permissions for punishments", "", Permission.ADMINISTRATOR);

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

        if (PunishmentManagement.checkPunishmentExistence(guildId, punishmentName) > 0 || punishmentName.isEmpty() ) {

            try {

                File tempFile = File.createTempFile("punishmentperms_", ".txt");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(ActiveDirectoryManagement.getPunishmentPermissions(event, guildId, punishmentName));
                }

                // Step 2: Send the file in the channel
                event.getChannel().sendMessage("Please review the attached for a list of permissions for punishments.").queue();
                event.getChannel().sendFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(tempFile, "punishmentperms.txt")).queue();

                // Step 3: Clean up temporary file if necessary
                tempFile.deleteOnExit();

            } catch (IOException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("An error occurred while generating the output file.").queue();
            }

        } else {

            event.getChannel().sendMessage("Invalid punishment name provided.").queue();

        }




    }

}
