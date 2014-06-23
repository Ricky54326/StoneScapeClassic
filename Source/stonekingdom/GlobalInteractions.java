/**
 * 
 */
package stonekingdom;

import core.Misc;

/**
 * @author David Hakki
 * @version 1.00
 * @100% - This file was 100% coded by David Hakki, thus ensuring the latest standards are followed
 */
public class GlobalInteractions {
	
	public static void dialog(Client c) { //Consists of NPC chats and general in-game interactions
		if(c.npcName == "null") {
			Misc.println_debug("test");
			return;
		}
		if(c.chatNumber == -1) {
			c.closeAllWindows(); //Ends the dialog
			return;
		}
		if(c.npcName.equalsIgnoreCase("Man")) {
			switch(c.chatNumber) {
			case 0:
				sendPlayerChat(c,"What is it like being a man?");
				break;
			case 1:
				sendNPCChat(c,"Lets see, people always pick my pockets",
						"they kill me while randomly walking by",
						"they don't even bury my bones most of the time.",
						"and when asked for what sin was I killed for", c.npcChat, c.npcName);
				break;
			case 2:
				sendNPCChat(c,"They say they killed me in the name of",
						"EXP!! Anywho maybe you can help me out here.",
						"Ghosts have over-run the musem",
						"So just kill 67 ghosts and I will reward you!", c.npcChat, c.npcName);
				break;
			case 3:
				sendPlayerChat(c,"What kind of reward are we talking about?");
				break;
			case 4:
				sendNPCChat(c,"I inherited a weapon called a scythe",
						"however I am terrible at fighting!",
						"I would be glad to give it to you!", c.npcChat, c.npcName);
				break;
			case 5:
				sendOption(c,"Would you like to add this task to your task journal?", "Yes", "No");
				break;
			case 6:
				sendItemChat(c,"New task added: Ghost busters", 
						"A new task has been added to your quest",
						"journal. Remember tasks are only available for",
						"a limited period of time and once they expire",
						"it will be removed from your quest journal", 1419, 200);
				c.chatNumber = -1;
				break;
			}
		}
		if(c.npcName.equalsIgnoreCase("Banker")) {
			switch(c.chatNumber) {
			case 0:
				sendPlayerChat(c,"Which color do you prefer, red or blue?");
				break;
			case 1:
				sendNPCChat(c,"yes", c.npcChat, c.npcName);
				break;
			case 2:
				sendPlayerChat(c,"That doesn't answer the question");
				break;
			case 3:
				sendNPCChat(c,"I agree", c.npcChat, c.npcName);
				break;
			case 4:
				sendPlayerChat(c,"..ok let me just see my bank account");
				break;
			case 5:
				c.openUpBank();
				c.chatNumber = -1;
				break;
			}
		}
		if(c.npcName.equalsIgnoreCase("Legends Guard")) {
			switch(c.chatNumber) {
			case 0:
				sendPlayerChat(c,"Um hi? What are you guys guarding");
				break;
			case 1:
				sendNPCChat(c,"Sire! We are the legends guards. We've been", 
						"guarding the Stone Kingdom for over 500 years!",
						"We stand in key areas providing guidence and help",
						"This room is where you can train combat.", c.npcChat, c.npcName);
				break;
			case 2:
				sendPlayerChat(c,"Wait you train combat here?", "What about fighting NPCs?",
						"Also can we use auto-clickers?");
				break;
			case 3:
				sendNPCChat(c,"NPCs are bad for EXP, but great for items", 
						"Remember items don't just randomly drop from NPCs",
						"Instead you'll need to get tasks from citizens.", c.npcChat, c.npcName);
				break;
			case 4:
				sendPlayerChat(c,"Who should I talk to get tasks");
				break;
			case 5:
				sendNPCChat(c,"Random citizens will provide tasks", "and remember they will change randomly."
						,"My advice is to regularly talk with all","citizens so you get good rewarding tasks.", c.npcChat, c.npcName);
				break;
			case 6:
				sendPlayerChat(c,"Sounds good thanks!");
				c.chatNumber = -1;
				break;
			}
		}
		
		if(c.npcName.equalsIgnoreCase("Wise Old Man")) {
			switch(c.chatNumber) {
			case 0:
				sendNPCChat(c,"Ow.. my head", "..can you please help me?", c.npcChat, "StoneScape Guide");
				break;
			case 1:
				sendPlayerChat(c,"What happened?? Are you ok?");
				break;
			case 2:
				sendNPCChat(c,"Thugs came.. and beat me badly..",
						"..and they stole ALL of my easter eggs",
						"and some of my other treasures",
						"can you please help an old yeehaw like me?", c.npcChat, "StoneScape Guide");
				break;
			case 3:
				sendPlayerChat(c,"I'd love to help you!");
				break;
			case 4:
				sendPlayerChat(c,"...SIKE IT AIN'T EASTER!!");
				break;
			case 5:
				sendNPCChat(c,"OOOOOOOOOOOO0O0O0OoO0O0ooooOOOOOOOOOOOO",
						"THATS A GOOD ONE!!!!!!!!!!!!!!!!!!!!!!!!!", 152, "Hammad hiding in a tree (needs a slap)");
				break;
			case 6:
				sendNPCChat(c,"Wha? These eggs were created by the Stone King",
						"THOUSANDS of years ago! Once the Stone King",
						"vanished, all but a few ceased to exist.",
						"It took me 20 years to find them...", c.npcChat, "StoneScape Guide");
				break;
			case 7:
				sendPlayerChat(c,"How am I suppose to find 3 eggs", "in the entirety of the world?");
				break;
			case 8:
				sendNPCChat(c,"Here I will give you my notes",
						"you can read my notes in your quest journal",
						"once you find all three have a chat with me",
						"and I will reward you handsomely!", c.npcChat, "StoneScape Guide");
				break;
			case 9:
				sendItemChat(c,"New treasure hunt added: Easter Egg Hunt", 
						"Your treasure hunt has been added to your quest",
						"journal. Remember in Stone Kingdom different",
						"players will get different clues! Also quests",
						"will change after some time!", 1961, 200);
				c.chatNumber = -1;
				break;
			}
		}
	}
	
