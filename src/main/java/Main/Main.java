package Main;

import CustomerFunctions.ConfigurationSQLFunctions;
import CustomerFunctions.PunishmentSQLFunctions;
import Events.MessageEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static HashMap<String, List<Long>> staffRoles = new HashMap<>();
    public static HashMap<String, List<String>> staffCommands = new HashMap<>();

    public static List<String> commands = new ArrayList<>();
    public static String reportFilePath = "src\\main\\resources\\Reports\\temp.csv";

    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("MTEzMjY3NzE1ODQ5MDM0NTU1NA.GWeGuB.FPRguDCQCaZvuRgt6ucGGP523LEACZT2yp7P0o")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("Chat"))
                .build();

        instantiateHashMaps();

        jda.addEventListener(new MessageEvent());

    }

    private static void instantiateHashMaps() {

        staffRoles.put("TrialMod", new ArrayList<>());
        staffRoles.put("Moderator", new ArrayList<>());
        staffRoles.put("HeadMod", new ArrayList<>());
        staffRoles.put("Admin", new ArrayList<>());
        staffRoles.put("Manager", new ArrayList<>());
        staffRoles.put("Developer", new ArrayList<>());

        staffCommands.put("TrialMod", new ArrayList<>());
        staffCommands.put("Moderator", new ArrayList<>());
        staffCommands.put("HeadMod", new ArrayList<>());
        staffCommands.put("Admin", new ArrayList<>());
        staffCommands.put("Manager", new ArrayList<>());
        staffCommands.put("Developer", new ArrayList<>());

        commands.add("ad");
        commands.add("delrec");
        commands.add("modlogs");
        commands.add("ban");
        commands.add("mute");
        commands.add("unban");
        commands.add("unmute");
        commands.add("warn");
        commands.add("kick");

        PunishmentSQLFunctions.createTable();
        ConfigurationSQLFunctions.createTable();

    }
}
