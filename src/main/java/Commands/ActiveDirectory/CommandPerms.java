package Commands.ActiveDirectory;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.SQLHandlers.ActiveDirectoryManagement;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommandPerms extends BaseCommand {

    public CommandPerms() {

        super("ActiveDirectory",
                "commandperms", new String[] {}, "commandperms", "Retrieves permissions for commands", "", Permission.ADMINISTRATOR);

    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        String guildId = event.getGuild().getId();
        String commandName = "";

        switch (args.length) {
            case 1: // Only the punishment provided
                commandName = args[0].toUpperCase();
                break;
        }

        if (CommandHandler.doesCommandExist(commandName.toLowerCase())) {

            try {

                File tempFile = File.createTempFile("commandperms_", ".txt");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(ActiveDirectoryManagement.getCommandPermissions(event, guildId, commandName));
                }

                // Step 2: Send the file in the channel
                event.getChannel().sendMessage("Please review the attached for a list of permissions for commands.").queue();
                event.getChannel().sendFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(tempFile, "commandperms.txt")).queue();

                // Step 3: Clean up temporary file if necessary
                tempFile.deleteOnExit();

            } catch (IOException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("An error occurred while generating the output file.").queue();
            }

        } else {

            event.getChannel().sendMessage("Invalid command name provided.").queue();

        }

    }

}