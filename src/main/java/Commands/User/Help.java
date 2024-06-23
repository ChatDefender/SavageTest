package Commands.User;

import Commands.BaseCommand;
import Handlers.CommandHandler;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Help extends BaseCommand {
    public Help() {
        super("help", new String[] {"halp", "idkwiad"}, "help [command name | command alias]", "Provides information on commands", "", Permission.MESSAGE_SEND);
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


            int longestString = 0;

            for (String s : CommandHandler.getCommands()) {

                if (s.length() >= longestString) {

                    longestString = s.length();

                }

            }

            sb.append("```Requesting User: ").append(event.getAuthor().getName()).append("\n").append("\nCommand").append(new String(new char[longestString - 7])).append("  | Short Description").append("\n");

            for (String s : CommandHandler.getCommands()) {

                int repeat = longestString - s.length();

                sb.append(s).append(new String(new char[repeat]).replace("\0", " ")).append(" | ").append(CommandHandler.getCommand(s).getShortDescription()).append("\n");

            }

            sb.append("\n```");

            event.getChannel().sendMessage(sb.toString()).queue();

        }

    }
}
