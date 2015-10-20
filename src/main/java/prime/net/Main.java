package prime.net;

import com.skype.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Created by Prime on 9/10/2015.
 */
public class Main extends JavaPlugin {

        /* Commands - *in Minecraft*
        idea: add permissions for each skype name, example: skype.message.loxkid123
         --User commands--
         IGN = In-Game-Game
         DONE /skype list -> Shows all friends/staff that the account has as friends and their status
                        , id + status + name(nickname, not actual Skype id to prevent players getting our real Skypename).
         DONE /skype message <IGN> <message> -> Sends a message to a staff member on Skype.

         --Staff commands--
         DONE /skype add <skypename> -> Adds the staff member to the skype list, and sends friend request to his/her Skype account.
                                   Also sends the staff the link to the General Chat for him.
         DONE /skype add <skypename> <IGN> -> Does the same stuff as the normal command but in this case, another staff is adding another staff taking the player name and Skype id.
         DONE /skype remove <Player>
         /skype off -> Prevents players from sending messages to his/her Skype.
         /skype off <IGN> -> Prevents players from sending a message to a specific staff.
         /skype on
         /skype on <IGN>
         /skype who -> Shows whose skypeID is who is in-Game. Example: ITS_PRIME Skype ID is loxkid123

         Commands - *in Skype*
         !skype off
         !skype on
          */
    String theMessage;
    //TODO: Save the HashMaps entries to config or somewhere else.
    Map<String, String> list = new HashMap<>();
    String friendRequestMSG = "Hello, my name is HQGaming-Skype-Bot! Seems like you're a staff member.";
    boolean notOnList = false;
    //       skypeID, Name
    //HashMap<Key, Value>
    public void onEnable(){
        getConfig().options().copyDefaults(true);
        saveConfig();
   }

    public void onDisable(){
        saveConfig();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (label.equalsIgnoreCase("skype")) {
                if(args.length == 0){
                    //TODO: Put the commands list here or send a command for /help skype.
                    p.sendMessage("Help commands here Kapp123");
                    return true;
                }
                if (args[0].equalsIgnoreCase("message") && (args.length > 2)) {
                    try{
                        StringBuilder builder = new StringBuilder();
                        for(int i=2; i < args.length; i++){
                            builder.append(" ").append(args[i]);
                            theMessage = builder.toString();
                        }
                        for(Map.Entry<String,String> entry : list.entrySet()) {
                            if (entry.getValue().equalsIgnoreCase(args[1])) {
                                Skype.getUser(entry.getKey()).send(theMessage);
                                p.sendMessage("Message has been sent to " + getSkypeID(args[1]) + " " + theMessage);
                            }
                            else if(!(entry.getValue().equalsIgnoreCase(args[1]))){
                                notOnList = true;
                                System.out.println("Message error plus " + args[1]);
                            }
                        }
                        if(notOnList == true){
                            p.sendMessage("Wrong username. Try /skype list to see all active staff on Skype.");
                        }
                    } catch (SkypeException e){
                        e.printStackTrace();
                    }
                }
                if (args[0].equalsIgnoreCase("list")) {
                    try {
                        p.sendMessage(this.getList(p));
                    } catch (SkypeException e) {
                        e.printStackTrace();
                    }
                }
                if(args[0].equalsIgnoreCase("add")) {
                    if((args.length < 2) && (list.containsKey(args[1]) || list.containsValue(args[2]))){
                        p.sendMessage("This skype ID or player is already on the skype list.");
                        return true;
                    }
                    else if (!(list.containsKey(args[1])) && args.length == 2) {
                        setSkypeID(args[1], p);
                        getConfig().set("skypeID", args[1]);
                        getConfig().set("player", p.getName());
                        try {
                            this.sendFriendRequest(args[1]);
                        } catch (SkypeException e) {
                            e.printStackTrace();
                        }
                        p.sendMessage("SkypeID: " + args[1] + " has been added to the user " + p.getName());
                    }
                    else if (!(list.containsKey(args[1]) && list.containsValue(args[2])) && args.length == 3) {
                        System.out.println(args.length);
                        setSkypeID(args[1], args[2]);
                        for(int i=0; i < getConfig().getInt("list."); i++) {
                            getConfig().set("list." + i + ".skypeID", args[1]);
                            getConfig().set("list." + i + ".player", args[2]);
                        }
                        p.sendMessage("SkypeID: " + args[1] + " has been added to the user " + args[2]);
                        try {
                            this.sendFriendRequest(args[1]);
                        } catch (SkypeException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        p.sendMessage("Ussage: /skype add <skypeID> or /skype add <skypeID> <MC-Name>");
                    }
                }
                if(args[0].equalsIgnoreCase("remove") && args.length == 2){
                    Iterator<Map.Entry<String,String>> iter = list.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry<String,String> entry = iter.next();
                        if(args[1].equalsIgnoreCase(entry.getValue())){
                            iter.remove();
                        }
                    }
                    p.sendMessage(args[1] + " has been removed from the Skype list.");
                }
                //skype off
                //skype off <player>
                if(args[0].equalsIgnoreCase("off")){
                    if(args.length == 1){
                        for(Map.Entry<String,String> entry : list.entrySet()) {
                            if(entry.getValue().equalsIgnoreCase(p.getName())){
                                //turn off skype messages for that player.
                            }
                        }
                    }
                    else if(args.length == 2){
                        for(Map.Entry<String,String> entry : list.entrySet()) {
                            if(entry.getValue().equalsIgnoreCase(args[1])){
                                //turn off skype messages for that player.
                            }
                        }
                    }
                }
                if(args[0].equalsIgnoreCase("who")){
                    for(Map.Entry<String,String> entry : list.entrySet()) {
                        p.sendMessage(ChatColor.GREEN + entry.getValue() + " Skype ID is: " + entry.getKey());
                    }
                }
            }
        }
        return true;
    }

    public String getList(Player p) throws SkypeException {
        for(Map.Entry<String,String> entry : list.entrySet()) {
            String _SkypeStatus = Skype.getUser(entry.getKey()).getStatus().toString();
            String _Name = entry.getValue();
            p.sendMessage(ChatColor.GREEN + "------------------------------------");
            p.sendMessage(ChatColor.GREEN + "Name: " + _Name);
            p.sendMessage(ChatColor.GREEN + "Status: " + _SkypeStatus);
            p.sendMessage(ChatColor.GREEN + "------------------------------------");
        }
        return null;
    }

    //returns the skypeID for the player sent.
    public String getSkypeID(String playerName) {
        String skypeID = null;
        for (Map.Entry<String, String> entry : list.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(playerName)) {
                skypeID = entry.getKey();
            }
        }
        return skypeID;
    }

    public void setSkypeID(String skypeID, Player p){
        list.put(skypeID, p.getName());
    }

    public void setSkypeID(String skypeID, String p){
        list.put(skypeID, p);
    }

    public void sendFriendRequest(String skypeId) throws SkypeException {
        Skype.getContactList().addFriend(skypeId, friendRequestMSG);
    }

}