package Main;

import static Handlers.MongoDBHandler.MongoDBHandler.*;

import Handlers.CommandHandler;
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

        return getPermLvl(event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete());

    }

    public static int getMentionedUserPermLevel(MessageReceivedEvent event, String userId) {

        return getPermLvl(event.getGuild().retrieveMemberById(userId).complete());

    }

    private static int getPermLvl(Member m) {

        if (m != null) {

            if (m.getId().equals("286270602820452353"))
                return 9999;

            if (m.hasPermission(Permission.ADMINISTRATOR))
                return 15;

            List<Role> lr = m.getRoles();

            for (String s : getGroups()) {

                List<String> values = getArrValues("Roles", s);

                for (Role r : lr) {

                    if (values.contains(r.getId())) {

                        return getPermissionLevel("Roles", s);

                    }

                }

            }

        }

        return 0;

    }

    public static int getCommandPermLvl(String cmd) {

        for (String s : getGroups()) {

            if (getArrValues("Commands", s).contains(cmd)) {

                return getPermissionLevel("Commands", s);

            }

        }

        // if no return value, then we can just return the default command permission level
        // Note: User - 0
        // Punishments - 2
        // Logs - 5
        // Configuration - 8

        return CommandHandler.getPermissionLevel(cmd);


    }



}
