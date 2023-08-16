package CustomerFunctions;

import Main.Main;
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
                return -2;
        }

    }

    public static int getAuthorPermLevel(MessageReceivedEvent event) {

        if (event.getAuthor().getId().equals("286270602820452353"))
            return 10;

        String staff = "";

        // Firstly, we want to see if the user's role exists in the database
        Member m = event.getGuild().retrieveMemberById(event.getAuthor().getId()).complete();
        List<Role> lr = m.getRoles();

        for (String s : Main.staffRoles.keySet()) {

            for (Role r : lr) {

                if (Main.staffRoles.get(s).contains(Long.parseLong(r.getId()))) {

                    staff = s;
                    break;

                }

            }

        }

        // if it does, assign them a permission level. If not, return 0.
        switch (staff) {
            case "TrialMod":
                return 1;
            case "Moderator":
                return 2;
            case "HeadModerator":
                return 3;
            case "Admin":
                return 4;
            case "Manager":
                return 5;
            case "Developer":
                return 6;
        }
        return 0;

    }

    public static int getCommandPermLvl(String cmd) {

        String staff = "";

        for (String s : Main.staffCommands.keySet()) {

            if (Main.staffCommands.get(s).contains(cmd)) {

                staff = s;
                break;

            }

        }

        switch (staff) {
            case "TrialMod":
                return 1;
            case "Moderator":
                return 2;
            case "HeadModerator":
                return 3;
            case "Admin":
                return 4;
            case "Manager":
                return 5;
            case "Developer":
                return 6;
        }
        return 0;

    }

    public static int getMentionedUserPermLevel(MessageReceivedEvent event, String userId) {

        if (userId.equals("286270602820452353"))
            return 10;

        String staff = "";

        // Firstly, we want to see if the user's role exists in the database
        Member m = event.getGuild().retrieveMemberById(userId).complete();
        List<Role> lr = m.getRoles();

        for (String s : Main.staffRoles.keySet()) {

            for (Role r : lr) {

                if (Main.staffRoles.get(s).contains(Long.parseLong(r.getId()))) {

                    staff = s;
                    break;

                }

            }

        }

        // if it does, assign them a permission level. If not, return 0.
        switch (staff) {
            case "TrialMod":
                return 1;
            case "Moderator":
                return 2;
            case "HeadModerator":
                return 3;
            case "Admin":
                return 4;
            case "Manager":
                return 5;
            case "Developer":
                return 6;
        }
        return 0;
    }

}
