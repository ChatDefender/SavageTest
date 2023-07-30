package CustomerFunctions;

public class ConfigurationSQLFunctions {

    static String url = "jdbc:sqlite:src\\main\\resources\\settings.db";

    public static void createTable() {

        String sql = "CREATE TABLE IF NOT EXISTS settings (" +
                "Prefix TEXT DEFAULT 's!'," +
                "MuteRoleId TEXT," +
                "PunishmentLogId TEXT)";

        BasicSQLFunctions.runStmt(url, sql);

        sql = "INSERT INTO settings VALUES ('s!', \"\", \"\")";

        BasicSQLFunctions.runStmt(url, sql);

    }

    public static String getSetting(String setting) {

        String sql = "SELECT "+setting+" FROM settings";

        return BasicSQLFunctions.getOne(url, sql, setting);

    }

    public static void setSetting(String setting, String newValue) {

        String sql = "UPDATE settings SET " + setting + " = '"+newValue+"'";

        BasicSQLFunctions.runStmt(url, sql);

    }

}
