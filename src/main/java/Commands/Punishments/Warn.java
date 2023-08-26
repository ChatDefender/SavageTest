package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.PunishmentSQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Warn extends BaseCommand {

    public Warn() {
        super("warn", new String[] {}, "warn [@user | userId] [reason]", "Warns a user who is misbehaving in the discord.", "");
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length < 2) {

            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"warn [user id | user mention] [reason]").queue();

        } else {

            // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
            String userId = args[1];
            userId = userId.replace("<@", "").replace(">", "");

            // Now that we have an ID, verify it's a guild member
            Member member = event.getGuild().retrieveMemberById(userId).complete();

            if (member.isOwner()) {

                event.getChannel().sendMessage("I cannot warn the owner of the server.").queue();

            } else if (functions.getMentionedUserPermLevel(event, userId) >= functions.getAuthorPermLevel(event)) {

                event.getChannel().sendMessage("I cannot warn that user as they have the same permission level as you or greater.").queue();

            } else {

                // convert the rest of the arguments into a string
                StringBuilder reason = new StringBuilder();
                for (int i = 2; i < args.length; i++) {

                    reason.append(args[i]).append(" ");

                }
                String finalReason = reason.toString();

                PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                pc.sendMessage("```\nWARNED IN " + member.getGuild().getName() + "\nModerator: " +event.getAuthor().getName() + "\nReason: " + finalReason + "\n```" ).queue(
                        success -> event.getChannel().sendMessage("Sent user a private message").queue(),
                        error -> event.getChannel().sendMessage("Failed to send private message: " + error.getMessage()).queue()
                );

                event.getChannel().sendMessage("Successfully warned " + member.getEffectiveName()).queue();

                int id = PunishmentSQLFunctions.insertPunishment("warn", member.getId(), event.getAuthor().getId(), String.valueOf(0), finalReason);

                TextChannel tc = event.getGuild().getTextChannelById(ConfigurationSQLFunctions.getSetting("PunishmentLogId"));
                if (tc != null && tc.canTalk()) {

                    tc.sendMessage("```\nUSER WARNED " + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\nReason: " + finalReason + "\nPunishmentId: "+id+"```" ).queue();

                } else {

                    event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();
                }

            }

        }

    }

}
