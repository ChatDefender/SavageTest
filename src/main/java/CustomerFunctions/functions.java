package CustomerFunctions;

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

}
