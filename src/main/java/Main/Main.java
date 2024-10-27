package Main;

import Commands.ActiveDirectory.*;
import Commands.ConfigureBot.*;
import Commands.Log.*;
import Commands.PunishmentManagement.*;
import Commands.Punishments.*;
import Commands.User.*;
import Events.*;
import Handlers.CommandHandler;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Main {

    public static void main(String[] args) {

        JDA jda = JDABuilder.createDefault(functions.getCredential("bot_api_key"))
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("Chat"))
                .addEventListeners(new BotReady())
                .build();

        registerCommands();

        jda.addEventListener(new MessageEvent());
        jda.addEventListener(new RoleDelete());
        jda.addEventListener(new MemberJoin());
        jda.addEventListener(new GuildJoin());

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Sending wake-up request at - " + new Date());
            }
        }, 0, 30000); // Output every 30 seconds

    }

    private static void registerCommands() {

        CommandHandler.registerCommand(new DeleteLogs());
        CommandHandler.registerCommand(new DeleteRecord());
        CommandHandler.registerCommand(new ModLogs());
        CommandHandler.registerCommand(new RecoverRecord());
        CommandHandler.registerCommand(new Ban());
        CommandHandler.registerCommand(new Kick());
        CommandHandler.registerCommand(new Mute());
        CommandHandler.registerCommand(new Unban());
        CommandHandler.registerCommand(new Unmute());
        CommandHandler.registerCommand(new Warn());
        CommandHandler.registerCommand(new Help());
        CommandHandler.registerCommand(new AddUnit());
        CommandHandler.registerCommand(new AddRole());
        CommandHandler.registerCommand(new AddCommand());
        CommandHandler.registerCommand(new RemoveGroup());
        CommandHandler.registerCommand(new RemoveRole());
        CommandHandler.registerCommand(new RemoveCommand());
        CommandHandler.registerCommand(new SetMutedRole());
        CommandHandler.registerCommand(new SetPrefix());
        CommandHandler.registerCommand(new SetPunishmentLogsChannel());
        CommandHandler.registerCommand(new Birthday());
        CommandHandler.registerCommand(new RecoverLogs());
        CommandHandler.registerCommand(new CreatePunishment());
        CommandHandler.registerCommand(new DeletePunishment());
        CommandHandler.registerCommand(new AddTier());
        CommandHandler.registerCommand(new RemoveTier());
        CommandHandler.registerCommand(new EditTier());
        CommandHandler.registerCommand(new RenamePunishment());
        CommandHandler.registerCommand(new Punish());
        CommandHandler.registerCommand(new SetPunishReset());
        CommandHandler.registerCommand(new AddPunishment());
        CommandHandler.registerCommand(new RemovePunishment());

    }

}