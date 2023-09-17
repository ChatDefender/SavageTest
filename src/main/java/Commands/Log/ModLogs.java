package Commands.Log;

import Commands.BaseCommand;
import Handlers.SQLHandlers.ConfigurationSQLFunctions;
import Handlers.SQLHandlers.PunishmentSQLFunctions;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import Main.*;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

// import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.List;

public class ModLogs extends BaseCommand {


    public ModLogs() {
        super("modlogs", new String[] {"modlog"}, "modlog [@user | user id] [-u(ser) | -s(taff)] [punishment type] [-a(rchived)]", "Retrieves records from the punishment log database.", "\nQueries the database for punishments\n-u is the user punished\n-s is the staff member responsible for the punishents\n-a will search only archived entries", 5);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        if (args.length < 3) {
            event.getChannel().sendMessage("Command Layout: "+ ConfigurationSQLFunctions.getSetting("Prefix")+"modlog [@user | user id] [-u(ser) | -s(taff)] [-a(rchived)]\nExplanation: Queries the database for punishments\n-u is the user punished\n-s is the staff member responsible for the punishents\n-a will search only archived entries").queue();
        } else {

            // Get the user ID, if a user is mentioned, remove the <@ and > to get only the ID
            String userId = args[1];
            userId = userId.replace("<@", "").replace(">", "");

            boolean canContinue = true;

            String punType = "";
            switch(args[3].toLowerCase()) {

                case "w":
                case "warn":
                    punType = "warn";
                    break;
                case "k":
                case "kick":
                    punType = "kick";
                    break;
                case "m":
                case "mute":
                    punType = "mute";
                    break;
                case "b":
                case "ban":
                    punType = "ban";
                    break;
                case "a":
                case "all":
                    punType = "all";
                    break;
                default:
                    canContinue = false;
                    event.getChannel().sendMessage("Invalid punishment type provided. Please use either the full word or the first letter of one of the following: `warn`, `kick`, `mute`, `ban`, or `all`").queue();
            }

            if (canContinue) {

                boolean isArchived = args.length == 5 && args[4].equals("-a");

                switch (args[2].toLowerCase()) {

                    case "-u":
                    case "-user":

                        PunishmentSQLFunctions.getPunishmentLogsForUser(userId, punType, isArchived);
                        handleCharacterLimit(event);

                        break;
                    case "-s":
                    case "-staff":

                        PunishmentSQLFunctions.getPunishmentLogsForStaff(userId, punType, isArchived);
                        handleCharacterLimit(event);

                        break;

                }

            }

        }

    }

    private void handleCharacterLimit(MessageReceivedEvent event) {
        if (getTable().length() > 2000) {

            // get the file
            FileUpload fu = FileUpload.fromData(new File(Main.reportFilePath));
            event.getChannel().sendMessage("Cannot send in table format, sending table...").queue();
            event.getChannel().sendFiles(fu).queue(
                    success -> {},
                    error -> event.getChannel().sendMessage("Could not send file: " + error.getMessage()).queue()
            );

        } else {

            event.getChannel().sendMessage("```"+getTable()+"```").queue();

        }
    }

//    public static int getLongestString() {
//
//        List<String> tableBuilder = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(Main.reportFilePath))) {
//            String line;
//
//            // Read the header line to get column names
//            if ((line = br.readLine()) != null) {
//                String[] headers = line.split(",");
//                tableBuilder.addAll(Arrays.asList(headers));
//            }
//
//            // Read the rest of the lines
//            while ((line = br.readLine()) != null) {
//                String[] row = line.split(",");
//                tableBuilder.addAll(Arrays.asList(row));
//            }
//
//            int longest = 0;
//            for (String s : tableBuilder) {
//
//                if (s.length() > longest) {
//
//                    longest = s.length();
//
//                }
//
//            }
//
//            return longest;
//            // Display the table as a string
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return 0;
//
//    }


    public static String getTable() {

        StringBuilder tableBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(Main.reportFilePath))) {
            String line;
            int[] columnWidths = null;

            // Read the CSV file line by line
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");

                // Initialize columnWidths on the first row
                if (columnWidths == null) {
                    columnWidths = new int[row.length];
                    for (int i = 0; i < row.length; i++) {
                        columnWidths[i] = row[i].length();
                    }
                } else {
                    // Update columnWidths with the maximum length in each column
                    for (int i = 0; i < row.length; i++) {
                        columnWidths[i] = Math.max(columnWidths[i], row[i].length());
                    }
                }
            }

            // Reset the file reader to start again from the beginning
            try (BufferedReader br2 = new BufferedReader(new FileReader(Main.reportFilePath))) {
                // Read the header line to get column names
                if ((line = br2.readLine()) != null) {
                    String[] headers = line.split(",");
                    appendRowToTable(tableBuilder, headers, columnWidths);
                }

                // Read the rest of the lines
                while ((line = br2.readLine()) != null) {
                    String[] row = line.split(",");
                    appendRowToTable(tableBuilder, row, columnWidths);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tableBuilder.toString();

    }

    private static void appendRowToTable(StringBuilder tableBuilder, String[] rowData, int[] columnWidths) {
        for (int i = 0; i < rowData.length; i++) {
            String cell = rowData[i];
            int width = columnWidths[i];
            tableBuilder.append(String.format("%-" + (width + 2) + "s", cell)).append("|"); // Add 2 to the width for padding
        }
        tableBuilder.append("\n");
    }

}
