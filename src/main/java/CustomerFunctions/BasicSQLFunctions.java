package CustomerFunctions;

import Main.Main;
import dnl.utils.text.table.TextTable;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BasicSQLFunctions {

    public static void runStmt(String url, String sql) {
        try {
            // Register the JDBC driver (you can skip this if using JDBC 4.0+)
            Class.forName("org.sqlite.JDBC");

            // Open a connection to the database
            Connection con = DriverManager.getConnection(url);
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql);

            stmt.close();
            con.close();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void runQuery(String url, String sql) {

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            FileWriter fw = new FileWriter(Main.reportFilePath);
            fw.close();

            ResultSet resultSet = statement.executeQuery(sql);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];

            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = metaData.getColumnName(i);
            }

            // Get the row data from the result set and store in a List
            List<Object[]> rows = new ArrayList<>();

            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                rows.add(rowData);
            }

            // Convert the List to a 2D array for rows
            Object[][] dataArray = rows.toArray(new Object[0][0]);
            TextTable table = new TextTable(columnNames, dataArray);

            // Print the table
            OutputStream os = Files.newOutputStream(Paths.get(Main.reportFilePath));
            table.toCsv(os);

            os.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public static String getOne(String url, String sql, String columnName) {

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()) {

                return resultSet.getString(columnName);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "";

    }

}