	public void sendStartInfo(Client c,String text, String text1, String text2, String text3, String title) {
		c.setLine(title, 6180);
		c.setLine(text, 6181);
		c.setLine(text1, 6182);
		c.setLine(text2, 6183);
		c.setLine(text3, 6184);
		c.sendFrame164(6179);
	}

	public void sendItemChat(Client c,String header, String one, int item, int zoom) {
		c.sendFrame246(4883, zoom, item);
		c.setLine(header, 4884);
		c.setLine(one, 4885);
		c.sendFrame164(4882);
	}

	public void sendItemChat(Client c,String header, String one, String two, int item, int zoom) {
		c.sendFrame246(4888, zoom, item);
		c.setLine(header, 4889);
		c.setLine(one, 4890);
		c.setLine(two, 4891);
		c.sendFrame164(4887);
	}

	public void sendItemChat(Client c,String header, String one, String two, String three, int item, int zoom) {
		c.sendFrame246(4894, zoom, item);
		c.setLine(header, 4895);
		c.setLine(one, 4896);
		c.setLine(two, 4897);
		c.setLine(three, 4898);
		c.sendFrame164(4893);
	}

	public static void sendItemChat(Client c,String header, String one, String two, String three, String four, int item, int zoom) {
		c.sendFrame246(4901, zoom, item);
		c.setLine(header, 4902);
		c.setLine(one, 4903);
		c.setLine(two, 4904);
		c.setLine(three, 4905);
		c.setLine(four, 4906);
		c.sendFrame164(4900);
	}
	
	/*
	 * Options
	 */
	
	public void sendOption(Client c, String title, String s) {
		c.setLine(title, 2470);
	 	c.setLine(s, 2471);
		c.setLine("Click here to continue", 2473);
		c.sendFrame164(13758);
	}	
	
	public static void sendOption(Client c, String title,String s, String s1) {
		c.setLine(title, 2460);
		c.setLine(s, 2461);
		c.setLine(s1, 2462);
		c.sendFrame164(2459);
	}
	
	public void sendOption(Client c, String title,String s, String s1, String s2) {
		c.setLine(title, 2470);
		c.setLine(s, 2471);
		c.setLine(s1, 2472);
		c.setLine(s2, 2473);
		c.sendFrame164(2469);
	}
	
	public void sendOption(Client c, String title,String s, String s1, String s2, String s3) {
		c.setLine(title, 2481);
		c.setLine(s, 2482);
		c.setLine(s1, 2483);
		c.setLine(s2, 2484);
		c.setLine(s3, 2485);
		c.sendFrame164(2480);
	}
	
	public void sendOption(Client c, String title,String s, String s1, String s2, String s3, String s4) {
		c.setLine(title, 2493);
		c.setLine(s, 2494);
		c.setLine(s1, 2495);
		c.setLine(s2, 2496);
		c.setLine(s3, 2497);
		c.setLine(s4, 2498);
		c.sendFrame164(2492);
	}

