package Commands.Punishments;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

public class Unban {

    public void unbanUser(MessageReceivedEvent event, String[] args) {

        if (args.length < 1) {

            event.getChannel().sendMessage("Command Layout: s!unban [user id]").queue();

        } else {

            User user;

            try {
                user = event.getJDA().retrieveUserById(args[1]).complete();

                if (user == null) {

                    event.getChannel().sendMessage("I could not find that member.").queue();

                } else {

                    boolean exists = false;

                    for (Guild.Ban b: event.getGuild().retrieveBanList().complete()) {

                        if (b.getUser().getId().equals(user.getId())) {

                            exists = true;
                            break;

                        }

                    }

                    if (exists) {

                        event.getGuild().unban(user).queue(

                                success -> event.getChannel().sendMessage("Successfully unbanned " + user.getName()).queue(),
                                error -> event.getChannel().sendMessage("Failed to unban user: " + error.getMessage()).queue()

                        );

                        TextChannel tc = event.getGuild().getTextChannelById("1132823318337179760");
                        if (tc != null && tc.canTalk()) {

                            tc.sendMessage("```\nUSER UNBANNED " +  "\nUser: " + user.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "\n```" ).queue();

                        } else {

                            event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();
                        }


                    } else {

                        event.getChannel().sendMessage("That user is not banned.").queue();

                    }

                }

            } catch (ErrorResponseException e) {

                e.printStackTrace();

                if (e.getErrorCode() == 10013) {

                    event.getChannel().sendMessage("I could not find the member specified.").queue();

                }

            }

        }

    }

}
