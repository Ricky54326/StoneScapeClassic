/**
 * 
 */
package stonekingdom;

/**
 * @author Stone- Copyright 2014
 * @version 1.00
 * @100% - This file was 100% created by Stone-
 */
public class Config {
public static final int MAX_PLAYERS = 500;
	//General server section	
	public static boolean SERVER_DEBUG = true; //True = debugging mode TURN OFF BEFORE LAUNCH
	public static int PORT_NUMBER = 30712;
	public static String SERVER_NAME = "Stone-Kingdom";
	public static int SERVER_REVISION = 317; //DO NOT CHANGE UNLESS YOU CHANGE CLIENTS, WILL BREAK ALL PACKETS
	public static void welcome(Client c){
		c.sendMessage("Welcome to Stone-Kingdom V1");
	}
	
//Starting Section (for new players!)
	//Location = Ardy
	public static int STARTX = 2683; 
	public static int STARTY = 3318;
	//Starter items and clan affiliation
	public static void Starter (Client c, String Name){
		if(c.clanName == "null" || (c.playerRights == 3 && Config.SERVER_DEBUG)) { //determines it is a new account
			if(Name == "The Fecities") {
				c.clanName = Name;
				//ItemHandler.wearItem(c,4375,"Cape");
				c.playerEquipment[c.playerCape]=4375;
				c.addItem(1165,1); // black full helm
				c.addItem(1077,1); // black platelegs
				c.addItem(1125,1); // black platebody
				c.addItem(1195,1); // Black kite shield
				c.addItem(1375,1); // Bronze battleaxe
				c.addItem(995,50); //coins
				c.addSkillXP(2412, "Sanctity"); 
				c.addSkillXP(2412, "Defence");
				c.sendMessage("You joined "+c.clanName);
				c.teleport(2582,3297, 0);
			}
			if(Name == "The Saradominites") {
				c.clanName = Name;
				c.playerEquipment[c.playerCape]=4355;
				c.addItem(538,1); //Druid robe bottom
				c.addItem(540,1); //Druid robe top
				c.addItem(995,100); //coins
				c.addSkillXP(2412, "Sorcery"); 
				c.addSkillXP(2412, "Herbology");
				c.sendMessage("You joined "+c.clanName);
				c.teleport(2617,3308, 0);
			}
			if(Name == "Al-Abdullah") {
				c.clanName = Name;
				c.playerEquipment[c.playerCape]=4395;
				c.addItem(1327,1); //black scimitar
				c.addItem(995,400); //coins
				c.addSkillXP(2412, "Attack"); 
				c.addSkillXP(2412, "Strength");
				c.sendMessage("You joined "+c.clanName);
				c.teleport(2668,3306, 0);
			}
			if(Name == "Trading Federation") {
				c.clanName = Name;
				c.addItem(995,800); //coins
				c.addSkillXP(389, "Mining"); 
				c.addSkillXP(389, "Smithing");
				c.addSkillXP(389, "Fishing");
				c.addSkillXP(389, "Woodcutting");
				c.playerEquipment[c.playerCape]=4335;
				c.sendMessage("You joined the "+c.clanName);
				c.teleport(2667,3269, 0);
			}
		} else {
			c.sendMessage("You can't get another starter!");
		}
		c.closeAllWindows();
	}
}