	/*
	 * Statements
	 */
	
	@SuppressWarnings("unused")
	private void sendStatement(Client c,String s) {
		c.setLine(s, 357);
		c.setLine("Click here to continue", 358);
		c.sendFrame164(356);
	}
	@SuppressWarnings("unused")
	private void sendStatement(Client c,String s, String s1) {
		c.setLine(s, 360);
		c.setLine(s1, 361);
		c.setLine("Click here to continue", 362);
		c.sendFrame164(359);
	}
	
	@SuppressWarnings("unused")
	private void sendStatement(Client c,String s, String s1, String s2) {
		c.setLine(s, 364);
		c.setLine(s1, 365);
		c.setLine(s2, 366);
		c.setLine("Click here to continue", 367);
		c.sendFrame164(363);
	}
	@SuppressWarnings("unused")
	private void sendStatement(Client c,String s, String s1, String s2, String s3) {
		c.setLine(s, 369);
		c.setLine(s1, 370);
		c.setLine(s2, 371);
		c.setLine(s3, 372);
		c.setLine("Click here to continue", 373);
		c.sendFrame164(368);
	}
	@SuppressWarnings("unused")
	private void sendStatement(Client c,String s, String s1, String s2, String s3, String s4) {
		c.setLine(s, 375);
		c.setLine(s1, 376);
		c.setLine(s2, 377);
		c.setLine(s3, 378);
		c.setLine(s4, 379);
		c.setLine("Click here to continue", 380);
		c.sendFrame164(374);
	}
	
	/*
	 * Npc Chatting
	 */
	private static void sendNPCChat(Client c,String s, int ChatNpc, String name) {
		c.setHeadAnim(4883, 591);
		c.setLine(name, 4884);
		c.setLine(s, 4885);
		c.setHead(ChatNpc, 4883);
		c.sendFrame164(4882);
	}
	private static void sendNPCChat(Client c,String s, String s1, int ChatNpc, String name) {
		c.setHeadAnim(4888, 591);
		c.setLine(name, 4889);
		c.setLine(s, 4890);
		c.setLine(s1, 4891);
		c.setHead(ChatNpc, 4888);
		c.sendFrame164(4887);
	}
	private static void sendNPCChat(Client c,String s, String s1, String s2, int ChatNpc, String name) {
		c.setHeadAnim(4894, 591);
		c.setLine(name, 4895);
		c.setLine(s, 4896);
		c.setLine(s1, 4897);
		c.setLine(s2, 4898);
		c.setHead(ChatNpc, 4894);
		c.sendFrame164(4893);
	}
	
	private static void sendNPCChat(Client c,String s, String s1, String s2, String s3, int ChatNpc, String name) {
		c.setHeadAnim(4901, 591);
		c.setLine(name, 4902);
		c.setLine(s, 4903);
		c.setLine(s1, 4904);
		c.setLine(s2, 4905);
		c.setLine(s3, 4906);
		c.setHead(ChatNpc, 4901);
		c.sendFrame164(4900);
	}
	
	/*
	 * Player Chating Back
	 */
	
	private static void sendPlayerChat(Client c,String s) {
		c.setHeadAnim(969, 591);
		c.setLine(Misc.capitalize(c.playerName), 970);
		c.setLine(s, 971);
		c.sendFrame185(969);
		c.sendFrame164(968);
	}
	private static void sendPlayerChat(Client c,String s, String s1) {
		c.setHeadAnim(974, 591);
		c.setLine(Misc.capitalize(c.playerName), 975);
		c.setLine(s, 976);
		c.setLine(s1, 977);
		c.sendFrame185(974);
		c.sendFrame164(973);
	}
	
	private static void sendPlayerChat(Client c,String s, String s1, String s2) {
		c.setHeadAnim(980, 591);
		c.setLine(Misc.capitalize(c.playerName), 981);
		c.setLine(s, 982);
		c.setLine(s1, 983);
		c.setLine(s2, 984);
		c.sendFrame185(980);
		c.sendFrame164(979);
	}
	@SuppressWarnings("unused")
	private void sendPlayerChat(Client c,String s, String s1, String s2, String s3) {
		c.setHeadAnim(987, 591);
		c.setLine(Misc.capitalize(c.playerName), 988);
		c.setLine(s, 989);
		c.setLine(s1, 990);
		c.setLine(s2, 991);
		c.setLine(s3, 992);
		c.sendFrame185(987);
		c.sendFrame164(986);
	}
	
}
