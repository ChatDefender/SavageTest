package Events;

import Handlers.CommandHandler;
import Handlers.SQLHandlers.CommandManagement;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageEvent extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {

            // Classifiers
            Message message = event.getMessage();
            User author = event.getAuthor();

            // If the message is from a bot, we want to ignore it.
            if (!author.isBot()) {

                // If the message is from a text channel, this is where we want to filter it by type
                if (event.isFromType(ChannelType.TEXT)) {

                    String prefix = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PREFIX);
                    String commandLine = message.getContentRaw();

                    if (commandLine.startsWith(prefix) || event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser())) {

                        commandLine = commandLine.substring(prefix.length());
                        String[] parts = commandLine.split("\\s+", 2);
                        String command = parts[0];

                        String[] args;
                        if (parts.length > 1) {

                            String argsString = parts[1];
                            args = argsString.split("\\s+");

                        } else {

                            args = new String[0];

                        }

                        if (CommandHandler.doesCommandExist(command)) {

                            boolean hasPerms = functions.hasCmdPerms(event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete(), event.getGuild().getId(), command);

                            if (hasPerms) {

                                int cmdLogId = CommandManagement.insertCommandLog(
                                        event.getGuild().getId(),
                                        event.getChannel().getId(),
                                        event.getAuthor().getId(),
                                        CommandHandler.getCommand(command).getName(),
                                        1
                                );

                                try {

                                    // Attempt to execute the command
                                    CommandHandler.executeCommand(command.toLowerCase(), event, args);

                                } catch (Exception e) {

                                    // Log the error to the database
                                    CommandManagement.logCommandError(cmdLogId, command + ".java", e.getMessage());

                                    e.printStackTrace();

                                    // Capture the stack trace as a string
                                    StringWriter sw = new StringWriter();
                                    e.printStackTrace(new PrintWriter(sw));
                                    String stackTrace = sw.toString();

                                    // Send stack trace to the specified guild and channel
                                    event.getJDA().getGuildById("1132678375660585061")
                                            .getTextChannelById("1299744552092962816")
                                            .sendMessage("```" + stackTrace + "```")
                                            .queue();

                                    // Respond in chat with a high-level error message
                                    event.getChannel().sendMessage("An unexpected error occurred while executing the command. Please try again later.").queue();
                                }

                            } else {

                                int cmdLogId = CommandManagement.insertCommandLog(
                                        event.getGuild().getId(),
                                        event.getChannel().getId(),
                                        event.getAuthor().getId(),
                                        CommandHandler.getCommand(command).getName(),
                                        0
                                );

                                // Log that the user did not have permissions to run this command
                                CommandManagement.logCommandError(cmdLogId, "MessageEvent.java", "User " + event.getAuthor().getId() + " did not have permissions to run this command.");

                                // Notify the user about the lack of permissions
                                event.getChannel().sendMessage("You do not have the necessary permissions to run this command.").queue();
                            }

                        }

                    }

                }

            }

        }

    }

}