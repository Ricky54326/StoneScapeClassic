package stonekingdom;

import core.Misc;

/**
 * @author Stone- Copyright 2014
 * @version 1.00
 * @100% - This file was 100% created by Stone-
 * Processess commands
 */
public class Commands {
    public static void playerCommand(Client c, String cmd){
        switch(c.playerRights){
            case 3: //Admin

            case 2: //Moderator

            case 1: //Full time member

            case 0: //New Member
            	if (cmd.startsWith("pass")) {
                    if(cmd.length() > 5) {
                        c.playerPass = cmd.substring(5);
                        c.sendMessage("Your new password is \"" + cmd.substring(5) + "\"");
                    } else {
                        c.sendMessage("You cannot have a blank password!");
                    }
                }
            	
            	if (cmd.equalsIgnoreCase("mypos")){
                	c.sendMessage("You are standing on X="+c.absX+" Y="+c.absY+" H="+c.heightLevel);
            	}
                
                Misc.println_debug(c.playerName+": ran command: "+cmd);
                break;
        }
    }

}
