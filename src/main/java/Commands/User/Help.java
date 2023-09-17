package Commands.User;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Main.functions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help extends BaseCommand {
    public Help() {
        super("help", new String[] {"halp", "idkwiad"}, "help [command name | command alias]", "Provides information on commands", "", 0);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        int authorPermissionLevel = functions.getAuthorPermLevel(event);

        if (args.length == 2) {

            if (CommandHandler.doesCommandExist(args[1].toLowerCase())) {

                if (authorPermissionLevel >= functions.getCommandPermLvl(args[1].toLowerCase())) {

                    StringBuilder sb = new StringBuilder().append("```");

                    BaseCommand command = CommandHandler.getCommand(args[1].toLowerCase());
                    sb.append("Command Help: ").append(command.getName()).append("\n");
                    sb.append("Permission Level: ").append(functions.getCommandPermLvl(command.getName())).append("\n");
                    sb.append("Name: ").append(command.getName()).append("\n");

                    sb.append("Alias: ");
                    for (String s : command.getAliases()) {

                        sb.append(s).append(", ");

                    }

                    sb.deleteCharAt(sb.length()-1).append("\n");
                    sb.append("Usage: ").append(command.getUsage()).append("\n");
                    sb.append("Short Description: ").append(command.getShortDescription()).append("\n");
                    sb.append("Long Description: ").append(command.getLongDescription()).append("\n");
                    sb.append("```");

                    event.getChannel().sendMessage("```\nRequester: " + event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete().getEffectiveName() + "\n" + sb + "```" ).queue();

                } else {

                    event.getChannel().sendMessage("You do not have permissions to look at the help for that command.").queue();

                }

            } else {

                event.getChannel().sendMessage("That command does not exist!").queue();

            }

        } else {


            StringBuilder sb = new StringBuilder();

            sb.append("Command | Short Description").append("\n");

            for (String s : CommandHandler.getCommands()) {

                int commandPermissionLevel = functions.getCommandPermLvl(s);

                if (authorPermissionLevel >= commandPermissionLevel) {

                    sb.append(s).append(" | ").append(CommandHandler.getCommand(s).getShortDescription()).append("\n");

                }

            }

            event.getChannel().sendMessage(sb.toString()).queue();

        }

    }
}
