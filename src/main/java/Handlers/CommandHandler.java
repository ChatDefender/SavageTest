package Handlers;

import Commands.BaseCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.*;

public class CommandHandler {

    // Simulated map of command names to command instances
    private static Map<String, BaseCommand> commandMap = new HashMap<>();

    public static void registerCommand(BaseCommand command) {
        commandMap.put(command.getName(), command);
        for (String alias : command.getAliases()) {
            commandMap.put(alias, command);
        }
    }

    public static void executeCommand(String commandName, MessageReceivedEvent event, String[] args) {
        BaseCommand command = commandMap.get(commandName);
        if (command != null) {
            command.run(event, args);
        }
    }

    public static boolean doesCommandExist(String command) {

        return commandMap.get(command) != null;

    }

    public static List<String> getCommands() {

        List<String> cmds = new ArrayList<>();

        for (String cmd : commandMap.keySet()) {

            if (!Arrays.asList(commandMap.get(cmd).getAliases()).contains(cmd)) {

                cmds.add(cmd);

            }

        }

        return cmds;

    }

}
