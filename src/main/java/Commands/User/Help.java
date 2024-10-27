package Commands.User;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Handlers.SQLHandlers.PunishmentLogManagement;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Help extends BaseCommand {
    public Help() {
        super("User",
                "help", new String[] {"halp"}, "help [command name | command alias]", "Provides information on commands", "", Permission.MESSAGE_SEND);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length == 1) {

            if (CommandHandler.doesCommandExist(args[0].toLowerCase())) {

                StringBuilder sb = new StringBuilder().append("```Requester: ")
                        .append(event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete().getEffectiveName())
                        .append("\n")
                        .append(functions.buildHelpBlock(CommandHandler.getCommand(args[0].toLowerCase()).getName()).replace("```", ""))
                        .append("```");
                event.getChannel().sendMessage(sb).queue();

            } else {

                event.getChannel().sendMessage("That command does not exist!").queue();

            }

        } else {

            StringBuilder sb = new StringBuilder();

            // Step 1: Determine max column widths
            int maxCategory = "Category".length();
            int maxName = "Name".length();
            int maxDescription = "Description".length();

            for (String command : CommandHandler.getCommands()) {
                BaseCommand cmd = CommandHandler.getCommand(command);
                maxCategory = Math.max(maxCategory, cmd.getCategory().length());
                maxName = Math.max(maxName, cmd.getName().length());
                maxDescription = Math.max(maxDescription, cmd.getShortDescription().length());
            }

            // Step 2: Build header
            String formatString = String.format("| %%-%ds | %%-%ds | %%-%ds |\n", maxCategory, maxName, maxDescription);
            sb.append(String.format(formatString, "Category", "Name", "Description"))
                    .append(functions.repeat('-', maxCategory + maxName + maxDescription + 10))
                    .append("\n");

            // Step 3: Add each command in a formatted row
            for (String command : CommandHandler.getCommands()) {
                BaseCommand cmd = CommandHandler.getCommand(command);
                sb.append(String.format(formatString, cmd.getCategory(), cmd.getName(), cmd.getShortDescription()));
            }

            try {

                File tempFile = File.createTempFile("help_", ".txt");
                try (FileWriter writer = new FileWriter(tempFile)) {
                    writer.write(sb.toString());
                }

                // Step 2: Send the file in the channel
                event.getChannel().sendMessage("Please review the attached for command help.").queue();
                event.getChannel().sendFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(tempFile, "help.txt")).queue();

                // Step 3: Clean up temporary file if necessary
                tempFile.deleteOnExit();

            } catch (IOException e) {
                e.printStackTrace();
                event.getChannel().sendMessage("An error occurred while generating the output file.").queue();
            }
        }
    }

}
