package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ModLogs extends BaseCommand {

    public ModLogs() {

        super("PunishmentLogs",
                "modlogs", new String[] {"modlog", "punlog"}, "modlog [@user | user id] (punishment type) (-s(taff)) (-a(rchived))", "Retrieves records from the punishment log database.", "\nQueries the database for punishments\n-s is the staff member responsible for the punishents\n-a will include archived entries", Permission.ADMINISTRATOR);

    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        String guildId = event.getGuild().getId();
        String userId = "";
        boolean isStaff = false;
        SQLFunctions.Punishments punType = SQLFunctions.Punishments.valueOf("ALL");
        boolean includeArchived = false;

        boolean isErr = false;

        switch (args.length) {
            case 0: // No arguments provided
                event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();
                break;

            case 1: // Only the user provided
                userId = args[0].replaceAll("[^0-9]", ""); // Remove non-numeric characters
                break;

            case 2: // User and punishment type
                userId = args[0].replaceAll("[^0-9]", "");
                try {
                    punType = SQLFunctions.Punishments.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.getChannel().sendMessage("Invalid punishment type: " + args[1]).queue();
                    isErr = true;
                    return;
                }
                break;

            case 3: // User, punishment type, and staff flag
                userId = args[0].replaceAll("[^0-9]", "");
                try {
                    punType = SQLFunctions.Punishments.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.getChannel().sendMessage("Invalid punishment type: " + args[1]).queue();
                    isErr = true;
                    return;
                }
                isStaff = args[2].equalsIgnoreCase("-s") || args[2].equalsIgnoreCase("-staff");
                break;

            case 4: // All arguments provided
                userId = args[0].replaceAll("[^0-9]", "");
                try {
                    punType = SQLFunctions.Punishments.valueOf(args[1].toUpperCase());
                } catch (IllegalArgumentException e) {
                    event.getChannel().sendMessage("Invalid punishment type: " + args[1]).queue();
                    isErr = true;
                    return;
                }
                isStaff = args[2].equalsIgnoreCase("-s") || args[2].equalsIgnoreCase("-staff");
                includeArchived = args[3].equalsIgnoreCase("-a") || args[3].equalsIgnoreCase("-archived") || args[3].equalsIgnoreCase("-archive");
                break;

            default:
                // Too many arguments
                event.getChannel().sendMessage("Too many arguments provided. Please check the command syntax.").queue();
                return;
        }

        // Log or further process the extracted variables: userId, punType, isStaff, includeArchived
        if (!isErr) {

            try {

                File tempFile = File.createTempFile("modlogs_", ".txt");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(PunishmentLogManagement.getPunishmentLogs(event, guildId, userId, isStaff, punType.toString(), includeArchived).toString());
                }

                // Step 2: Send the file in the channel
                event.getChannel().sendMessage("Please review the attached for punishment logs.").queue();
                event.getChannel().sendFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(tempFile, "modlogs.txt")).queue();

                // Step 3: Clean up temporary file if necessary
                tempFile.deleteOnExit();

            } catch (IOException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("An error occurred while generating the output file.").queue();
            }

        }

    }

}
