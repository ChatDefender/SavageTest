package Commands.User;

import Commands.BaseCommand;
import Main.functions;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PermissionLevel extends BaseCommand {
    public PermissionLevel() {
        super("permissionlevel", new String[] {"permlvl", "pl"}, "permissionlevel",  "Tells the user their permission level", "The bot owner will have the highest permission level\nPeople with the Permission.ADMINISTRATOR on discord will have a perm lvl of 15\nThe default permission level for commands is 14\nThe rest are set based on the active directory", 0);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        String userId = args.length == 1 ? event.getAuthor().getId() : args[1].replace("<@", "").replace(">", "");

        Member m = event.getGuild().retrieveMemberById(userId).complete();

        int permLvl = args.length == 1 ? functions.getAuthorPermLevel(event) : functions.getMentionedUserPermLevel(event, userId);

        sendMessage(event, m, permLvl);

    }

    private static void sendMessage(MessageReceivedEvent event, Member m, int permLevel) {

        event.getChannel().sendMessage(m.getEffectiveName() + "'s permission level is" + permLevel).queue();

    }

}
