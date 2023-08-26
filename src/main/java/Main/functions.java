package Main;

import static Handlers.MongoDBHandler.MongoDBHandler.*;

import Handlers.MongoDBHandler.MongoDBHandler;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class functions {

    public static long timeToMilliseconds(String duration) {

        Pattern pattern = Pattern.compile("(\\d+)([m|h|d|mon|yr])");
        Matcher matcher = pattern.matcher(duration.trim());

        if (!matcher.matches()) {

            return -1;

        }

        long timeValue = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        switch (unit) {
            case "m":
                return timeValue * 60L * 1000L;
            case "h":
                return timeValue * 60L * 60L * 1000L;
            case "d":
                return timeValue * 24L * 60L * 60L * 1000L;
            case "mon":
                return timeValue * 30L * 24L * 60L * 60L * 1000L;
            case "yr":
                return timeValue * 365L * 24L * 60L * 60L * 1000L;
            default:
                return -1;
        }

    }

    public static int getAuthorPermLevel(MessageReceivedEvent event) {

        if (event.getAuthor().getId().equals("286270602820452353"))
            return 9999;

        if (event.getMember().hasPermission(Permission.ADMINISTRATOR))
            return 15;

        int permlvl = 0;

        // Firstly, we want to see if the user's role exists in the database
        Member m = event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete();
        List<Role> lr = m.getRoles();

        for (String s : getGroups()) {

            for (Role r : lr) {

                if (getArrValues("Roles", s).contains(r.getId())) {

                    permlvl = MongoDBHandler.getPermissionLevel("Roles", s);

                    break;

                }

            }

            break;

        }

        return permlvl;

    }

    public static int getCommandPermLvl(String cmd) {

        // we set perm lvl so high for commands because we do not want people abusing them
        int permlvl = 14;


        for (String s : getGroups()) {

            if (getArrValues("Commands", s).contains(cmd)) {

                permlvl = getPermissionLevel("Commands", s);
                break;

            }

        }

        return permlvl;

    }

    public static int getMentionedUserPermLevel(MessageReceivedEvent event, String userId) {

        if (userId.equals("286270602820452353"))
            return 9999;

        if (event.getGuild().retrieveMemberById(userId).complete().hasPermission(Permission.ADMINISTRATOR))
            return 15;

        // Firstly, we want to see if the user's role exists in the database
        Member m = event.getGuild().retrieveMemberById(userId).complete();
        List<Role> lr = m.getRoles();

        for (String s : getGroups()) {

            for (Role r : lr) {

                if (getArrValues("Roles", s).contains(r.getId())) {

                    return MongoDBHandler.getPermissionLevel("Roles", s);

                }

            }

        }

        return 0;
    }

}
