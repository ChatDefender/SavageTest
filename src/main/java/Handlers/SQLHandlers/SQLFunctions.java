package Handlers.SQLHandlers;

import Main.functions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLFunctions {

    private static final String hostname = functions.getCredential("hostname");
    private static final String servName = functions.getCredential("serv_name");
    private static final String port = functions.getCredential("port");

    private static final String db_conn_string = "jdbc:oracle:thin:@"+hostname+":"+port+"/"+servName;
    private static final String username = functions.getCredential("username");
    private static final String password = functions.getCredential("password");

    public enum Settings {

        PREFIX, MUTE_ROLE_ID, PUNISHMENT_LOG_ID, PUNISHMENT_RESET

    }

    public enum Punishments {
        WARN, MUTE, BAN, KICK, ALL, UNMUTE, UNBAN
    }

    static Connection conn = null;

    static void verifyConnection() {

        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");


            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(db_conn_string, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {

            throw new RuntimeException(e);
        }

    }

}
