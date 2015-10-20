package prime.net;

import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.SkypeException;

/**
 * Created by Prime on 9/21/2015.
 */

    /*
    Ideas for cmds
    !broadcast <message> - Broadcasts a message on the minecraft server.
    !message <player> <message> - Sends a message to a player.
    !mute <player> - mutes an annoying player, Kappa.
     */

    public final class SkypeCMDs implements ChatMessageListener {

    private boolean off = false;
    @Override
    public void chatMessageReceived(ChatMessage message) throws SkypeException{
        if(message.equals("!help")){
            message.getSender().send("!help - Shows you list of all available commands.");
            message.getSender().send("!off - Disables users from sending messsages from the minecraft server");
            message.getSender().send("!on - Enables users to send you messages from the minecraft server. ");
            System.out.println(message.toString());
        }

        if(message.equals("!off")){
            this.off = true;
        }
        if(message.equals("!on")){

        }

    }

    @Override
    public void chatMessageSent(ChatMessage chatMessage) throws SkypeException {
    if(off == true){
        //TODO: Cancel or remove msg.
    }
    }
}
