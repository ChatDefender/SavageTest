package Commands.User;

import Commands.BaseCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Birthday extends BaseCommand {

    public Birthday() {
        super("birthday", new String[] {"bday"}, "birthday [@user]", "Provides information on commands", "", Permission.MESSAGE_SEND);
    }

    @Override
    public void run(MessageReceivedEvent event, String[] args) {

        String user = args.length == 0 ? "" : " " + args[0];

        event.getChannel().sendMessage("Happy birthday" + user + "! \nhttps://www.twitch.tv/lolipopgi/clip/CuteEasyMacaroniTBTacoRight-GEyluBFXObw-02qG").queue();


    }

}
