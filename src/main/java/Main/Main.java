package Main;

import Commands.BaseCommand;
import Commands.Configuration.ActiveDirectory;
import Commands.Configuration.ConfigureBot;
import Commands.Log.ClearLogs;
import Commands.Log.DeleteRecord;
import Commands.Log.ModLogs;
import Commands.Log.RecoverRecord;
import Commands.Punishments.*;
import Commands.User.PermissionLevel;
import Events.MemberJoin;
import Handlers.CommandHandler;
import Handlers.MongoDBHandler.MongoDBHandler;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.PunishmentSQLFunctions;
import Events.BotReady;
import Events.MessageEvent;
import Events.RoleDelete;
import Handlers.SQLHandlers.TimedPunishmentsSQLFunctions;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    // Username: user_savage
    // Password: password_T2yp7P0o

    public static String reportFilePath = "src\\main\\resources\\Reports\\temp.csv";

    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("MTEzMjY3NzE1ODQ5MDM0NTU1NA.GWeGuB.FPRguDCQCaZvuRgt6ucGGP523LEACZT2yp7P0o")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("Chat"))
                .build();

        verifyDatabases();
        registerCommands();

        jda.addEventListener(new MessageEvent());
        jda.addEventListener(new RoleDelete());
        jda.addEventListener(new BotReady());
        jda.addEventListener(new MemberJoin());
    }

    private static void verifyDatabases() {

        MongoDBHandler.verifyCollections();
        PunishmentSQLFunctions.createTable();
        ConfigurationSQLFunctions.createTable();
        TimedPunishmentsSQLFunctions.createTable();

    }

    private static void registerCommands() {

        CommandHandler.registerCommand(new ActiveDirectory());
        CommandHandler.registerCommand(new ConfigureBot());
        CommandHandler.registerCommand(new ClearLogs());
        CommandHandler.registerCommand(new DeleteRecord());
        CommandHandler.registerCommand(new ModLogs());
        CommandHandler.registerCommand(new RecoverRecord());
        CommandHandler.registerCommand(new Ban());
        CommandHandler.registerCommand(new Kick());
        CommandHandler.registerCommand(new Mute());
        CommandHandler.registerCommand(new Unban());
        CommandHandler.registerCommand(new Unmute());
        CommandHandler.registerCommand(new Warn());
        CommandHandler.registerCommand(new PermissionLevel());

    }




}
