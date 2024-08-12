package Commands.Punishments;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSettings;
import Handlers.SQLHandlers.SQLFunctions;
import Main.functions;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Unmute extends BaseCommand {


    public Unmute() {
        super("unmute", new String[] {}, "unmute {@user | userId}", "Unmutes a user", "Used when someone finally started acting right, removed the permanent mute, or other reasons", Permission.MANAGE_ROLES);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        // Verify text is provided in the arguments
        if (args.length != 1) {

            event.getChannel().sendMessage(functions.buildHelpBlock(this.getName())).queue();

        } else {

            String muteRoleId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.MUTEDROLEID);

            if (muteRoleId.isEmpty()) {

                event.getChannel().sendMessage("The mute role needs to be configured. Run `"+ ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PREFIX)+"setmuteroleid [@mutedRole | mute role id]`").queue();

            } else {

                Role r = event.getGuild().getRoleById(Long.parseLong(muteRoleId));

                if (r == null) {

                    event.getChannel().sendMessage("Cannot find the Muted role.").queue();

                } else {

                    // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
                    String userId = functions.getUserId(event, args[0]);

                    // Now that we have an ID, verify it's a guild member
                    Member member = event.getGuild().retrieveMemberById(userId).complete();

                    if (event.getGuild().retrieveMemberById(member.getId()).complete().getRoles().contains(r)) {

                        event.getGuild().removeRoleFromMember(member, r).queue();

                        PrivateChannel pc = member.getUser().openPrivateChannel().complete();
                        pc.sendMessage("```\nUNMUTED IN " + member.getGuild().getName() + "\nModerator: " + event.getAuthor().getName() + "```").queue();

                        event.getChannel().sendMessage("Successfully unmuted " + member.getEffectiveName()).queue();

                        String punishmentLogChannelId = ConfigurationSettings.getSetting(event.getGuild().getId(), SQLFunctions.Settings.PUNISHMENTLOGID);

                        if (punishmentLogChannelId != null) {

                            TextChannel tc = event.getGuild().getTextChannelById(punishmentLogChannelId);
                            if (tc != null && tc.canTalk()) {

                                tc.sendMessage("```\nPUNISHMENT EXECUTED: UNMUTE" + member.getGuild().getName() + "\nUser: " + member.getEffectiveName() + " \nModerator: " +event.getAuthor().getName() + "```" ).queue();

                            } else {

                                event.getChannel().sendMessage("I cannot find the punishment log channel, or I may not have permissions to view/send messages.").queue();

                            }

                        }

                    } else {

                        event.getChannel().sendMessage("The user is not currently muted.").queue();

                    }




                }

            }

        }
    }

}
