import Events.MessageEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {

    public static void main(String[] args) {
        JDA jda = JDABuilder.createDefault("MTEzMjY3NzE1ODQ5MDM0NTU1NA.GWeGuB.FPRguDCQCaZvuRgt6ucGGP523LEACZT2yp7P0o")
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("Chat"))
                .build();

        jda.addEventListener(new MessageEvent());

    }
}
