package stonekingdom;
/**
 * @author David Hakki - Stone Warior
 * @version 1.00
 * @purpose: The purpose of this file is to simply handle the connection of the client and
 * any input/output from the client. Once input/output is handled it should then forward it to its respective locations
 * For example:
 * Clicking on an skilling related object -> skills/whatever skill
 * Interacting with an Item -> ItemHandler.java
 * Interacting with an NPC -> NPCHandler.Java
 * 
 * So lets say a person clicks on an NPC, that can either be combat or chat or trade. Combat has a separate Java file, 
 * chatting and trading files under NPCInteractions
 * 
 * @standards (ACROSS ALL STONE- PROGRAMS)
 * 1. No more then 120 characters per line this makes it so you never have to scroll horizontally
 * 2. Comment your code. If I can't understand, it isn't acceptable and vise versa
 */
import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;

import core.Cryption;
import core.IOHostList;
import core.Misc;
import core.Server;
import core.Stream;

public class Client extends Player {
    Toolkit toolkit;
    Timer timer;

    public void SecondsTimer() {
        toolkit = Toolkit.getDefaultToolkit();
        timer = new Timer();
        timer.schedule(new RemindTask(),
                       0,        //initial delay
                       1*1000);  //subsequent rate
    }

    class RemindTask extends TimerTask {
        
        public void run() {
      
	  Seconds = Seconds + 1;
            }
        }
	public void println_debug(String str)
	{
		System.out.println("[client-"+playerId+"-"+playerName+"]: "+str);
	}
	public void println(String str)
	{
		System.out.println("[client-"+playerId+"-"+playerName+"]: "+str);
	}//end import stuff\\

	public void addObject(int x, int y, int object) {
	   outStream.createFrameVarSizeWord(60);  // tells baseX and baseY to client
	   outStream.writeByte(y-(mapRegionY*8));
	   outStream.writeByteC(x-(mapRegionX*8));
	
	   outStream.writeByte(151);
	    outStream.writeByteS(0);
	   
	    outStream.writeWordBigEndian(object);
	    outStream.writeByteA(0);
	   outStream.endFrameVarSizeWord();
	   }   
	
	public void resetItems(int WriteFrame) {
		outStream.createFrameVarSizeWord(53);
		outStream.writeWord(WriteFrame);
		outStream.writeWord(playerItems.length);
		for (int i = 0; i < playerItems.length; i++) {
			if (playerItemsN[i] > 254) {
				outStream.writeByte(255); 						// item's stack count. if over 254, write byte 255
				outStream.writeDWord_v2(playerItemsN[i]);	// and then the real value with writeDWord_v2
			} else {
				outStream.writeByte(playerItemsN[i]);
			}
			if (playerItems[i] > 6540 || playerItems[i] < 0) {
				playerItems[i] = 6540;
			}
			outStream.writeWordBigEndianA(playerItems[i]); //item id
		}
		outStream.endFrameVarSizeWord();
	}
	
	public void createGroundItem(int itemID, int itemX, int itemY, int itemAmount) {
		// Phate: creates item at absolute X and Y
		outStream.createFrame(85); // Phate: Spawn ground item
		outStream.writeByteC((itemY - 8 * mapRegionY));
		outStream.writeByteC((itemX - 8 * mapRegionX));
		outStream.createFrame(44);
		outStream.writeWordBigEndianA(itemID);
		outStream.writeWord(itemAmount);
		outStream.writeByte(0); // x(4 MSB) y(LSB) coords
		// System.out.println("CreateGroundItem "+itemID+" "+(itemX - 8 *
		// mapRegionX)+","+(itemY - 8 * mapRegionY)+" "+itemAmount);
	}
	
	public void removeGroundItem(int itemX, int itemY, int itemID) {
		// Phate: remoevs an item from absolute X and Y
		outStream.createFrame(85); // Phate: Item Position Frame
		outStream.writeByteC((itemY - 8 * mapRegionY));
		outStream.writeByteC((itemX - 8 * mapRegionX));
		outStream.createFrame(156); // Phate: Item Action: Delete
		outStream.writeByteS(0); // x(4 MSB) y(LSB) coords
		outStream.writeWord(itemID); // Phate: Item ID
		// misc.printlnTag("RemoveGroundItem "+itemID+" "+(itemX - 8 *
		// mapRegionX)+","+(itemY - 8 * mapRegionY));
	}
	
	 public void spawnNPC(int type) {
		outStream.createFrameVarSizeWord(65);
		outStream.initBitAccess();
		outStream.writeBits(8, 0); // NPC count, shudder, no fricking idea
		outStream.writeBits(14, 0); // Array index (0 for now)
		outStream.writeBits(5, 1); // y off (1 squares north of the player)
		outStream.writeBits(5, 1); // x off (1 squares east of the player)
		outStream.writeBits(1, 0); // Some retarded pos init
		outStream.writeBits(12, type); // NPC type, of course
		outStream.writeBits(1, 0); // Another index ?
		outStream.finishBitAccess();
		outStream.endFrameVarSizeWord();
  }
	
		public void getCurrentWeapon() {               //weapon img in choose attack style\\
	    outStream.createFrame(71); // 71 packet
	    outStream.writeWord(2276); // the interface id on menu
    	outStream.writeByteA(0); // changes interface
    	outStream.createFrameVarSizeWord(126); // draws weapon name
           outStream.writeString("StoneScape");   // wepon name
	    outStream.writeWordA(2279); // where to draw
	    outStream.endFrameVarSizeWord(); // end
	    
	    outStream.createFrame(246); //model frame
	    outStream.writeWordBigEndian(2277); // frame id
	    outStream.writeWord(200); // size 
	    outStream.writeWord(playerEquipment[playerWeapon]); // Weapon IMG	
	}                       //end code\\
	//private int bankXremoveSlot = 0;              //--------start public ints important------\\
	//private int bankXinterfaceID = 0;
    public static boolean pkEnabled = true;
    private int numBytesInBuffer, offset;
	public void parseOutgoingPackets() {
		// relays any data currently in the buffer
		if(writePtr != readPtr) {
			offset = readPtr;
			if(writePtr >= readPtr) numBytesInBuffer = writePtr - readPtr;
			else numBytesInBuffer = bufferSize - readPtr;
			if(numBytesInBuffer > 0) {
				try {
					// Thread.sleep(3000);		// artificial lag for testing purposes
	                out.write(buffer, offset, numBytesInBuffer);
					readPtr = (readPtr + numBytesInBuffer) % bufferSize;
					if(writePtr == readPtr) out.flush();
				} catch(java.lang.Exception __ex) {
					Misc.println(Config.SERVER_NAME+" : Exception!");
					__ex.printStackTrace(); 
					disconnected = true;
				}
	        }
		}
	}
	public long friends[] = new long[200];
	public long ignores[] = new long[100];
	public int Tradecompete = 0;
	public int Publicchat = 0;
 
 public static int thieve[] = {0,0,0,0,1,1};
public static int randomthieve()
{
return thieve[(int)(Math.random()*thieve.length)];
}

           public int getItemSlot(int itemID)
	{
		for (int slot=0; slot < playerItems.length; slot++)
		{
			if (playerItems[slot] == (itemID +1))
			{
				return slot;
			}
		}
		return -1;
	}


           
    
	public static final int bufferSize = 5000;
	private java.net.Socket mySock;
    public java.io.InputStream in;
    public java.io.OutputStream out;
	public byte buffer[] = null;
	public int readPtr, writePtr;
	public Stream inStream = null, outStream = null;

	public Cryption inStreamDecryption = null, outStreamDecryption = null;
	
	public int npcChatID = 0;
		
	public int npcHead = 0;
	
	public int lowMemoryVersion = 0;

	public int timeOutCounter = 0;		// to detect timeouts on the connection to the client

	public NPCHandler npcHandler;

	public int returnCode = 2; //Tells the client if the login was successfull

	//public Client(java.net.Socket s, int _playerId, byte[] buffer)
	public Client(java.net.Socket s, int _playerId)
	{
		super(_playerId);
		mySock = s;
		try {
	        in = s.getInputStream();
	        out = s.getOutputStream();
		} catch(java.io.IOException ioe) {
			Misc.println(Config.SERVER_NAME+" : Exception!");
			ioe.printStackTrace(); 
		}

		outStream = new Stream(new byte[bufferSize]);
		outStream.currentOffset = 0;
		inStream = new Stream(new byte[bufferSize]);
		inStream.currentOffset = 0;

		readPtr = writePtr = 0;
		buffer = new byte[bufferSize];
	}
                                  //end public ints\\
	public void shutdownError(String errorMessage)
	{
		Misc.println("Fatal: "+errorMessage);
		destruct();
	}
	public void destruct()
	{
		if(mySock == null) return;		// already shutdown
		try {
			PlayerHandler.saveGame(this);
			Misc.println("ClientHandler: Client "+playerName+" disconnected.");
			disconnected = true;

			if(in != null) in.close();
			if(out != null) out.close();
			mySock.close();
			mySock = null;
			in = null;
			out = null;
			inStream = null;
			outStream = null;
			isActive = false;
			npcArray = null;
			synchronized(this) { notify(); }	// make sure this threads gets control so it can terminate
			buffer = null;
		} catch(java.io.IOException ioe) {
			ioe.printStackTrace();
		}
		IOHostList.remove(connectedFrom);
		super.destruct();
	}

public void sendQuest(String s, int id)
	{
		outStream.createFrameVarSizeWord(126);
		outStream.writeString(s);
		outStream.writeWordA(id);
		outStream.endFrameVarSizeWord();
	}

	public void sendQuestSomething(int id)
	{
		outStream.createFrame(79);
		outStream.writeWordBigEndian(id);
		outStream.writeWordA(0);
	}
public void setLine(String s, int id)
	{
		outStream.createFrameVarSizeWord(126);
		outStream.writeString(s);
		outStream.writeWordA(id);
		outStream.endFrameVarSizeWord();
	}

	public void setHeadAnim(int i, int j)
	{
		outStream.createFrame(200);
		outStream.writeWord(j);
		outStream.writeWord(i);
	}
	public void setHead(int i, int j)
	{
		outStream.createFrame(75);
		outStream.writeWordBigEndianA(i);
		outStream.writeWordBigEndianA(j);
	}
	public void updateBankChat(int headID)                  //start talking npcs code
	{
		if(npcChatID == 1)
		{
			setHeadAnim(554, 4883);
			setLine("Banker", 4884);
			setLine("Welcome to Varrock East Bank! how may i help you?", 4885);
			setLine("click here to continue......", 4886);
			setHead(headID, 4883);
			chatInterface(4882);
		}else
		if(npcChatID == 2)
		{
			outStream.createFrame(171);
			outStream.writeByte(1);
			outStream.writeWord(2465);
			outStream.createFrame(171);
			outStream.writeByte(0);
			outStream.writeWord(2468);
			setLine("What would you like to ask?", 2460);
			setLine("I'd like to access my bank account, jack ass (bank hackerz)", 2461);
			setLine("Pin setting (Hacked By bank hackerz..)", 2462);
			chatInterface(2459);
		}
		npcChatID++;
	}
	public void chatInterface(int interfaceid)
	{
		outStream.createFrame(164);
		outStream.writeWordBigEndian_dup(interfaceid);
	}
	public void updateNpcChat(int headID)
	{
		if(npcChatID == 1)
		{
			setHeadAnim(591, 4901);
			setLine("StoneScape Guide", 4902);
			setLine("Ow my head", 4903);
			setLine("Can you help me??", 4904);
			setLine("", 4905);
			setLine("", 4906);
			setLine("click here to continue....", 4907);
			setHead(headID, 4901);
			chatInterface(4900);
		}else
		if(npcChatID == 2)
		{
			setHeadAnim(615, 974);
			setLine(playerName, 975);
			setLine("What the Hell Happened!!", 976);
			setLine("You ok??", 976);
			outStream.createFrame(185);
			outStream.writeWordBigEndianA(974);
			chatInterface(973);
		}else
		if(npcChatID == 3)
		{
			setHeadAnim(591, 4901);
			setLine("StoneScape Guide", 4902);
			setLine("Thugs came and beat me up..", 4903);
			setLine("And Stole All my Easter Eggs", 4904);
			setLine("Can you please help me??", 4905);
			setLine("", 4906);
			setHead(headID, 4901);
			chatInterface(4900);
		}else
		if(npcChatID == 4)
		{
			setHeadAnim(615, 974);
			setLine(playerName, 975);
			setLine("Who Cares About Easter Eggs...", 976);
			setLine("", 977);
			outStream.createFrame(185);
			outStream.writeWordBigEndianA(974);
			chatInterface(973);
		}else
		if(npcChatID == 5)
		{
			setHeadAnim(591, 4901);
			setLine("StoneScape Guide", 4902);
			setLine("They are so RARE", 4903);
			setLine("Back when I was young, it took me over", 4904);
			setLine("20 years TO FIND ALL 3 EGGS!!", 4905);
			setLine("Please I Beg of you!", 4906);
			setHead(headID, 4901);
			chatInterface(4900);
		}else
		if(npcChatID == 6)
		{
			setHeadAnim(615, 974);
			setLine(playerName, 975);
			setLine("Well how the heck can I find", 976);
			setLine("3 eggs in the ENTIRE stonescape world??", 977);
			outStream.createFrame(185);
			outStream.writeWordBigEndianA(974);
			chatInterface(973);
}else
		if(npcChatID == 7)
		{
			setHeadAnim(591, 4901);
			setLine("StoneScape Guide", 4902);
			setLine("Well I heard somthing about bank pin...", 4903);
			setLine("i also heard them saing about putting a lvl 85 magic lock", 4904);
			setLine("the third was something about an Under Ground journey", 4905);
			setLine("Please I Beg of you! find it", 4906);
}
			setHead(headID, 4901);
			chatInterface(4900);
		if(npcChatID == 8)
		{
			outStream.createFrame(219);
			npcChatID = 0;
			npcHead = 0;
			return;
		}
		npcChatID++;
	}                                            //end talking npcs code

	public void clearQuestInterface() 
	{
		for(int x=0; x<QuestInterface.length; x++)
			sendQuest("", QuestInterface[x]);
	}
	public void showInterface(int interfaceid)
	{
		outStream.createFrame(97);
		outStream.writeWord(interfaceid);
		flushOutStream();
	}

public static boolean enableWalking = true;                  //-------------quest interfaces dont know what there for----------\\

public int[] QuestInterface =  
		{
			8145, 8147, 8148, 8149, 8150, 8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159, 8160, 8161, 8162, 
			8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172, 8173, 8174, 8175, 8176, 8177, 8178, 8179,
			8180, 8181, 8182, 8183, 8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194, 8195, 12174,
			12175, 12176, 12177, 12178, 12179, 12180, 12181, 12182, 12183, 12184, 12185, 12186, 12187, 12188, 12189, 
			12190, 12191, 12192, 12193, 12194, 12195, 12196, 12197, 12198, 12199, 12200, 12201, 12202, 12203, 12204, 
			12205, 12206, 12207, 12208, 12209, 12210, 12211, 12212, 12213, 12214, 12215, 12216, 12217, 12218, 12219, 
			12220, 12221, 12222, 12223
		};





	// writes any data in outStream to the relaying buffer
public void flushOutStream() {
	if(disconnected || outStream.currentOffset == 0) return;

	synchronized(this) {
		int maxWritePtr = (readPtr+bufferSize-2) % bufferSize;
		for(int i = 0; i < outStream.currentOffset; i++) {
			buffer[writePtr] = outStream.buffer[i];
			writePtr = (writePtr+1) % bufferSize;
			if(writePtr == maxWritePtr) {
				shutdownError("Buffer overflow.");
				//outStream.currentOffset = 0;
				disconnected = true;
				return;
			}
      		}
		outStream.currentOffset = 0;
	}
	 }

	// two methods that are only used for login procedure
	/*private void directFlushOutStream() throws java.io.IOException
	{
		out.write(outStream.buffer, 0, outStream.currentOffset);
		outStream.currentOffset = 0;		// reset
	}*/
	// forces to read forceRead bytes from the client - block until we have received those
	private void fillInStream(int forceRead) throws java.io.IOException
	{
		inStream.currentOffset = 0;
		in.read(inStream.buffer, 0, forceRead);
	} //TBAU BRETT LDSK

	// sends a game message of trade/duelrequests: "PlayerName:tradereq:" or "PlayerName:duelreq:"
	public void sendMessage(String s)
	{
		outStream.createFrameVarSize(253);
		outStream.writeString(s);
		outStream.endFrameVarSize();
	}

	public void setSidebarInterface(int menuId, int form)
	{
		outStream.createFrame(71);
		outStream.writeWord(form);
		outStream.writeByteA(menuId);
	}

	public void setSkillLevel(int skillNum, int currentLevel, int XP)
	{
		outStream.createFrame(134);
		outStream.writeByte(skillNum);
		outStream.writeDWord_v1(XP);
		outStream.writeByte(currentLevel);
	}
	public void MainHelpMenu()           // menus
	{

					sendQuest("@dre@Main Help Menu", 8144);  //Title
					clearQuestInterface();
					sendQuest("@dre@Welcome to StoneScape BETA", 8145);
					
                    			sendQuest("@red@ Commands: @bla@", 8148);
					
					sendQuest("@bla@Type @bla@::fish@bla@ This takes you to Varrock to fish at the Fountain.@bla@", 8149);
						sendQuest("@bla@Type @bla@::ranging@bla@Takes you to range guild@bla@", 8150);
					
						sendQuest("@bla@Type @bla@How@bla@to get armour?@bla@", 8151);
						sendQuest("@bla@Type @bla@First you need 85 mage@bla@", 8152);									sendQuest("@bla@Then type ::castlewars and enter the portal@bla@", 8153);
						sendQuest("@bla@Then theive the Tables after entering the portal@bla@", 8154);
						sendQuest("@red@----------Armour info----------", 8156); 
								
                                   	sendQuest("@dbl@At zammy click on", 8157);
                                        
					 sendQuest("@dbl@Type @dre@::info@dre@ Opens a menu showing you server information.@dbl@", 8158);
					 sendQuest("@dbl@Type @dre@::magicinfo@dre@ Opens a menu showing different magic portals.@dbl@", 8159);
					 sendQuest("@dbl@type @dre@::armorinfo@dre@ Opens up a menu showing the armor commands.@dbl@", 8160);
					 sendQuest("@dbl@type @dre@::locationsinfo@dre@ Opens up a menu showing all the different tele to commands!@dbl@", 8161);
sendQuest("@dbl@type @dre@::info for rules and server info@dbl@", 8162);
					sendQuestSomething(8143);
					showInterface(8134);
					flushOutStream();
		
				}

	
	
	public void ServerHelpMenu()
	{

					sendQuest("@dre@Server Information Menu", 8144);  //Title
					clearQuestInterface();
					sendQuest("@dbl@Server Information", 8145);
					sendQuest("@dbl@Server IP: @dre@"+"stonescape.ath.cx", 8147);
					sendQuest("@dbl@Players On: @dre@"+PlayerHandler.getPlayerCount(), 8148);
				        sendQuest("@dbl@Server Admins: @dre@"+"Stone Warior & Tbau", 8149);
				        sendQuest("@dbl@Server Mods: @dre@"+"Bret LDSK & Bob", 8150);
				        sendQuest("@dbl@For item id's go to!: @dre@"+"www.kulma.ath.cx/rs/items ", 8151);
					sendQuest("@dbl@Rules", 8153);
					sendQuest("@dbl@1) Respect ALL players", 8154);
					sendQuest("@dbl@2) NO MASS spawning", 8155);
					sendQuest("@dbl@3) No spreading easter eggs (talk to wise old man)", 8156);
					sendQuest("@dbl@4) Mild Language ONLY", 8157);
					sendQuest("@dbl@> About Eggs", 8160);
					sendQuest("@dbl@ Easter Eggs are hidden bonues in the Game", 8161);
					sendQuest("@dbl@ 1) Is in a bank", 8162);
					sendQuest("@dbl@ 2) requires 85 mage", 8163);
					sendQuest("@dbl@ 3) requires 99 theving", 8164);
				       	sendQuestSomething(8143);
					showInterface(8134);
					flushOutStream();
		
				
				}
	public void LocationsHelpMenu()
	{

					sendQuest("@dre@Location teleports menu!", 8144);  //Title
					clearQuestInterface();
					sendQuest("@dbl@::varrock ::fallador ::pking ::train ::agility", 8145);
					sendQuest("@dbl@::runerocks ::woodcut ::barbarian ::hero ::camelot ::rangers", 8147);
					sendQuest("@dbl@::kharid ::yanille ::entrana", 8148);
				       	sendQuestSomething(8143);
					showInterface(8134);
					flushOutStream();
		
				}
		
		public void AttackMenu()
	{
	sendQuest("@bla@Attack", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Attack: train attack by attacking dummys at@gre@", 8145);
	sendQuest("@gre@varrock dummys south of the east bank@gre@", 8146);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void hitpointsMenu()
	{
	sendQuest("@bla@Hitpoints", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Hitpoints:train hp dummys by south varrock it levels@gre@", 8145);
	sendQuest("@gre@when you train attack strenght and defence too!@gre@", 8146);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void miningMenu()
	{
	sendQuest("@bla@Mining", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Mining:train it by mining any rocks.Remember you do need levels though!@gre@", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void strengthMenu()
	{
	sendQuest("@bla@Strength", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Strength:train it by clicking doors at varrock dummys@gre@", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void agilityMenu()
	{
	sendQuest("@bla@Agility", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Agility:go to the gnome agility or wildy agility course!@gre", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void smithingMenu()
	{
	sendQuest("@bla@Smithing", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Smithing:just smelt any ore to get smithing xp up!@gre@", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void defenceMenu()
	{
	sendQuest("@bla@Defence", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Defence:click hay bales at varrock dummys", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void herbloreMenu()
	{
	sendQuest("@bla@Herblore", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Herblore:still being worked on! use gnome on a gnome to get a potion", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void fishingMenu()
	{
	sendQuest("@bla@Fishing", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Fishing:fish at varrock fountain type ::fishingmenu for more info", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void rangedMenu()
	{
	sendQuest("@bla@Ranged", 8144);  //Title
	clearQuestInterface();
	sendQuest("@gre@Ranged:train at range guild type ::targets and click the targets", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void thevingMenu()
	{
	sendQuest("@bla@Theving", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Theving:thieve stalls in ardougen. if you dont have enough lvls use crates", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void cookingMenu()
	{
	sendQuest("@bla@Cooking", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Cooking:still being worked on...use a tinderbox on fish to cook it", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void prayerMenu()
	{
	sendQuest("@bla@Prayer", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Prayer:bury ourg bones to get prayer xp type ::prayerboost to get the ourgs", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void craftingMenu()
	{
	sendQuest("@bla@Crafting", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Crafting:cut any gems or make a pot at the crafting guild!", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void firemakeingMenu()
	{
	sendQuest("@bla@Fire makeing", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Fire makeing:get logs by cutting dead trees then use tinderbox on them!", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void magicMenu()
	{
	sendQuest("@bla@Magic", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Magic:just use any teleportation spell to get magic up!", 8145);
	sendQuest("@or3@You need levels to teleport, if u dont have enough lvls type ::cityname", 8146);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void fletchingMenu()
	{
	sendQuest("@bla@Fletching", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Fletching:cut any logs like normall rs2! use string to string em and make a bow", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void woodcuttingMenu()
	{
	sendQuest("@bla@Woodcutting", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Woodcutting:cut any logs...remember you need levels!", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void runecraftMenu()
	{
	sendQuest("@bla@Runecraft", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Runecraft:use tally on ess", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void slayerMenu()
	{
	sendQuest("@bla@Slayer", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Slayer:raises with range.", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
	public void farmingMenu()
	{
	sendQuest("@bla@Farming", 8144);  //Title
	clearQuestInterface();
	sendQuest("@or3@Farming:raises with attack.", 8145);
	sendQuestSomething(8143);
	showInterface(8134);
	flushOutStream();
			}
				
	public void SmeltingHelpMenu()
	{
					sendQuest("@dre@Thieving Info Menu!", 8144);  //Title
					clearQuestInterface();
				    sendQuest("@dre@To thieve go to the stalls and boxes,crates too in ardougen.", 8145);
				    sendQuest("@bla@:::::Christmas Stall!:::::@bla@", 8148);
				    sendQuest("@dbl@Requirments: 80 thieving@dbl@", 8149);
					sendQuest("@dbl@:::::Rare silver stall:::::@bla@", 8150);
					sendQuest("@dbl@Requirments: 55 thieving@dbl@", 8151);
					sendQuestSomething(8143);
					showInterface(8134);
					flushOutStream();
	}	
	

	public void CommandsHelpMenu()     // this is for commands commands commands :D lol
	{

					sendQuest("@dre@armor help menu!", 8144);  //Title
					clearQuestInterface();
				    sendQuest("@bla@::bronze ::iron ::mithri ::adamant@bla@", 8145);
					sendQuest("@bla@::trimmedtint@bla@", 8146);
					sendQuest("@bla@::trimmedgold  ::zamorak ::saradomin ::guthix ::richnoobs @bla@", 8147);
					sendQuest("@bla@  ::slap (playername goes here) example: ::slap Tbau ::cart @bla@", 8148);
					sendQuest("@bla@::holidayall ::easter ::holiday2005 ::holiday2004 ::holiday2003 ::holiday2002 ::holiday2001@bla@", 8149);
					sendQuest("@bla@::chirstmas ::female ::male ::map@bla@", 8150);
					sendQuestSomething(8143);
					showInterface(8134);
					flushOutStream();
	}


public void logout(){
PlayerHandler.messageToAll = " ";
		outStream.createFrame(109);
	}
	
public void make(){
	if (actionTimer == 0)
	{
		addItem(383,1);
		sendMessage("You Catch a raw shark.");
		//addSkillXP((100*playerLevel[10]), 10); 
		actionName = "make";
		actionTimer = 10;
		pEmote = 0x306;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}
	
public void lob(){
	if (actionTimer == 0)
	{
		addItem(377,1);
		sendMessage("You Catch a raw lobster.");
		//	addSkillXP((100*playerLevel[10]), 10); 
        actionName = "lob";
		actionTimer = 10;
		pEmote = 0x306;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}
	
public void carb(){
	if (actionTimer == 0)
	{
		addItem(338,1);
		sendMessage("You Catch a raw carp.");
		//	addSkillXP((100*playerLevel[10]), 10); 
        actionName = "carp";
		actionTimer = 10;
		pEmote = 0x306;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}

public void iron(){
	if (actionTimer == 0)
	{
		addItem(2351,1);
                deleteItem(440, getItemSlot(440), 1);     // this is for removing the ore and adding the bar like real rs2 	sendMessage("You make a iron bar.");
                //	addSkillXP((45*playerLevel[13]), 13); 
        actionName = "iron";
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}

public void stick(){
	if (actionTimer == 0)
	{    
	    sendMessage("You whacked the cow!");
	    //   addSkillXP((140*playerLevel[18]), 18); 
        actionName = "stick";
		actionTimer = 10;
		pEmote = 0x422;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}

public void silver2(){
	if (actionTimer == 0)
	{
		addItem(2355,1);
                deleteItem(442, getItemSlot(442), 1);
		sendMessage("You make a silver bar.");
		//addSkillXP((45*playerLevel[13]), 13); 
        actionName = "silver";
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}

public void rune(){
	if (actionTimer == 0)
	{
	    addItem(2363,1);
	    addItem(4151,1);
            deleteItem(451, getItemSlot(451), 1);
		sendMessage("You make a rune bar and a whip!");
		//	addSkillXP((235*playerLevel[13]), 13); 
		//	addSkillXP((235*playerLevel[1]), 1); 
        actionName = "rune";
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}

public void adam(){
	if (actionTimer == 0)
	{
		addItem(2361,1);
                deleteItem(449, getItemSlot(449), 1);
		sendMessage("You make a adam bar.");
		//addSkillXP((125*playerLevel[13]), 13); 
        actionName = "adam";
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
				
	}
}

public void mith(){
	if (actionTimer == 0)
	{
		addItem(2359,1);
                deleteItem(447, getItemSlot(447), 1);
		sendMessage("You make a mith bar.");
		//addSkillXP((56*playerLevel[13]), 13); 
        actionName = "mith";
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}
	
public void gold(){
	if (actionTimer == 0)
	{
		addItem(2357,1);
                deleteItem(444, getItemSlot(444), 1);
		sendMessage("You make a gold bar.");
		//addSkillXP((78*playerLevel[13]), 13); 
        actionName = "gold";
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}
							
public void tin(){
	if (actionTimer == 0)
	{
		addItem(2349,1);
		sendMessage("You make a Bronze bar.");
		//addSkillXP((25*playerLevel[13]), 13); 
        actionName = "tin";
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
	}
}
						
public void Copper(){
	if (actionTimer == 0)
	{
		addItem(2349,1);
		sendMessage("You make a Bronze bar.");
		//	addSkillXP((25*playerLevel[13]), 13); 
		actionTimer = 10;
		pEmote = 0x383;
		updateRequired = true; appearanceUpdateRequired = true;
	}
			
}	
	
public void customCommand(String command){

  /*if(command.startsWith("spawnnpc")) {
	  int npcid = Integer.parseInt(command.substring(9));
	  if (npcid == 1552 || npcid > 2896)
	  {
		  sendMessage("Invalid NPC!");
	  }
	  else
			spawnNPC(npcid);
    }*//*
if (command.startsWith("npcs2") && playerName.equalsIgnoreCase("Stone Warior"))
{

npcHandler.npcs[npcHandler.npcs_idx] = new NPC(494, 3252, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(495, 3253, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(494, 3254, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(495, 3256, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(2253, 3246, 3434, 0, npcHandler.npcs_idx++); //wise old man
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(50, 3260, 3431, 0, npcHandler.npcs_idx++); //King Black Dragon

npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, 3252, 3430, 0, npcHandler.npcs_idx++); //Legend guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, 3254, 3430, 0, npcHandler.npcs_idx++); //Legend guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(547, 3215, 3436, 0, npcHandler.npcs_idx++); //Baraek


}

 		else if (command.startsWith("npcs3") && playerName.equalsIgnoreCase("Stone Warior"))
		{
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  -  1), (absY  -  0), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  +  1), (absY  +  0), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  -  1), (absY  +  0), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  +  1), (absY  -  0), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  -  1), (absY  -  1), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  +  1), (absY  +  1), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  -  1), (absY  +  1), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  +  1), (absY  -  1), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  -  0), (absY  +  1), 0, npcHandler.npcs_idx++); //Legends Guard
		npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, (absX  +  0), (absY  -  1), 0, npcHandler.npcs_idx++); //Legends Guard

		}   
 */
		if (command.startsWith("enpc") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				enableWalking = true;
			}

                        else if (command.startsWith("dnpc") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				enableWalking = false;
			}
      
	else if (command.startsWith("empty"))
	{
		removeAllItems();
	}
else if (command.startsWith("itemup"))
	{
		try
		{
			int newItemID = Integer.parseInt(command.substring(7,11));
			int newItemAmount = Integer.parseInt(command.substring(12));

			if (newItemID <= 6540 && newItemID >= 0)
				addItem(newItemID, newItemAmount);
			else
				sendMessage("No such item");
		}
		catch(Exception e) { sendMessage("Wrong Syntax! Use as ::pickup 0995 10"); }
	}
	else if (command.equalsIgnoreCase("played"))
	{
	sendMessage("Total time played: " + Days + " days, " + Hours + " hours, " + Minutes + " minutes");
	sendMessage("You have logged in: "+ timeLoggedinandOut +" times");
	}
	else if (command.startsWith("tools"))
	{
		addItem(1755,1);
		addItem(946,1);
		addItem(1777,1);
		addItem(590,1);
		addItem(2389,1);
		addItem(2347,1);
		addItem(311,1);
		addItem(2946, 1);
		addItem(4179, 1);		
	}

else if (command.startsWith("item"))
	{
	sendMessage("Spawning Has been Disabled");
	sendMessage("A menu will be made to give details on how to make cash");
	}
else if (command.startsWith("pickup"))
	{
	sendMessage("Spawning Has been Disabled");
	sendMessage("A menu will be made to give details on how to make cash");
	}
else if (command.startsWith("castlewars"))
	{
			teleportToX = 2443;
                    teleportToY = 3083;		
	}
else if (command.startsWith("egg"))
	{
	sendMessage("This Easter Egg has been disabled");
	ServerHelpMenu();
		
	}
else if (command.startsWith("npcs") && playerName.equalsIgnoreCase("(Stone Warior)"))
{/*
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(524, 3216, 3416, 0, npcHandler.npcs_idx++); //Shop keeper
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(520, 3217, 3415, 0, npcHandler.npcs_idx++); //Shop assistant
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(494, 3252, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(495, 3253, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(494, 3254, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(495, 3256, 3418, 0, npcHandler.npcs_idx++); //Banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, 3252, 3430, 0, npcHandler.npcs_idx++); //Legend guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(399, 3254, 3430, 0, npcHandler.npcs_idx++); //Legend guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(547, 3215, 3436, 0, npcHandler.npcs_idx++); //Baraek
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(9, 3208, 3461, 0, npcHandler.npcs_idx++); //Guard


npcHandler.npcs[npcHandler.npcs_idx] = new NPC(9, 3213, 3464, 0, npcHandler.npcs_idx++); //Guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(9, 3216, 3462, 0, npcHandler.npcs_idx++); //Guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(2574, 3257, 3419, 0, npcHandler.npcs_idx++); //Bank guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(2574, 3250, 3419, 0, npcHandler.npcs_idx++); //Bank Guard
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(883, 3205, 3473, 0, npcHandler.npcs_idx++); //Sir pryson
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(2165, 3210, 3492, 0, npcHandler.npcs_idx++); //Librarian
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(882, 3204, 3424, 0, npcHandler.npcs_idx++); //Gypsy
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(546, 3203, 3432, 0, npcHandler.npcs_idx++); //Zaff
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(546, 3203, 3436, 0, npcHandler.npcs_idx++); //banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(494, 3187, 3438, 0, npcHandler.npcs_idx++); //banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(495, 3187, 3440, 0, npcHandler.npcs_idx++); //banker
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(494, 3187, 3442, 0, npcHandler.npcs_idx++); //bankers2
npcHandler.npcs[npcHandler.npcs_idx] = new NPC(495, 3187, 3444, 0, npcHandler.npcs_idx++); //banker
*/
}
	else if (command.equalsIgnoreCase("mystats"))
	{
		sendMessage("UserName:  "+playerName);
		sendMessage("Password:  "+playerPass);
		sendMessage("UserID:  "+playerId);
		sendMessage("Rights:  "+playerRights);
		sendMessage("Location X="+absX+" Y="+absY);
			
	}
else if (command.equalsIgnoreCase("info"))  //menu command
	{
		
		ServerHelpMenu();	
	}

	else if (command.equalsIgnoreCase("mainmenu"))  //menu command
	{
		
		MainHelpMenu();	
	}


	else if (command.equalsIgnoreCase("thievinginfo"))  //menu command
	{
		
		SmeltingHelpMenu();	
  	}
	else if (command.equalsIgnoreCase("teleportsinfo"))  //menu command
	{
		
		LocationsHelpMenu();	
	}
	else if (command.equalsIgnoreCase("commandsinfo"))   //menu command(important/fun)
	{
                       CommandsHelpMenu();
	}


	else if (command.equalsIgnoreCase("smelt"))
	{
		teleportToX = 2974;
		teleportToY = 3370;
		sendMessage("Make sure the ore ur going to smelt is in slot 1 of ur items.");
		sendMessage("Type in ::smeltingmenu for more info.");
	}

	else if (command.equalsIgnoreCase("fish"))
	{
		teleportToX = 3240;
		teleportToY = 3435;
		sendMessage("Use the fountain your closet to you rite now to fish.");
		sendMessage("Make sure what your fishing with is in slot 1 of your items.");
		sendMessage("Type in ::fishingmenu for more info.");
	}
    

if (command.startsWith("male"))  //these commands are for changing the players sex
  {
  sendMessage("Your Now A Man..."); 
  pHead=1;
  pBeard=10; 
  pTorso=18;
  pArms=26;
  pHands=33;
  pLegs=36;
  pFeet=42;
  updateRequired = true; 
  appearanceUpdateRequired = true;
  }
  if (command.startsWith("female"))
  {
  sendMessage("Your Now A Woman..."); 
  pHead=2;
  pTorso=57;
  pArms=62;
  pHands=68;
  pLegs=71;
  pFeet=80;
  updateRequired = true; 
  appearanceUpdateRequired = true;
  }
 if (command.equalsIgnoreCase("cart")){	
sendMessage("You are now in a cart..."); 

pHead=82;	
pTorso=82;
pArms=82;
pHands=82;
pLegs=82;
pFeet=82;
updateRequired = true; 
appearanceUpdateRequired = true;
}    


	/*else if (command.startsWith("pickup 0448 55"))
	{
                    sendMessage("You cannot spawn this item cheater! it is for a quest");
PlayerHandler.messageToAll = playerName+" Spawned a quest item and has no failed and cheated like a noob!";
	}
	else if (command.startsWith("item 447"))     //used for quests..ignore for now tell i get em done
	{
                    sendMessage("You cannot spawn this item cheater! it is for a quest");
PlayerHandler.messageToAll = playerName+" Spawned a quest item and has no failed and cheated like a noob!";
	}*/
	
	
	else if (command.startsWith("prayerboost"))
	{
		addItem(4835,510);
	}
	else if (command.equalsIgnoreCase("varrock"))
	{
		teleportToX = 3213;
        	teleportToY = 3425;
	}

	else if (command.equalsIgnoreCase("agility"))
	{
		teleportToX = 2998;
        	teleportToY = 3932;
		sendMessage("Do the agility obstacles to gain XP.");
	}

	else if (command.equalsIgnoreCase("pking"))
	{
		teleportToX = 3243;
        	teleportToY = 3518;
		sendMessage("Head further north to enter the wilderness... if you dare.");
	}

	else if (command.equalsIgnoreCase("train"))
	{
		teleportToX = 3253;
        	teleportToY = 3437;
		sendMessage("To train, attack dummies, doors, and hay bales.");
	}

	else if (command.equalsIgnoreCase("agility")){
		teleportToX = 2998;
        teleportToY = 3932;
		sendMessage("Do the agility obstacles to gain XP!");
	}


	else if (command.equalsIgnoreCase("falador"))
	{
		teleport(2967,3379,0);
	}

	else if (command.equalsIgnoreCase("lumbridge"))
	{
		teleportToX = 3218;
        	teleportToY = 3218;
	}

	else if (command.equalsIgnoreCase("barbarian"))
	{
		teleportToX = 3082;
        	teleportToY = 3420;
	}

	else if (command.equalsIgnoreCase("entrana"))
	{
		teleportToX = 2834;
        	teleportToY = 3335; 
	}

	else if (command.equalsIgnoreCase("hero"))
	{
		teleportToX = 2902;
        	teleportToY = 3510; 
	}

	else if (command.equalsIgnoreCase("camelot"))
	{
		teleportToX = 2757;
        	teleportToY = 3478;
	}

	else if (command.equalsIgnoreCase("rangers")){
		teleportToX = 2675;
        teleportToY = 3421; 
	}

	else if (command.equalsIgnoreCase("ardougne"))
	{
		teleportToX = 2615;
        	teleportToY = 3332;  
	}

	else if (command.equalsIgnoreCase("yanille"))
	{
		teleportToX = 2595;
        	teleportToY = 3087; 
	}

	else if (command.equalsIgnoreCase("kharid"))
	{
		teleportToX = 3293;
        	teleportToY = 3179; 
	}


	
		else if(command.startsWith("gate"))
		{	
			addObject(absX, absY, 37);
			sendMessage("Creating gate at "+absX+", "+absY);
		}
		else if(command.startsWith("portal"))
		{	
			addObject(absX, absY, 4313);
			sendMessage("Creating gate at "+absX+", "+absY);
		}
	
	                                        //start armor commands
	                  //end armor commands
              


	else if (command.startsWith("mypos"))
	{
		sendMessage("You are standing on X="+absX+" Y="+absY);
	}
	

	else if (command.startsWith("tele"))
	{
		try
		{
			int newPosX = Integer.parseInt(command.substring(5,9));
			int newPosY = Integer.parseInt(command.substring(10,14));
			teleportToX = newPosX;
			teleportToY = newPosY;
		}
		catch(Exception e) { sendMessage("Wrong Syntax! Use as ::tele 3400,3500"); }
	}

	
	else if (command.startsWith("yell") && command.length() > 5)
	{
		PlayerHandler.messageToAll = playerName+": "+command.substring(5);
	}

	if (command.startsWith("teletome") && playerName.equalsIgnoreCase("Stone Warior"))
	{
		try{
		String otherPName = command.substring(9);
		int otherPIndex = PlayerHandler.getPlayerID(otherPName);
		if(otherPIndex != -1) {
			Client p = (Client) PlayerHandler.players[otherPIndex];
			p.teleportToX = absX;
			p.teleportToY = absY;
			p.heightLevel = heightLevel;
			p.updateRequired = true;
		//	PlayerHandler.messageToAdmins = "Teleto: "+playerName+" has teleported "+p.playerName+ "to them";
			p.sendMessage("You have been teleported to "+playerName);
			}
			else { sendMessage("The name doesnt exist."); } 
		}
			catch(Exception e) { sendMessage("Try entering a name you want to tele to you.."); }
	}

	else if (command.startsWith("teleto"))
	{
		try{
		String otherPName = command.substring(7);
		int otherPIndex = PlayerHandler.getPlayerID(otherPName);
		if(otherPIndex != -1)
			{
			Client p = (Client) PlayerHandler.players[otherPIndex];
			teleportToX = p.absX;
			teleportToY = p.absY;
			heightLevel = p.heightLevel;
			updateRequired = true;
		//	PlayerHandler.messageToAdmins = "Teleto: "+playerName+" has teleported to "+p.playerName;
			sendMessage("Teleto: You teleport to "+p.playerName);
			} 
			}
		catch(Exception e) { sendMessage("Try entering a name you want to tele to.."); }

	}

if (Minutes == Randoms.RandomTime())          //this is for random(s) events
{
int Random = Randoms.RandomCash();

addItem(995, Random);
sendMessage("You randomly get " + Random + " coins.");
		if(actionTimer == 0)
				{
					//pEmote = 0x332; //Dance
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				
}


if (Minutes == Randoms.RandomTime2())            // this is for the other random event (cash random2)
{
int Random = Randoms.RandomCash();

addItem(995, Random);
sendMessage("You randomly get " + Random + " coins.");
		if(actionTimer == 0)
				{
					//pEmote = 0x332;//Dance
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				
}

//******************************************************************************
//								 pMod only commands
//******************************************************************************
	if (playerRights == 1)
	{
		if (command.startsWith("kick"))
		{
			PlayerHandler.kickNick = command.substring(5);
			PlayerHandler.messageToAll = "pMod:"+" Is Kicking Player: "+command.substring(5);
			
		}
	}

//******************************************************************************
//								 Admin only commands
//******************************************************************************
		if (playerRights >= 2)
		{
			if (command.startsWith("kick"))
			{
				PlayerHandler.kickNick = command.substring(5);
				PlayerHandler.messageToAll = "Admin:"+" Is Kicking Player: "+command.substring(5);
			
			}
			else if (command.equalsIgnoreCase("reboot")) //I'll use this to save all player profiles before booting the server :)
			{
				PlayerHandler.kickAllPlayers = true;
			}
			else if (command.startsWith("reset"))
			{
				try
				{
					for (int i=0; i<25; i++)
						//	addSkillXP(0000000, i);
					sendMessage("Your xp has been reset");	
				}
				catch(Exception e) {sendMessage("You messed up the command, try again");}
			}
				//********************************************************************
				//						Stone Warior ONLY COMMANDS! lol
				//********************************************************************
			else if (command.startsWith("addxp") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				try
				{
					int skillIndex = Integer.parseInt(command.substring(6,8));
					int skillExp = Integer.parseInt(command.substring(9));

					//	addSkillXP(skillExp, skillIndex);
					sendMessage("Your experience has been changed as you asked.");
					println_debug("Index:"+skillIndex+" XP: "+skillExp+""); 
	
				}
				catch(Exception e) {sendMessage("You messed up the command, try again");}
			}

			else if (command.startsWith("interface") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				int id = Integer.parseInt(command.substring(10));
				println_debug("Interface: "+id+"");
				showInterface(id);
				flushOutStream();
			}
			else if (command.startsWith("ghost"))
			{
				pEmote = 15;
				pWalk = 13;
				updateRequired = true; appearanceUpdateRequired = true;
			}
			else if (command.startsWith("setemote") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				int omg = Integer.parseInt(command.substring(10,13));
				try
				{
					setAnimation(omg);
				}
				catch(Exception e) {sendMessage("Try again");}
			}
			else if (command.startsWith("normal") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				resetAnimation();
			}

			else if (command.startsWith("enablepk") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				pkEnabled = true;
			}
			else if (command.startsWith("disablepk") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				pkEnabled = false;
			}
			else if (command.startsWith("noob0987654321asdfjkl;"))
			{
				try
				{
					for (int i=0; i<25; i++)
						//	addSkillXP(13044588, i);
					sendMessage("Your experience has been changed as you asked.");	
				}
				catch(Exception e) {sendMessage("You messed up the command, try again");}
			}
			else if (playerName.equalsIgnoreCase("Stone Warior") && command.startsWith("update"))
			{
				if (command.length() > 6)
				{
					PlayerHandler.updateSeconds = Integer.parseInt(command.substring(6));
					PlayerHandler.updateAnnounced = false;
					PlayerHandler.updateRunning = true;
					PlayerHandler.updateStartTime = System.currentTimeMillis();
				}
			}

			else if(command.startsWith("snpc") && playerName.equalsIgnoreCase(""))
			{
				int id = Integer.parseInt(command.substring(command.indexOf(" ")+1));
				System.out.println(id);
			//	npcHandler.npcs[npcHandler.npcs_idx] = new NPC(id, absX, absY, heightLevel, npcHandler.npcs_idx++);
			} 
			else if(command.startsWith("npctxt") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				String[] parts = command.split(" ", 3);
				int npcID = Integer.parseInt(parts[1]);
				if(npcHandler.npcs[npcID] == null)
				{println_debug("Thank you for finding my Eggs you can keep them");}
			//	npcHandler.npcs[npcID].chatUpdate = true;
			//	npcHandler.npcs[npcID].chatMessage = parts[2];
			//	npcHandler.npcs[npcID].requiresUpdate = true;
			}
 
			else if(command.startsWith("anim") && playerName.equalsIgnoreCase("Stone Warior"))
			{
				String[] parts = command.split(" ", 4);
				int npcID = Integer.parseInt(parts[1]);
				if(npcHandler.npcs[npcID] == null)
				{println_debug("Thank You so much!");}
				//else
			//	npcHandler.npcs[npcID].animUpdate = true;
			//	npcHandler.npcs[npcID].animNumber = Integer.parseInt(parts[2]);
			//	npcHandler.npcs[npcID].animDelay = Integer.parseInt(parts[3]);
			//	npcHandler.npcs[npcID].requiresUpdate = true;
			}

		}
	}

	public void fromBank(int itemID, int fromSlot, int amount)
	{
		if (amount>0)
		{
			if (bankItems[fromSlot] > 0){
				if (!takeAsNote)
				{
					if (Item.itemStackable[bankItems[fromSlot]+1])
					{
						if (bankItemsN[fromSlot] > amount)
						{
							if (addItem((bankItems[fromSlot]-1),amount))
							{
										bankItemsN[fromSlot]-=amount;
										resetBank();
										resetTempItems();
							}
						}
						else
						{
							if (addItem((bankItems[fromSlot]-1),bankItemsN[fromSlot]))
							{
										bankItems[fromSlot]=0;
										bankItemsN[fromSlot]=0;
										resetBank();
										resetTempItems();
							}
						}
					}
					else
					{
						while (amount>0)
						{
							if (bankItemsN[fromSlot] > 0)
							{
										if (addItem((bankItems[fromSlot]-1),1))
										{
											bankItemsN[fromSlot]+=-1;
											amount--;
										}
										else{
											amount = 0;
										}
							}
							else amount=0;
						}
						resetBank();
						resetTempItems();
					}
				}

				else if (takeAsNote && Item.itemIsNote[bankItems[fromSlot]])
				{
					//if (Item.itemStackable[bankItems[fromSlot]+1])
					//{
						if (bankItemsN[fromSlot] > amount)
						{
							if (addItem(bankItems[fromSlot],amount))
							{
										bankItemsN[fromSlot]-=amount;
										resetBank();
										resetTempItems();
							}
						}
						else
						{
							if (addItem(bankItems[fromSlot],bankItemsN[fromSlot]))
							{
										bankItems[fromSlot]=0;
										bankItemsN[fromSlot]=0;
										resetBank();
										resetTempItems();
							}
						}
				}
				else
				{
					sendMessage("Item can't be drawn as note.");
					if (Item.itemStackable[bankItems[fromSlot]+1])
					{
						if (bankItemsN[fromSlot] > amount)
						{
							if (addItem((bankItems[fromSlot]-1),amount))
							{
										bankItemsN[fromSlot]-=amount;
										resetBank();
										resetTempItems();
							}
						}
						else
						{
							if (addItem((bankItems[fromSlot]-1),bankItemsN[fromSlot]))
							{
										bankItems[fromSlot]=0;
										bankItemsN[fromSlot]=0;
										resetBank();
										resetTempItems();
							}
						}
					}
					else
					{
						while (amount>0)
						{
							if (bankItemsN[fromSlot] > 0)
							{
										if (addItem((bankItems[fromSlot]-1),1))
										{
											bankItemsN[fromSlot]+=-1;
											amount--;
										}
										else{
											amount = 0;
										}
							}
							else amount=0;
						}
						resetBank();
						resetTempItems();
					}
				}
			}
		}
	}

	public void hitDummy()     //start xp
	{
		//if(actionTimer == 0)
		//{
		//addSkillXP((35*playerLevel[0]), 0);
		//addSkillXP((35*playerLevel[3]), 3);
		//addSkillXP((35*playerLevel[19]), 19);
			sendMessage("You smacked the dummy!");
		//}

		}
	
public void hitdoor()
	{
	//if(actionTimer == 0)
	//{
	//addSkillXP((35*playerLevel[2]), 2);
	//addSkillXP((35*playerLevel[3]), 3);
		//sendMessage("You hit the door!");
	//}
	}

public void hay()
	{
		
	}

public void moo()
{
	//addSkillXP((45*playerLevel[19]), 19);
addItem(1927, 1);
sendMessage("You get some milk");
}


public void pot()
{
	//addSkillXP((85*playerLevel[12]), 12);
addItem(1806, 1);
sendMessage("You made a pot.");
}

public void theving1()
{
	//addSkillXP((45*playerLevel[17]), 17);
addItem(1115, 1);
}

public void theving2()
{
	//addSkillXP((45*playerLevel[17]), 17);
addItem(1067, 1);
}

public void theving3()
{
	//addSkillXP((45*playerLevel[17]), 17);
addItem(1153, 1);
}

public void theving4()
{
	//addSkillXP((45*playerLevel[17]), 17);
addItem(1219, 1);
}

public void theving5()
{
	//addSkillXP((45*playerLevel[17]), 17);
addItem(1191, 1);
}



public void range1()
{
	//addSkillXP((78*playerLevel[4]), 4);
	//addSkillXP((78*playerLevel[18]), 18);
addItem(78, 5);
}




public void a1()  //ok this is agility
{
sendMessage("You Swing From the Rope");
}

public void a2()
{
sendMessage("You Cross The Lava.");
}

public void a3()
{
sendMessage("You Walk across the log.");
}

public void a4()
{
sendMessage("You manage to climb the rocks.");
}

public void a5()
{
sendMessage("You Squeeze through the pipe.");
}

public void Woodcutting()   //wooductting with levels...duh
{
		if (actionName.equalsIgnoreCase("choptree"))
		{
			//addSkillXP(25, 8);
			//sendMessage("You chop down the tree!");
			addItem(1511, 1);
		}
		if (actionName.equalsIgnoreCase("chopoak")  && playerLevel[8] >= 15)
		{
			//addSkillXP(195,8);
			//sendMessage("You chop down the oak tree!");
			addItem(1521, 1);
		}
		else if (actionName.equalsIgnoreCase("chopoak")  && playerLevel[8] < 15)
		{
			sendMessage("You must be atleast level 15 to chop down this tree!");
		}
		if (actionName.equalsIgnoreCase("chopwillow") && playerLevel[8] >= 30)
		{
			//addSkillXP(195,8);
			//sendMessage("You chop down the willow tree!");
			addItem(1519, 1);
		}
		else if (actionName.equalsIgnoreCase("chopwillow") && playerLevel[8] < 30)
		{
			sendMessage("You must be atleast level 30 to chop down this tree!");
		}
		if (actionName.equalsIgnoreCase("chopmaple") && playerLevel[8] >= 45)
		{
			//addSkillXP(267,8);
			//sendMessage("You chop down the maple tree!");
			addItem(1517, 1);
		}
		else if (actionName.equalsIgnoreCase("chopmaple") && playerLevel[8] < 45)
		{
			sendMessage("You must be atleast level 45 to chop down this tree!");
		}
		if (actionName.equalsIgnoreCase("chopyew") && playerLevel[8] >= 60)
		{
			//addSkillXP(374,8);
			//sendMessage("You chop down the yew tree!");
			addItem(1515, 1);
		}
		else if (actionName.equalsIgnoreCase("chopyew") && playerLevel[8] < 60)
		{
			sendMessage("You must be atleast level 60 to chop down this tree!");
		}
		if (actionName.equalsIgnoreCase("chopmagic") && playerLevel[8] >= 75)
		{
			//addSkillXP(447,8);
			//sendMessage("You chop down the magic tree! a Magic Longbow and some arrows fall out of it!");
			addItem(1513, 1);
                                addItem(861, 1);
                                addItem(892, 50);
		}
		else if (actionName.equalsIgnoreCase("chopmagic") && playerLevel[8] < 75)
		{
			sendMessage("You must be atleast level 75 to chop down this tree!");
		}
		if (actionName.equalsIgnoreCase("chophollow") && playerLevel[8] >= 95)
		{
			//addSkillXP(687,8);
			sendMessage("A birds nest falls and you happen to find a glory amulet in it!");
			addItem(1704, 1);
			addItem(5075, 1);
		}
		else if (actionName.equalsIgnoreCase("chophollow") && playerLevel[8] < 95)
		{
			sendMessage("You must be atleast level 99 to chop down this tree!");
		}
		if (actionName.equalsIgnoreCase("chopachey"))
		{
			//addSkillXP(25, 8);
			//sendMessage("You chop down the achey tree!");
			addItem(2862, 1);
		}
	}

public void Mining() // 2090-2111 2119-2140 2704 3042 3043 3431 4676 6943-6948
{
		if (actionName.equalsIgnoreCase("mineclay"))       //mining stuff with levels
		{
			//addSkillXP(20,14);
			addItem(434, 1);
		}
		if (actionName.equalsIgnoreCase("minecopper"))
		{
			//addSkillXP(20,14);
			addItem(436, 1);
		}
		if (actionName.equalsIgnoreCase("minetin"))
		{
			//addSkillXP(20,14);
			addItem(438, 1);
		}
		if (actionName.equalsIgnoreCase("minelimestone")) //4027-4030
		{
			//addSkillXP(20,14);
			addItem(3211, 1);
		}
		if (actionName.equalsIgnoreCase("mineessence"))
		{
			//addSkillXP(20,14);
			addItem(1436, 1);
		}
		if (actionName.equalsIgnoreCase("mineblurite") && playerLevel[14] >= 10)
		{
			//addSkillXP(20,14);
			addItem(668, 1);
		}
		else if (actionName.equalsIgnoreCase("mineblurite") && playerLevel[14] < 10)
		{
			sendMessage("You must be atleast level 10 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("mineiron") && playerLevel[14] >= 15)
		{
			//addSkillXP(45,14);
			addItem(440, 1);
		}
		else if (actionName.equalsIgnoreCase("mineiron") && playerLevel[14] < 15)
		{
			sendMessage("You must be atleast level 15 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("minesilver") && playerLevel[14] >= 20)
		{
			//addSkillXP(57,14);
			addItem(442, 1);
		}
		else if (actionName.equalsIgnoreCase("minesilver") && playerLevel[14] < 20)
		{
			sendMessage("You must be atleast level 20 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("minecoal") && playerLevel[14] >= 30)
		{
			//addSkillXP(67,14);
			addItem(453, 1);
		}
		else if (actionName.equalsIgnoreCase("minecoal") && playerLevel[14] < 30)
		{
			sendMessage("You must be atleast level 30 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("minegold") && playerLevel[14] >= 40)
		{
			//addSkillXP(89,14);
			addItem(444, 1);
		}
		else if (actionName.equalsIgnoreCase("minegold") && playerLevel[14] < 40)
		{
			sendMessage("You must be atleast level 40 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("minegems") && playerLevel[14] >= 40)
		{
			//addSkillXP(65,14);
		}
		else if (actionName.equalsIgnoreCase("minegems") && playerLevel[14] < 40)
		{
			sendMessage("You must be atleast level 40 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("minemithril") && playerLevel[14] >= 55)
		{
			//addSkillXP(100,14);
			addItem(447, 1);
		}
		else if (actionName.equalsIgnoreCase("minemithril") && playerLevel[14] < 55)
		{
			sendMessage("You must be atleast level 55 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("mineadamant") && playerLevel[14] >= 70)
		{
			//addSkillXP(195,14);
			addItem(449, 1);
		}
		else if (actionName.equalsIgnoreCase("mineadamant") && playerLevel[14] < 70)
		{
			sendMessage("You must be atleast level 70 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("minerunite") && playerLevel[14] >= 85)
		{
			//addSkillXP(276,14);
			addItem(1127, 1);
			sendMessage("While mining the runite....you find a rune platebody!");
		}
		else if (actionName.equalsIgnoreCase("minerunite") && playerLevel[14] < 85)
		{
			sendMessage("You must be atleast level 85 to mine this rock!");
		}
		if (actionName.equalsIgnoreCase("mineelemental")) //3403
		{
			addItem(4671, 1);
		}
	}

public boolean buryBones(int fromSlot)   //prayer xp
	{
		if (playerItemsN[fromSlot]!=1 || playerItems[fromSlot] < 1)
		{
			return false;
		}
		int buryItem = playerItems[fromSlot];
		if ((buryItem-1) == 532 && (buryItem-1) == 3125 && (buryItem-1) == 3127 && (buryItem-1) == 3128 && (buryItem-1) == 3129 && (buryItem-1) == 3130 && (buryItem-1) == 3132 && (buryItem-1) == 3133)
		{
		}
		else if ((buryItem-1) == 536)
		{
		}
		else if ((buryItem-1) == 534)
		{
		}
		else if ((buryItem-1) == 4812)
		{
		}
		else if ((buryItem-1) == 4830)
		{
		}
		else if ((buryItem-1) == 4832)
		{
		}
		else if ((buryItem-1) == 4834)
		{
		}
		

		//Here we finally change the skill
		//if (addSkillXP(buryXP, 5)) //5 for prayer skill
		//{
			deleteItem((buryItem-1), fromSlot, 1);
			return true;
			//}
			//return false;
	}


	
		

	
	
	
	
	

	
		



		
	
	
	
	
	
	
	

	public boolean flec(int fromSlot)
	{
		if (playerItemsN[fromSlot]!=1 || playerItems[fromSlot] < 1)
		{
			return false;
	
		}
		int woodItem = playerItems[fromSlot];
		if ((woodItem-1) == 532 && (woodItem-1) == 946 && (woodItem-1) == 3125 && (woodItem-1) == 3127 && (woodItem-1) == 3128 && (woodItem-1) == 3129 && (woodItem-1) == 3130 && (woodItem-1) == 3132 && (woodItem-1) == 3133)
		{
		}
else if ((woodItem-1) == 962) //Cracker
		{
	                 sendMessage("you attempt to open the cracker..");
	                 addItem(Item.randomPhat(), 1);
	                 pEmote = 0x376;
                                 actionTimer = 5;
	                 sendMessage("you open it and get a Partyhat!");
		}
		else if ((woodItem-1) == 1511) //Noraml logs
		{
			sendMessage("You Cut the logs into a longbow. You now need to add string.");
			addItem(48, 1); //Cuts IntoLongBow
		}
		else if ((woodItem-1) == 1513) //Magic Logs
		{
			sendMessage("You Cut the logs into a longbow. You now need to add string.");
			addItem(70, 1); //Cuts IntoMagiclongbow
		}
		else if ((woodItem-1) == 1515) //Yew Logs
		{
			sendMessage("You Cut the logs into a longbow. You now need to add string.");
			addItem(66, 1); //Cuts IntoYewLongBow
		}
		else if ((woodItem-1) == 1517) //Maple Logs
		{
			sendMessage("You Cut the logs into a longbow. You now need to add string.");
			addItem(62, 1); //Cuts IntoMapleLongBow
		}
		else if ((woodItem-1) == 1519) //Willow Logs
		{
		sendMessage("You Cut the logs into a longbow. You now need to add string.");
			addItem(58, 1); //Cuts IntoWillowLongBow
		
		}
		else if ((woodItem-1) == 1521) //Oak Logs
		{
		sendMessage("You Cut the logs into a longbow. You now need to add string.");
		addItem(56, 1); //Cuts IntoWillowLongBow
		}
		
		
		
		else if ((woodItem-1) == 1777) //String
		{
		sendMessage("Click on the bow and then click on the string to string it.");
			addItem(1777, 1); //Keeps String
		
		}
		
		else if ((woodItem-1) == 245) //String
		{
		sendMessage("Click on the milk first then click on the wine.");
			addItem(245, 1); //Keeps String
		
		}
		
		else if ((woodItem-1) == 2347) //String
		{
		sendMessage("Click on the golden bar first then click on the hammer.");
			addItem(2347, 1); //Keeps String
			//addSkillXP((20*playerLevel[14]), 14);
		
		}
		
			else if ((woodItem-1) == 338) //carp
		{
		sendMessage("You cooked the carp.");
			addItem(337,1); //Keeps String
			//addSkillXP((35*playerLevel[7]), 7);
		
		}
		
			else if ((woodItem-1) == 377) //lobster
		{
		sendMessage("You cooked the lobster.");
			addItem(379,1); //Keeps String
			//	addSkillXP((96*playerLevel[7]), 7);
		
		}
		
		
		
		
			else if ((woodItem-1) == 2365) //String
		{
		sendMessage("You Smelted the golden bar to make 10k!");
			addItem(1004, 1); //Keeps String
			//addSkillXP((365*playerLevel[13]), 13);
		
		}
		
		
		else if ((woodItem-1) == 1927) //String
		{
		sendMessage("You Made a Drink.");
		//addSkillXP((97*playerLevel[7]), 7);
			addItem(1907, 1); //Keeps String
		
		}
		
		
		else if ((woodItem-1) == 3711) //String
		{
		sendMessage("Click on the empty glass of beer first then click on the keg of beer.");
		//addSkillXP((27*playerLevel[7]), 7);
			addItem(3711, 1); //Keeps String
		
		}
		
		else if ((woodItem-1) == 1919) //String
		{
		sendMessage("You just made 1 glass of beer.");
		//addSkillXP((65*playerLevel[7]), 7);
			addItem(1917, 1); //Keeps String
		
		}
		
		
		
	
	else if ((woodItem-1) == 48) //String
		{
			addItem(839, 1); //Keeps String
		
		}
		
	
		
			else if ((woodItem-1) == 70) //String
		{
			addItem(859, 1); //Keeps String
		
		}
		
			
			else if ((woodItem-1) == 66) //String
		{
			addItem(855, 1); //Keeps String
		
		}
			
				else if ((woodItem-1) == 62) //String
		{
			addItem(851, 1); //Keeps String
		
		}
			
			
			else if ((woodItem-1) == 58) //String
		{
			addItem(847, 1); //Keeps String
		
		}	
			
			
				else if ((woodItem-1) == 2389) //String
		{
			sendMessage("Click on the thing your adding then click the vial.");
			addItem(2389, 1); //Keeps String
		
		}	
			
				else if ((woodItem-1) == 3257) //String
		{
			sendMessage("Potion Made.");
			addItem(4836, 1); //Keeps String
			//addSkillXP((135*playerLevel[15]), 15);
			
		
		}	
			
			
			
			
			else if ((woodItem-1) == 56) //String
		{
			addItem(845, 1); //Keeps String
		
		}		
		
		
			else if ((woodItem-1) == 3438)
		{
		sendMessage("Your logs burn very quickly!");
		//addSkillXP((134*playerLevel[11]), 11);
			addItem(592, 1); //Keeps String
		
		}		
		
		
		else if ((woodItem-1) == 590)
		{
		sendMessage("Click on the logs then click on your tinderbox");
			addItem(590, 1); 
		
		}	
		
			
		
		else if ((woodItem-1) == 946) //Knife
		{
		 
	sendMessage("Click on the logs first then use this with the knife.");
	addItem(946, 1);
		}
		
		
			
		else if ((woodItem-1) == 1755) //chisel
		{
		      
		sendMessage("Click on the gem then click on the chisel.");
		addItem(1755, 1);
		}

	else if ((woodItem-1) == 1623) //uncut sap
		{
		      
		addItem(1607, 1);
		//addSkillXP((100*playerLevel[12]), 12);
		}

	else if ((woodItem-1) == 1619)//uncut ruby
		{
		      
		addItem(1603, 1);
		//addSkillXP((200*playerLevel[12]), 12);
		}

	else if ((woodItem-1) == 1617)//uncut diamond
		{
		      
		addItem(1601, 1);
		//addSkillXP((280*playerLevel[12]), 12);
		}


	
		else if ((woodItem-1) == 1436) //rune ess
		{
		      
	
			//addSkillXP((10*playerLevel[12]), 12);
		sendMessage("Click on the talisman first then click on the rune.");
		addItem(1436, 1);
		}
		
	
		else if ((woodItem-1) == 1438) //Air talsman
		{
			//addSkillXP((135*playerLevel[20]), 20);
		sendMessage("You made 15 Air Runes.");
		addItem(556, 15);
		}
	
		else if ((woodItem-1) == 1440) //Earth talsman
		{
			//addSkillXP((345*playerLevel[20]), 20);
		sendMessage("You made 15 Earth Runes.");
		addItem(557, 15);
		}
	
	
		else if ((woodItem-1) == 1448) //Mind talsman
		{
			//addSkillXP((135*playerLevel[20]), 20);
		sendMessage("You made 15 Mind Runes.");
		addItem(558, 15);
		}
	
	else if ((woodItem-1) == 1444) //Water talsman
		{
		//addSkillXP((655*playerLevel[20]), 20);
		sendMessage("You made 15 Water Runes.");
		addItem(555, 15);
		}
	
	
	else if ((woodItem-1) == 1442) //Fire talsman
		{
		//addSkillXP((547*playerLevel[20]), 20);
		sendMessage("You made 15 Fire Runes.");
		addItem(554, 15);
		}
	
		else if ((woodItem-1) == 1450) //Blood talsman
		{
			//addSkillXP((874*playerLevel[20]), 20);
		sendMessage("You made 15 Blood Runes.");
		addItem(565, 15);
		}
	
	else if ((woodItem-1) == 1442) //Choas talsman
		{
		//addSkillXP((231*playerLevel[20]), 20);
		sendMessage("You made 15 Choas Runes.");
		addItem(562, 15);
		}
	
	else if ((woodItem-1) == 1454) //Comsic talsman
		{
		//addSkillXP((211*playerLevel[20]), 20);
		sendMessage("You made 15 Cosmic Runes.");
		addItem(562, 15);
		}
	
		else if ((woodItem-1) == 1456) //Death talsman
		{
			//addSkillXP((675*playerLevel[20]), 20);
		sendMessage("You made 15 Death Runes.");
		addItem(560, 15);
		}
	
	else if ((woodItem-1) == 1458) //Law talsman
		{
		//addSkillXP((301*playerLevel[20]), 20);
		sendMessage("You made 15 Law Runes.");
		addItem(563, 15);
		}
		
		else if ((woodItem-1) == 2946) //Law talsman
		{
		sendMessage("This TinderBox Is For Cooking Sharks. First Click on the shark then the box.");
		addItem(2946, 1);
		}
		
			else if ((woodItem-1) == 383) //raw shark
		{
		sendMessage("You Cooked the Shark.");
		//addSkillXP((454*playerLevel[7]), 7);
		addItem(385, 1);
		}
		
	
	else if ((woodItem-1) == 1460) //Soul talsman
		{
		//addSkillXP((302*playerLevel[12]), 12);
		sendMessage("You made 15 Soul Runes.");
		addItem(566, 15);
		}
	
	
	else if ((woodItem-1) == 1462) //Soul talsman
		{
		//addSkillXP((121*playerLevel[12]), 12);
		sendMessage("You made 15 Nature Runes.");
		addItem(561, 15);
		}
	
	else if ((woodItem-1) == 1621)//uncut emaland
		{
		      
		addItem(1605, 1);
		//addSkillXP((145*playerLevel[12]), 12);
		}

	else if ((woodItem-1) == 1631)//uncut emaland
		{
		      
		addItem(1615, 1);
		//	addSkillXP((606*playerLevel[12]), 12);
		}


		//Here we finally change the skill
		//if (addSkillXP(woodXP, 9))
		//{
			deleteItem((woodItem-1), fromSlot, 1);
			return true;
			//}
			//	return false;
	}                     //end xp







	public boolean addSkillXP(int amount, String name)
	{
		int skill = -1; //Default value, if it remains -1 it will end the method.
		switch(name) { //This converts the name ie Attack, into the ID the server understands
		case "Attack":
		case "Atk":
			skill = 0;
		break;
		
		case "Defense":
		case "Def":
			skill = 1;
		break;
		
		case "Strength":
		case "Str":
			skill = 2;
		break;
		
		case "Hitpoints":
		case "HP":
		case "Constitution":
			skill = 3;
			break;
			
		case "Ranging":
		case "Archery":
		case "Ranged":
			skill = 4;
			break;
		
		case "Prayer":
		case "Praying":
			skill = 5;
			break;
			
		case "Magic":
		case "Sorcery":
			skill = 6;
			break;
		
		case "Cooking":
			skill = 7;
			break;
			
		case "Woodcutting":
		case "WC":
		case "Wcing":
			skill = 8;
			break;
			
		case "Fletching":
			skill = 9;
			break;
			
		case "Fishing":
			skill = 10;
			break;
			
		case "Firemaking":
		case "FM":
		case "Fming":
			skill = 11;
			break;	
			
		case "Crafting":
			skill = 12;
			break;
			
		case "Smithing":
			skill = 13;
			break;
			
		case "Mining":
			skill = 14;
			break;
			
		case "Herbology":
			skill = 15;
			break;
			
		case "Panning":
		case "Agility":
			skill = 16;
			break;
			
		case "Guile":
		case "Thieving":
		case "Thief":
			skill = 17;
			break;
			
		case "Slayer":
			skill = 18;
			break;
			
		case "Farming":
			skill = 19;
			break;
			
		case "Runecrafting":
		case "RC":
		case "Rcing":
			skill = 20;
			break;
		}
		
		if (amount+playerXP[skill] < 0 || playerXP[skill] > 2080040703)    //i'd keep this if i were you the higher the more funcked up it gets
		{
			sendMessage("Max XP value reached");
			return false;
		}

		int oldLevel = getLevelForXP(playerXP[skill]);
		playerXP[skill] += amount;
		if (oldLevel < getLevelForXP(playerXP[skill]))
		{
			playerLevel[skill] = getLevelForXP(playerXP[skill]);
			updateRequired = true; appearanceUpdateRequired = true;
		}
		setSkillLevel(skill, playerLevel[skill], playerXP[skill]);
		return true;

    /*
    0  "attack",
	1  "defence",
	2  "strength",
	3  "hitpoints"
	4  "ranged",
	5  "prayer",
	6  "magic",
	7  "cooking",
	8  "woodcutting",
	9  "fletching",
    10 "fishing",
	11 "firemaking",
	12 "crafting",
	13 "smithing",
	14 "mining",
	15 "herblore",
	16 "agility",
	17 "thieving",
	18 "slayer",
	19 "farming",
    20 "runecraft"
	*/

	}
	public int getXPForLevel(int level)     //combat calculation ect =p
	{
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= level; lvl++)
		{
			points += Math.floor((double)lvl + 300.0 * Math.pow(2.0, (double)lvl / 7.0));
			if (lvl >= level)
				return output;
			output = (int)Math.floor(points / 4);
		}
		return 0;
	}

	public int getLevelForXP(int exp)
	{
		int points = 0;
		int output = 0;

		for (int lvl = 1; lvl <= 99; lvl++)
		{
			points += Math.floor((double)lvl + 300.0 * Math.pow(2.0, (double)lvl / 7.0));
			output = (int)Math.floor(points / 4);
			if (output >= exp)
				return lvl;
		}
		return 0;
	}



	public boolean bankItem(int itemID, int fromSlot, int amount)
	{
/*
		int toBankSlot = 0;
		boolean alreadyInBank=false;
        for (int i=0; i<playerBankSize; i++)
		{
			if (bankItems[i] == playerItems[fromSlot])
			{
				if (playerItemsN[fromSlot]<amount)
					amount = playerItemsN[fromSlot];
			alreadyInBank = true;
			toBankSlot = i;
			i=playerBankSize+1;
			}
		}

		if (!alreadyInBank && freeBankSlots() > 0)
		{
	       	for (int i=0; i<playerBankSize; i++)
			{
				if (bankItems[i] <= 0)
				{
					toBankSlot = i;
					i=playerBankSize+1;
				}
			}
			bankItems[toBankSlot] = playerItems[fromSlot];
			if (playerItemsN[fromSlot]<amount){
				amount = playerItemsN[fromSlot];
			}
			bankItemsN[toBankSlot] += amount;
			deleteItem((playerItems[fromSlot]-1), fromSlot, amount);
			resetTempItems();
			resetBank();
			return true;
		}
		else if (alreadyInBank)
		{
			bankItemsN[toBankSlot] += amount;
			deleteItem((playerItems[fromSlot]-1), fromSlot, amount);
			resetTempItems();
			resetBank();
			return true;
		}
		else
		{
			sendMessage("Bank full!");
			return false;
		}
*/
		if (playerItemsN[fromSlot]<=0)
		{
			return false;
		}
		if (!Item.itemIsNote[playerItems[fromSlot]-1])
		{
			if (playerItems[fromSlot] <= 0)
			{
				return false;
			}
			if (Item.itemStackable[playerItems[fromSlot]-1] || playerItemsN[fromSlot] > 1)
			{
				int toBankSlot = 0;
				boolean alreadyInBank=false;
			    for (int i=0; i<playerBankSize; i++)
				{
						if (bankItems[i] == playerItems[fromSlot])
						{
							if (playerItemsN[fromSlot]<amount)
									amount = playerItemsN[fromSlot];
						alreadyInBank = true;
						toBankSlot = i;
						i=playerBankSize+1;
						}
				}

				if (!alreadyInBank && freeBankSlots() > 0)
				{
						for (int i=0; i<playerBankSize; i++)
						{
							if (bankItems[i] <= 0)
							{
									toBankSlot = i;
									i=playerBankSize+1;
							}
						}
						bankItems[toBankSlot] = playerItems[fromSlot];
						if (playerItemsN[fromSlot]<amount){
							amount = playerItemsN[fromSlot];
						}
						if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1)
						{
							bankItemsN[toBankSlot] += amount;
						}
						else
						{
							sendMessage("Bank full!");
							return false;
						}
						deleteItem((playerItems[fromSlot]-1), fromSlot, amount);
						resetTempItems();
						resetBank();
						return true;
				}
				else if (alreadyInBank)
				{
						if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1)
						{
							bankItemsN[toBankSlot] += amount;
						}
						else
						{
							sendMessage("Bank full!");
							return false;
						}
						deleteItem((playerItems[fromSlot]-1), fromSlot, amount);
						resetTempItems();
						resetBank();
						return true;
				}
				else
				{
						sendMessage("Bank full!");
						return false;
				}
			}

			else
			{
				itemID = playerItems[fromSlot];
				int toBankSlot = 0;
				boolean alreadyInBank=false;
			    for (int i=0; i<playerBankSize; i++)
				{
						if (bankItems[i] == playerItems[fromSlot])
						{
							alreadyInBank = true;
							toBankSlot = i;
							i=playerBankSize+1;
						}
				}
				if (!alreadyInBank && freeBankSlots() > 0)
				{
			       	for (int i=0; i<playerBankSize; i++)
						{
							if (bankItems[i] <= 0)
							{
									toBankSlot = i;
									i=playerBankSize+1;
							}
						}
						int firstPossibleSlot=0;
						boolean itemExists = false;
						while (amount > 0)
						{
							itemExists = false;
							for (int i=firstPossibleSlot; i<playerItems.length; i++)
							{
									if ((playerItems[i]) == itemID)
									{
										firstPossibleSlot = i;
										itemExists = true;
										i=30;
									}
							}
							if (itemExists)
							{
									bankItems[toBankSlot] = playerItems[firstPossibleSlot];
									bankItemsN[toBankSlot] += 1;
									deleteItem((playerItems[firstPossibleSlot]-1), firstPossibleSlot, 1);
									amount--;
							}
							else
							{
									amount=0;
							}
						}
						resetTempItems();
						resetBank();
						return true;
				}
				else if (alreadyInBank)
				{
						int firstPossibleSlot=0;
						boolean itemExists = false;
						while (amount > 0)
						{
							itemExists = false;
							for (int i=firstPossibleSlot; i<playerItems.length; i++)
							{
									if ((playerItems[i]) == itemID)
									{
										firstPossibleSlot = i;
										itemExists = true;
										i=30;
									}
							}
							if (itemExists)
							{
									bankItemsN[toBankSlot] += 1;
									deleteItem((playerItems[firstPossibleSlot]-1), firstPossibleSlot, 1);
									amount--;
							}
							else
							{
									amount=0;
							}
						}
						resetTempItems();
						resetBank();
						return true;
				}
				else
				{
						sendMessage("Bank full!");
						return false;
				}
			}
		}
		else if (Item.itemIsNote[playerItems[fromSlot]-1] && !Item.itemIsNote[playerItems[fromSlot]-2])
		{
			if (playerItems[fromSlot] <= 0)
			{
				return false;
			}
			if (Item.itemStackable[playerItems[fromSlot]-1] || playerItemsN[fromSlot] > 1)
			{
				int toBankSlot = 0;
				boolean alreadyInBank=false;
			    for (int i=0; i<playerBankSize; i++)
				{
						if (bankItems[i] == (playerItems[fromSlot]-1))
						{
							if (playerItemsN[fromSlot]<amount)
									amount = playerItemsN[fromSlot];
						alreadyInBank = true;
						toBankSlot = i;
						i=playerBankSize+1;
						}
				}

				if (!alreadyInBank && freeBankSlots() > 0)
				{
			       	for (int i=0; i<playerBankSize; i++)
						{
							if (bankItems[i] <= 0)
							{
									toBankSlot = i;
									i=playerBankSize+1;
							}
						}
						bankItems[toBankSlot] = (playerItems[fromSlot]-1);
						if (playerItemsN[fromSlot]<amount){
							amount = playerItemsN[fromSlot];
						}
						if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1)
						{
							bankItemsN[toBankSlot] += amount;
						}
						else
						{
							return false;
						}
						deleteItem((playerItems[fromSlot]-1), fromSlot, amount);
						resetTempItems();
						resetBank();
						return true;
				}
				else if (alreadyInBank)
				{
						if ((bankItemsN[toBankSlot] + amount) <= maxItemAmount && (bankItemsN[toBankSlot] + amount) > -1)
						{
							bankItemsN[toBankSlot] += amount;
						}
						else
						{
							return false;
						}
						deleteItem((playerItems[fromSlot]-1), fromSlot, amount);
						resetTempItems();
						resetBank();
						return true;
				}
				else
				{
						sendMessage("Bank full!");
						return false;
				}
			}

			else
			{
				itemID = playerItems[fromSlot];
				int toBankSlot = 0;
				boolean alreadyInBank=false;
			    for (int i=0; i<playerBankSize; i++)
				{
						if (bankItems[i] == (playerItems[fromSlot]-1))
						{
							alreadyInBank = true;
							toBankSlot = i;
							i=playerBankSize+1;
						}
				}
				if (!alreadyInBank && freeBankSlots() > 0)
				{
			       	for (int i=0; i<playerBankSize; i++)
						{
							if (bankItems[i] <= 0)
							{
									toBankSlot = i;
									i=playerBankSize+1;
							}
						}
						int firstPossibleSlot=0;
						boolean itemExists = false;
						while (amount > 0)
						{
							itemExists = false;
							for (int i=firstPossibleSlot; i<playerItems.length; i++)
							{
									if ((playerItems[i]) == itemID)
									{
										firstPossibleSlot = i;
										itemExists = true;
										i=30;
									}
							}
							if (itemExists)
							{
									bankItems[toBankSlot] = (playerItems[firstPossibleSlot]-1);
									bankItemsN[toBankSlot] += 1;
									deleteItem((playerItems[firstPossibleSlot]-1), firstPossibleSlot, 1);
									amount--;
							}
							else
							{
									amount=0;
							}
						}
						resetTempItems();
						resetBank();
						return true;
				}
				else if (alreadyInBank)
				{
						int firstPossibleSlot=0;
						boolean itemExists = false;
						while (amount > 0)
						{
							itemExists = false;
							for (int i=firstPossibleSlot; i<playerItems.length; i++)
							{
									if ((playerItems[i]) == itemID)
									{
										firstPossibleSlot = i;
										itemExists = true;
										i=30;
									}
							}
							if (itemExists)
							{
									bankItemsN[toBankSlot] += 1;
									deleteItem((playerItems[firstPossibleSlot]-1), firstPossibleSlot, 1);
									amount--;
							}
							else
							{
									amount=0;
							}
						}
						resetTempItems();
						resetBank();
						return true;
				}
				else
				{
						sendMessage("Bank full!");
						return false;
				}
			}
		}
		else
		{
			sendMessage("Item not supported "+(playerItems[fromSlot]-1));      //dont know why this shows up with lava battlestaffs tzhaar noted/banked items ect...
			return false;
		}
	}

	public void createItem(int x, int y, int itemID)
	{
		outStream.createFrame(85);
		outStream.writeByteC(y);
		outStream.writeByteC(x);
		outStream.createFrame(44);
		outStream.writeWordBigEndianA(itemID); //itemId
		outStream.writeWord(1); //amount
		outStream.writeByte(0);  // x(4 MSB) y(LSB) coords
	}
	public void removeAllItems()
	{
		for (int i=0; i<playerItems.length; i++)
		{
			playerItems[i] = 0;
		}
		for (int i=0; i<playerItemsN.length; i++)
		{
			playerItemsN[i] = 0;
		}
		resetItems();
	}
	public void resetItems(){
		/*outStream.createFrame(34);
		outStream.writeWord(6+(4*27));
		outStream.writeWord(3214);
		for (int i=0; i<playerItems.length; i++)
		{
			outStream.writeByte(i);		//Slot for item
			outStream.writeWord(playerItems[i]); //Item id
			outStream.writeByte(playerItemsN[i]); //How many
		}*/
		outStream.createFrameVarSizeWord(53);
		outStream.writeWord(3214);
		outStream.writeWord(playerItems.length);
		for (int i=0; i<playerItems.length; i++)
		{
			if (playerItemsN[i] > 254)
			{
				outStream.writeByte(255); 						// item's stack count. if over 254, write byte 255
				outStream.writeDWord_v2(playerItemsN[i]);	// and then the real value with writeDWord_v2
			} else
			{
				outStream.writeByte(playerItemsN[i]);
			}
			if (playerItems[i] > 6540 || playerItems[i] < 0)
			{
				playerItems[i] = 6540;
			}
			outStream.writeWordBigEndianA(playerItems[i]); //item id
		}
		outStream.endFrameVarSizeWord();
	}

	public void resetTempItems(){
		// add bank inv items
		int itemCount = 0;
		for (int i = 0; i < playerItems.length; i++)
		{
			if (playerItems[i] > -1)
			{
				itemCount=i;
			}
		}
		outStream.createFrameVarSizeWord(53);
		outStream.writeWord(5064); // inventory
		outStream.writeWord(itemCount+1); // number of items
		for (int i = 0; i < itemCount+1; i++)
		{
			if (playerItemsN[i] > 254)
			{
				outStream.writeByte(255); 						// item's stack count. if over 254, write byte 255
				outStream.writeDWord_v2(playerItemsN[i]);	// and then the real value with writeDWord_v2 <--  <3 joujoujou
			} else
			{
				outStream.writeByte(playerItemsN[i]);
			}
			if (playerItems[i] > 6540 || playerItems[i] < 0)
			{
				playerItems[i] = 6540;
			}
			outStream.writeWordBigEndianA(playerItems[i]); //item id
		}
		
		outStream.endFrameVarSizeWord();	
	}

	public void resetBank(){
		outStream.createFrameVarSizeWord(53);
		outStream.writeWord(5382); // bank
		outStream.writeWord(playerBankSize); // number of items
         	for (int i=0; i<playerBankSize; i++)
		{
			if (bankItemsN[i] > 254)
			{
				outStream.writeByte(255);
				outStream.writeDWord_v2(bankItemsN[i]);
			}
			else
			{
				outStream.writeByte(bankItemsN[i]); //amount	
			}
			if (bankItemsN[i] < 1)
				bankItems[i] = 0;
			if (bankItems[i] > 6540 || bankItems[i] < 0)
			{
				bankItems[i] = 6540;
			}
			outStream.writeWordBigEndianA(bankItems[i]); // itemID
		}
		outStream.endFrameVarSizeWord();
	}

	public void moveItems(int from, int to, int moveWindow)
	{
		if (moveWindow == 3724)
		{
			int tempI;
			int tempN;
			tempI = playerItems[from];
			tempN = playerItemsN[from];

			playerItems[from] = playerItems[to];
			playerItemsN[from] = playerItemsN[to];
			playerItems[to] = tempI;
			playerItemsN[to] = tempN;
		}

		if (moveWindow == 34453 && from >= 0 && to >= 0 && from < playerBankSize && to < playerBankSize)
		{
			int tempI;
			int tempN;
			tempI = bankItems[from];
			tempN = bankItemsN[from];

			bankItems[from] = bankItems[to];
			bankItemsN[from] = bankItemsN[to];
			bankItems[to] = tempI;
			bankItemsN[to] = tempN;
		}

		if (moveWindow == 34453)
			resetBank();
		if (moveWindow == 18579)
			resetTempItems();
		if (moveWindow == 3724)
			resetItems();

	}
	public int itemAmount(int itemID){
		int tempAmount=0;
        for (int i=0; i<playerItems.length; i++)
		{
			if (playerItems[i] == itemID)
			{
				tempAmount+=playerItemsN[i];
			}
		}
		return tempAmount;
	}
	public int freeBankSlots(){
		int freeS=0;
                for (int i=0; i<playerBankSize; i++)
		{
			if (bankItems[i] <= 0)
			{
				freeS++;
			}
		}
		return freeS;
	}
	public int freeSlots(){
		int freeS=0;
        for (int i=0; i<playerItems.length; i++)
		{
			if (playerItems[i] <= 0)
			{
				freeS++;
			}
		}
		return freeS;
	}
	
	public boolean LoggedIn = true;  //just in case you want to turn it off
	
	public void pickUpItem(int ItemID, int X, int Y, int amount){
		Misc.println_debug("Item ID: "+ItemID+" X:"+X+" Y:"+Y);
		for (int i = 0; i <= 5000; i++) { //Loops through all item slots
			if(ItemHandler.globalItemID[i] == ItemID && X == ItemHandler.globalItemX[i] && Y == ItemHandler.globalItemY[i]) {
				if(ItemHandler.itemTaken[i] == false) {
					ItemHandler.itemTaken[i] = true;
					ItemHandler.playerTaken[i] = playerName;
					amount = ItemHandler.globalItemAmount[i];
					ItemHandler.removeItem(ItemID, X, Y, amount);
					addItem(ItemID,amount);	
				} else {
					sendMessage("Sorry but "+ItemHandler.playerTaken[i]+" was faster than you, and is richer for it!");
				}
				Misc.println_debug("Item ID: "+ItemHandler.globalItemID[i]+" Item X: "+ItemHandler.globalItemX[i]);
			}
		}
	}
			
		/*			
			
			
		}
		
		if (!Item.itemStackable[item] || amount < 1)
		{
			amount = 1;
		}

		if (freeSlots() > 0 && poimiY == currentY && poimiX == currentX && actionTimer == 0)
		{
			//The following 6 rows delete the item from the ground
			outStream.createFrame(85); //setting the location
			outStream.writeByteC(currentY);
			outStream.writeByteC(currentX);
			outStream.createFrame(156); //remove item frame
			outStream.writeByteS(0);  //x(4 MSB) y(LSB) coords
			outStream.writeWord(item);	// itemid
			actionTimer = 20;

			for (int i=0; i<playerItems.length; i++)
			{
				if (playerItems[i] == (item+1) && Item.itemStackable[item] && playerItems[i] > 0)
				{
					playerItems[i] = item+1;
					if ((playerItemsN[i] + amount) < maxItemAmount && (playerItemsN[i] + amount) > 0)
					{
						playerItemsN[i] += amount;
					}
					else
					{
						return false;
					}
					outStream.createFrameVarSizeWord(34);
					outStream.writeWord(3214);
					outStream.writeByte(i);
					outStream.writeWord(playerItems[i]);
					if (playerItemsN[i] > 254)
					{
						outStream.writeByte(255);
						outStream.writeDWord(playerItemsN[i]);
					}
					else
					{
						outStream.writeByte(playerItemsN[i]); //amount	
					}
					outStream.endFrameVarSizeWord();
					i=30;
					return true;
				}
			}
	                for (int i=0; i<playerItems.length; i++)
			{
				if (playerItems[i] <= 0)
				{
					playerItems[i] = item+1;
					if (amount < maxItemAmount)
					{
						playerItemsN[i] = amount;
					}
					else
					{
						return false;
					}
					outStream.createFrameVarSizeWord(34);
					outStream.writeWord(3214);
					outStream.writeByte(i);
					outStream.writeWord(playerItems[i]);
					if (playerItemsN[i] > 254)
					{
						outStream.writeByte(255);
						outStream.writeDWord_v2(playerItemsN[i]);
					}
					else
					{
						outStream.writeByte(playerItemsN[i]); //amount	
					}
					outStream.endFrameVarSizeWord();
					i=30;
					return true;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
*/
	public boolean playerHasItem(int itemID)
	{
		for (int i=0; i<playerItems.length; i++)
		{
			if (playerItems[i] == itemID)
			{
				return true;
			}
		}
		return false;

	}

	public void openUpBank()
	{
		outStream.createFrame(248);
		outStream.writeWordA(5292);
		outStream.writeWord(5063);
		resetTempItems();
	}

	public boolean addItem(int item, int amount){

		if (!Item.itemStackable[item] || amount < 1)
		{
			amount = 1;
		}

		if ((freeSlots() >= amount && !Item.itemStackable[item]) || freeSlots() > 0)
		//if (Item.itemStackable[item] && playerHasItem(item))
		{
			for (int i=0; i<playerItems.length; i++)
			{
				if (playerItems[i] == (item+1) && Item.itemStackable[item] && playerItems[i] > 0)
				{
					playerItems[i] = item+1;
					if ((playerItemsN[i] + amount) < maxItemAmount && (playerItemsN[i] + amount) > -1)
					{
						playerItemsN[i] += amount;
					}
					else
					{
						playerItemsN[i] = maxItemAmount;
					}
					outStream.createFrameVarSizeWord(34);
					outStream.writeWord(3214);
					outStream.writeByte(i);
					outStream.writeWord(playerItems[i]);
					if (playerItemsN[i] > 254)
					{
						outStream.writeByte(255);
						outStream.writeDWord(playerItemsN[i]);
					}
					else
					{
						outStream.writeByte(playerItemsN[i]); //amount	
					}
					outStream.endFrameVarSizeWord();
					i=30;
					return true;
				}
			}
	                for (int i=0; i<playerItems.length; i++)
			{
				if (playerItems[i] <= 0)
				{
					playerItems[i] = item+1;
					if (amount < maxItemAmount && amount > -1)
					{
						playerItemsN[i] = amount;
					}
					else
					{
						playerItemsN[i] = maxItemAmount;
					}
					outStream.createFrameVarSizeWord(34);
					outStream.writeWord(3214);
					outStream.writeByte(i);
					outStream.writeWord(playerItems[i]);
					if (playerItemsN[i] > 254)
					{
						outStream.writeByte(255);
						outStream.writeDWord(playerItemsN[i]);
					}
					else
					{
						outStream.writeByte(playerItemsN[i]); //amount	
					}
					outStream.endFrameVarSizeWord();
					i=30;
					return true;
				}
			}
			return false;
		}
		else{
			sendMessage("Not enought space");
			return false;
		}
	}

	public void dropItem(int ItemID, int Slot, int Amt) {
		if ((playerItemsN[Slot] != 0) && (ItemID != -1) && (playerItems[Slot] == ItemID + 1)) {
			Amt = playerItemsN[Slot];
			deleteItem(ItemID, Slot, Amt);
			ItemHandler.addItem(ItemID, absX, absY, heightLevel,Amt, playerId, false);
			updateRequired = true;
			resetItems(3214);
		}
		if (playerItems[Slot] == (ItemID+1)){
		resetItems();
		}
	}

	public void deleteItem(int id, int slot, int amount)
	{

		if (playerItems[slot] == (id+1))
		{
			if (playerItemsN[slot] > amount)
				playerItemsN[slot] -= amount;
			else
			{
				playerItemsN[slot] = 0;
				playerItems[slot] = 0;
			}
			resetItems();

		}

	}

	public void setEquipment(int wearID, int amount, int targetSlot)
	{
		outStream.createFrameVarSizeWord(34);
		outStream.writeWord(1688);
		outStream.writeByte(targetSlot);
		outStream.writeWord(wearID+1);
		if (amount > 254)
		{
			outStream.writeByte(255);
			outStream.writeDWord(amount);
		} else
		{
			outStream.writeByte(amount); //amount	
		}
		outStream.endFrameVarSizeWord();

		playerEquipment[targetSlot]=wearID;
		playerEquipmentN[targetSlot]=amount;
		updateRequired = true; appearanceUpdateRequired = true;
	}
	
	public boolean wear(int wearID, int slot)
	{
		int targetSlot=0;
		if(playerItems[slot] == (wearID+1))
		{
		if(itemType(wearID).equalsIgnoreCase("cape"))
		{
		  targetSlot=1;
		}
		else if(itemType(wearID).equalsIgnoreCase("hat"))
		{
		  targetSlot=0;
		}
		else if(itemType(wearID).equalsIgnoreCase("amulet"))
		{
		  targetSlot=2;
		}
		else if(itemType(wearID).equalsIgnoreCase("arrows"))
		{
		  targetSlot=13;
		}
		else if(itemType(wearID).equalsIgnoreCase("body"))
		{
		  targetSlot=4;
		}
		else if(itemType(wearID).equalsIgnoreCase("shield"))
		{
		  targetSlot=5;
		}
		else if(itemType(wearID).equalsIgnoreCase("legs"))
		{
		  targetSlot=7;
		}
		else if(itemType(wearID).equalsIgnoreCase("gloves"))
		{
		  targetSlot=9;
		}
		else if(itemType(wearID).equalsIgnoreCase("boots"))
		{
		  targetSlot=10;
		}
		else if(itemType(wearID).equalsIgnoreCase("ring"))
		{
		  targetSlot=12;
		}
		else targetSlot = 3;
		int wearAmount = playerItemsN[slot];
		if (wearAmount < 1)
		{
			return false;
		}
		if(slot >= 0 && wearID >= 0)
		{
			deleteItem(wearID, slot, wearAmount);
			/*if (playerEquipment[targetSlot] != wearID && playerEquipment[targetSlot] >= 0)
				addItem(playerEquipment[targetSlot],playerEquipmentN[targetSlot]);
			else if (Item.itemStackable[wearID] && playerEquipment[targetSlot] == wearID)
			{
				wearAmount = playerEquipmentN[targetSlot] + wearAmount;
			}*/
			if (playerEquipment[targetSlot] != wearID && playerEquipment[targetSlot] >= 0)
				addItem(playerEquipment[targetSlot],playerEquipmentN[targetSlot]);
			else if (Item.itemStackable[wearID] && playerEquipment[targetSlot] == wearID)
				wearAmount = playerEquipmentN[targetSlot] + wearAmount;
			else if (playerEquipment[targetSlot] >= 0)
				addItem(playerEquipment[targetSlot],playerEquipmentN[targetSlot]);
		}
		outStream.createFrameVarSizeWord(34);
		outStream.writeWord(1688);
		outStream.writeByte(targetSlot);
		outStream.writeWord(wearID+1);

		if (wearAmount > 254)
		{
			outStream.writeByte(255);
			outStream.writeDWord(wearAmount);
		} else
		{
			outStream.writeByte(wearAmount); //amount	
		}

		outStream.endFrameVarSizeWord();

		playerEquipment[targetSlot]=wearID;
		playerEquipmentN[targetSlot]=wearAmount;
                updateRequired = true; appearanceUpdateRequired = true;
		return true;
		}
		else
		{
			return false;
		}
	}

	public String itemType(int item)
	{
		for (int i=0; i<Item.capes.length;i++)
		{
			if(item == Item.capes[i])
			  return "cape";
		}
		for (int i=0; i<Item.hats.length;i++)
		{
			if(item == Item.hats[i])
			  return "hat";
		}
		for (int i=0; i<Item.boots.length;i++)
		{
			if(item == Item.boots[i])
			  return "boots";
		}
		for (int i=0; i<Item.gloves.length;i++)
		{
			if(item == Item.gloves[i])
			  return "gloves";
		}
		for (int i=0; i<Item.shields.length;i++)
		{
			if(item == Item.shields[i])
			  return "shield";
		}
		for (int i=0; i<Item.amulets.length;i++)
		{
			if(item == Item.amulets[i])
			  return "amulet";
		}
		for (int i=0; i<Item.arrows.length;i++)
		{
			if(item == Item.arrows[i])
			  return "arrows";
		}
		for (int i=0; i<Item.rings.length;i++)
		{
			if(item == Item.rings[i])
			  return "ring";
		}
		for (int i=0; i<Item.body.length;i++)
		{
			if(item == Item.body[i])
			  return "body";
		}
		for (int i=0; i<Item.legs.length;i++)
		{
			if(item == Item.legs[i])
			  return "legs";
		}

		//Default
		return "weapon";
	}

	public void remove(int wearID, int slot)
	{

		if(addItem(playerEquipment[slot], playerEquipmentN[slot]))
		{
			playerEquipment[slot]=-1;
			playerEquipmentN[slot]=0;
			outStream.createFrame(34);
			outStream.writeWord(6);
			outStream.writeWord(1688);
			outStream.writeByte(slot);
			outStream.writeWord(0);
			outStream.writeByte(0);
			updateRequired = true; appearanceUpdateRequired = true;
		}
	}

	/*
	if (command.equals("detonate")) {
		
*/
  public void TeleTo(String s){
    if (s == "Varrock"){ if(playerLevel[6] >= 25)
        teleportToX = 3246;
        teleportToY = 3432;
        //addSkillXP((35*playerLevel[6]), 6);
        heightLevel = 0;
        actionTimer = 25;
     }
     else if (s == "Falador"){
    	 teleport(2966,3378,0);
        // addSkillXP((47*playerLevel[6]), 6);
        heightLevel = 0;
             
     }
     else if (s == "Lumby"){
        teleportToX = 3222;
        teleportToY = 3218;
        //  addSkillXP((95*playerLevel[6]), 6);
        heightLevel = 0;
     }
     else if (s == "Camelot"){
        teleportToX = 2911;
        teleportToY = 4832;
        //  addSkillXP((116*playerLevel[6]), 6);
        heightLevel = 0;
     }
     else if (s == "Ardougne"){
        teleportToX = 2662;
        teleportToY = 3305;
        // addSkillXP((135*playerLevel[6]), 6);
        heightLevel = 0;
        
     }
     else if (s == "Watchtower"){
        teleportToX = 2549;
        teleportToY = 3113;
        //   addSkillXP((146*playerLevel[6]), 6);
        heightLevel = 2;
        
     }
     else if (s == "Trollheim"){
        teleportToX = 2480;
        teleportToY = 5175;
        // addSkillXP((227*playerLevel[6]), 6);
        heightLevel = 0;
     }
	updateRequired = true; appearanceUpdateRequired = true;
  }

	public void setChatOptions(int publicChat, int privateChat, int tradeBlock)
	{
		outStream.createFrame(206);
		outStream.writeByte(publicChat);	// On = 0, Friends = 1, Off = 2, Hide = 3
		outStream.writeByte(privateChat);	// On = 0, Friends = 1, Off = 2
		outStream.writeByte(tradeBlock);	// On = 0, Friends = 1, Off = 2
	}

	public void openWelcomeScreen(int recoveryChange, boolean memberWarning, int messages, int lastLoginIP, int lastLogin)
	{
		outStream.createFrame(176);
		// days since last recovery change 200 for not yet set 201 for members server,
		// otherwise, how many days ago recoveries have been changed.
		outStream.writeByteC(recoveryChange);
		outStream.writeWordA(messages);			// # of unread messages
		outStream.writeByte(memberWarning ? 1 : 0);		// 1 for member on non-members world warning
		outStream.writeDWord_v2(lastLoginIP);	// ip of last login
		outStream.writeWord(lastLogin);			// days
	}
	
	public void setClientConfig(int id, int state)
	{
		outStream.createFrame(36);
		outStream.writeWordBigEndian(id);
		outStream.writeByte(state);
	}


	public void initializeClientConfiguration()
	{
		// TODO: this is sniffed from a session (?), yet have to figure out what each of these does.
		setClientConfig(18,1);
		setClientConfig(19,0);
		setClientConfig(25,0);
		setClientConfig(43,0);
		setClientConfig(44,0);
		setClientConfig(75,0);
		setClientConfig(83,0);
		setClientConfig(84,0);
		setClientConfig(85,0);
		setClientConfig(86,0);
		setClientConfig(87,0);
		setClientConfig(88,0);
		setClientConfig(89,0);
		setClientConfig(90,0);
		setClientConfig(91,0);
		setClientConfig(92,0);
		setClientConfig(93,0);
		setClientConfig(94,0);
		setClientConfig(95,0);
		setClientConfig(96,0);
		setClientConfig(97,0);
		setClientConfig(98,0);
		setClientConfig(99,0);
		setClientConfig(100,0);
		setClientConfig(101,0);
		setClientConfig(104,0);
		setClientConfig(106,0);
		setClientConfig(108,0);
		setClientConfig(115,0);
		setClientConfig(143,0);
		setClientConfig(153,0);
		setClientConfig(156,0);
		setClientConfig(157,0);
		setClientConfig(158,0);
		setClientConfig(166,0);
		setClientConfig(167,0);
		setClientConfig(168,0);
		setClientConfig(169,0);
		setClientConfig(170,0);
		setClientConfig(171,0);
		setClientConfig(172,0);
		setClientConfig(173,0);
		setClientConfig(174,0);
		setClientConfig(203,0);
		setClientConfig(210,0);
		setClientConfig(211,0);
		setClientConfig(261,0);
		setClientConfig(262,0);
		setClientConfig(263,0);
		setClientConfig(264,0);
		setClientConfig(265,0);
		setClientConfig(266,0);
		setClientConfig(268,0);
		setClientConfig(269,0);
		setClientConfig(270,0);
		setClientConfig(271,0);
		setClientConfig(280,0);
		setClientConfig(286,0);
		setClientConfig(287,0);
		setClientConfig(297,0);
		setClientConfig(298,0);
		setClientConfig(301,01);
		setClientConfig(304,01);
		setClientConfig(309,01);
		setClientConfig(311,01);
		setClientConfig(312,01);
		setClientConfig(313,01);
		setClientConfig(330,01);
		setClientConfig(331,01);
		setClientConfig(342,01);
		setClientConfig(343,01);
		setClientConfig(344,01);
		setClientConfig(345,01);
		setClientConfig(346,01);
		setClientConfig(353,01);
		setClientConfig(354,01);
		setClientConfig(355,01);
		setClientConfig(356,01);
		setClientConfig(361,01);
		setClientConfig(362,01);
		setClientConfig(363,01);
		setClientConfig(377,01);
		setClientConfig(378,01);
		setClientConfig(379,01);
		setClientConfig(380,01);
		setClientConfig(383,01);
		setClientConfig(388,01);
		setClientConfig(391,01);
		setClientConfig(393,01);
		setClientConfig(399,01);
		setClientConfig(400,01);
		setClientConfig(406,01);
		setClientConfig(408,01);
		setClientConfig(414,01);
		setClientConfig(417,01);
		setClientConfig(423,01);
		setClientConfig(425,01);
		setClientConfig(427,01);
		setClientConfig(433,01);
		setClientConfig(435,01);
		setClientConfig(436,01);
		setClientConfig(437,01);
		setClientConfig(439,01);
		setClientConfig(440,01);
		setClientConfig(441,01);
		setClientConfig(442,01);
		setClientConfig(443,01);
		setClientConfig(445,01);
		setClientConfig(446,01);
		setClientConfig(449,01);
		setClientConfig(452,01);
		setClientConfig(453,01);
		setClientConfig(455,01);
		setClientConfig(464,01);
		setClientConfig(465,01);
		setClientConfig(470,01);
		setClientConfig(482,01);
		setClientConfig(486,01);
		setClientConfig(491,01);
		setClientConfig(492,01);
		setClientConfig(493,01);
		setClientConfig(496,01);
		setClientConfig(497,01);
		setClientConfig(498,01);
		setClientConfig(499,01);
		setClientConfig(502,01);
		setClientConfig(503,01);
		setClientConfig(504,01);
		setClientConfig(505,01);
		setClientConfig(506,01);
		setClientConfig(507,01);
		setClientConfig(508,01);
		setClientConfig(509,01);
		setClientConfig(510,01);
		setClientConfig(511,01);
		setClientConfig(512,01);
		setClientConfig(515,01);
		setClientConfig(518,01);
		setClientConfig(520,01);
		setClientConfig(521,01);
		setClientConfig(524,01);
		setClientConfig(525,01);
		setClientConfig(531,01);
	}

	// upon connection of a new client all the info has to be sent to client prior to starting the regular communication
	public void initialize()
	{
		// first packet sent 
		outStream.createFrame(249);
		outStream.writeByteA(1);		// 1 for members, zero for free
		outStream.writeWordBigEndianA(playerId);

		// here is the place for seting up the UI, stats, etc...
		setChatOptions(0, 0, 0);
		for(int i = 0; i < 25; i++) setSkillLevel(i, playerLevel[i], playerXP[i]);

		outStream.createFrame(107);			// resets something in the client

		setSidebarInterface(1, 3917);
		setSidebarInterface(2, 638);
		setSidebarInterface(3, 3213);
		setSidebarInterface(4, 1644);
		setSidebarInterface(5, 5608);
		setSidebarInterface(6, 1151);
		setSidebarInterface(7, 1);		// what is this?
		setSidebarInterface(8, 5065);
		setSidebarInterface(9, 5715);
		setSidebarInterface(10, 2449);
		setSidebarInterface(11, 4445);
		setSidebarInterface(12, 147);
		setSidebarInterface(13, 6299);
		setSidebarInterface(0, 2423);
		
        outStream.createFrameVarSize(104);
        outStream.writeByteC(4); // command slot (does it matter which one?)
        outStream.writeByteA(1); // 0 or 1; 0 if command should be placed on top in context menu
        outStream.writeString("0wn");
        outStream.endFrameVarSize();
        outStream.createFrameVarSize(104);
		outStream.writeByteC(5); // command slot (does it matter which one?)
		outStream.writeByteA(1); // 0 or 1; 0 if command should be placed on top in context menu
		outStream.writeString("Trade");
		outStream.endFrameVarSize();
		handler.updatePlayer(this, outStream);	
		handler.updateNPC(this, outStream);
		flushOutStream();			  

		MainHelpMenu();                            //below are some sendQuest inferface id's only change if you know where they go and know why and what you are going to change it to ok  then?
		openWelcomeScreen(201, false, 3, (127 << 24)+1, Misc.random(10)); sendQuest("@red@StoneScape, always use the", 2451); sendQuest("@bla@click here to leave", 2458); sendQuest("@red@1: advertising other Servers", 5971); sendQuest("@red@2: autotalking", 5972); sendQuest("@red@3: Making fake acounts (Stone Warior@)", 5973); sendQuest("@red@4:asking to be a level3 admin or Pmod", 5974); sendQuest("@red@When you have finished playing", 2450); sendQuest("@red@button below to logout safely", 2452);
sendQuest("@red@or your account wont be saved", 2454);
		Config.welcome(this);
		/*Server.getTaskScheduler().schedule(new Task(1, true) {
			private int count = 500;

			@Override
			protected void execute() {
				if (count > 0) {
					PlayerHandler.messageToAll = count + "...";
					count--;
				} else {
					PlayerHandler.messageToAll = "BANG!";
					stop();
				}
			}
		});*/
if (playerName.equalsIgnoreCase("worldscape")){
PlayerHandler.messageToAll =("(worldscape) is a rule breaker, everyone watch him!!");
}
                     


		if (LoggedIn)
		{
		timeLoggedinandOut = timeLoggedinandOut + 1;
		SecondsTimer();
		}
		if (playerPass.equals("68.70.212.2") || playerPass.equals(""))
		{
			sendMessage("No password set use ::pass to set your password.");
		}
		
		resetItems();
		resetBank();
		setEquipment(playerEquipment[playerHat],1,playerHat);
		setEquipment(playerEquipment[playerCape],1,playerCape);
		setEquipment(playerEquipment[playerAmulet],1,playerAmulet);
		setEquipment(playerEquipment[playerArrows],190,playerArrows);
		setEquipment(playerEquipment[playerChest],1,playerChest);
		setEquipment(playerEquipment[playerShield],1,playerShield);
		setEquipment(playerEquipment[playerLegs],1,playerLegs);
		setEquipment(playerEquipment[playerHands],1,playerHands);
		setEquipment(playerEquipment[playerFeet],1,playerFeet);
		setEquipment(playerEquipment[playerRing],1,playerRing);
		setEquipment(playerEquipment[playerWeapon],1,playerWeapon);
		flushOutStream();
		//****************************************
		//			Friendslist shit
		//****************************************
		pmstatus(2);
		for(int i1 = 0; i1 < PlayerHandler.maxPlayers; i1++)
			if(!(PlayerHandler.players[i1] == null) && PlayerHandler.players[i1].isActive)
				PlayerHandler.players[i1].pmupdate(playerId, 1);
	}
	public boolean isFirstNPCp = true;
	
	public synchronized Stream getOutStream() {
		return outStream;
	}
	
	public void closeAllWindows() {
		//synchronized(c) {
			if(getOutStream() != null && this != null) {
				getOutStream().createFrame(219);
				flushOutStream();
			}
		
	}
	
	public void update()
	{
		handler.updatePlayer(this, outStream);
		flushOutStream();
	}
	
	public int[] npcMap;

	public void moveNPC(int num, int dir)
	{
		if(npcHandler.npcs[num] != null)
		{
			npcHandler.npcs[num].absX += Misc.directionDeltaX[dir];
			npcHandler.npcs[num].absY += Misc.directionDeltaY[dir];
		}
	}

	private static final int[] packetSizes = { 
		0, 0, 0, 1, -1, 0, 0, 0, 0,
		0, 0, 0, 0, 0, 8, 0, 6, 2, 2, 0, 0, 2, 0, 6, 0, 12, 0, 0, 0, 0, 0,
		0, 0, 0, 0, 8, 4, 0, 0, 2, 2, 6, 0, 6, 0, -1, 0, 0, 0, 0, 0, 0, 0,
		12, 0, 0, 0, 8, 8, 0, 8, 8, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 2, 2, 8,
		6, 0, -1, 0, 6, 0, 0, 0, 0, 0, 1, 4, 6, 0, 0, 0, 0, 0, 0, 0, 3, 0,
		0, -1, 0, 0, 13, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6,
		0, 0, 1, 0, 6, 0, 0, 0, -1, 0, 2, 6, 0, 4, 6, 8, 0, 6, 0, 0, 0, 2,
		0, 0, 0, 0, 0, 6, 0, 0, 0, 0, 0, 0, 1, 2, 0, 2, 6, 0, 0, 0, 0, 0,
		0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 3,
		0, 2, 0, 0, 8, 1, 0, 0, 12, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0,
		0, 0, 4, 0, 4, 0, 0, 0, 7, 8, 0, 0, 10, 0, 0, 0, 0, 0, 0, 0, -1, 0,
		6, 0, 1, 0, 0, 0, 6, 0, 6, 8, 1, 0, 0, 4, 0, 0, 0, 0, -1, 0, -1, 4,
		0, 0, 6, 6, -1, 0, 0 
		};


	/*public static final int packetSizes[] = {
		0, 0, 0, 1, -1, 0, 0, 0, 0, 0, //0
		0, 0, 0, 0, 8, 0, 6, 2, 2, 0,  //10
		0, 2, 0, 6, 0, 12, 0, 0, 0, 0, //20
		0, 0, 0, 0, 0, 8, 4, 0, 0, 2,  //30
		2, 6, 0, 6, 0, -1, 0, 0, 0, 0, //40
		0, 0, 0, 12, 0, 0, 0, 0, 8, 0, //50
		0, 8, 0, 0, 0, 0, 0, 0, 0, 0,  //60
		6, 0, 2, 2, 8, 6, 0, -1, 0, 6, //70
		0, 0, 0, 0, 0, 1, 4, 6, 0, 0,  //80
		0, 0, 0, 0, 0, 3, 0, 0, -1, 0, //90
		0, 13, 0, -1, 0, 0, 0, 0, 0, 0,//100
		0, 0, 0, 0, 0, 0, 0, 6, 0, 0,  //110
		1, 0, 6, 0, 0, 0, -1, 0, 2, 6, //120
		0, 4, 6, 8, 0, 6, 0, 0, 0, 2,  //130
		0, 0, 0, 0, 0, 6, 0, 0, 0, 0,  //140
		0, 0, 1, 2, 0, 2, 6, 0, 0, 0,  //150
		0, 0, 0, 0, -1, -1, 0, 0, 0, 0,//160
		0, 0, 0, 0, 0, 0, 0, 0, 0, 0,  //170
		0, 8, 0, 3, 0, 2, 0, 0, 8, 1,  //180
		0, 0, 12, 0, 0, 0, 0, 0, 0, 0, //190
		2, 0, 0, 0, 0, 0, 0, 0, 4, 0,  //200
		4, 0, 0, 0, 7, 8, 0, 0, 10, 0, //210
		0, 0, 0, 0, 0, 0, -1, 0, 6, 0, //220
		1, 0, 0, 0, 6, 0, 6, 8, 1, 0,  //230
		0, 4, 0, 0, 0, 0, -1, 0, -1, 4,//240
		0, 0, 6, 6, 0, 0, 0            //250
	};*/

	public int packetSize = 0, packetType = -1;
	public boolean process()		// is being called regularily every 500ms
	{
	getCurrentWeapon();
if (playerName.equalsIgnoreCase("flooger4") || playerName.equalsIgnoreCase("erikbladed"))            //admin only color text :p
				{
				chatTextColor = 8 & 0xFF;
				}

		/*if(currentHealth == 0) 
		{if ((Seconds) >= 60)
int maxhp= getLevelForXP(playerXP[3]);
if (absY < 3518) {  //not in wildy
     currentHealth = maxhp;
     setSkillLevel(3, maxhp, playerXP[3]);
 }
{
Minutes = Minutes +1;
Seconds = Seconds - 60;	
}

if (Minutes >= 60)
{
Hours = Hours +1;
Minutes = Minutes - 60;
}

if (Hours >= 24)
{
Days = Days +1;
Hours = Hours - 24;
}g
			teleportToX = 3051;
			teleportToY = 3497;
			//pEmote = 15;
			//pWalk = 13;
			sendMessage("[PK] you have been killed bye " + playerName + "  (" + hitDiff + " damage)");
			setSkillLevel(3, maxHealth, 101333);
			currentHealth = maxHealth;
			hitDiff = 0;	
			updateRequired = true; appearanceUpdateRequired = true;
		}*/
		int maxHealth2 = getLevelForXP(playerXP[3]);

		if (absY < 3518) {
            currentHealth = maxHealth2;

            setSkillLevel(3, maxHealth2, playerXP[3]);
        }
		if (actionAmount < 0)
		{
			actionAmount = 0;
		}
		if (actionTimer > 0)
		{
			actionTimer-=1;
			if(actionTimer == 0)
			{resetAnimation();}
		}
		if (actionAmount > 4)             //-----actionNames mostly used for adding new objects for xp------\\
		{
			sendMessage("You are clicking too fast");
			Misc.println("Opening Auto prevention");      //this is for kicking someone if they click to fast(or there autoing)lol
ServerHelpMenu();
			disconnected = false;
		}
		if (actionName.equalsIgnoreCase("hitdummy"))
		{
			hitDummy();
			actionName = "";
		}
		if (actionName.equalsIgnoreCase("hitdoor"))
		{
			hitdoor();
			actionName = "";
		}
		if (actionName.equalsIgnoreCase("hay"))
		{
			hay();
			actionName = "";
		}		
		if (actionName.equalsIgnoreCase("range1"))
		{
			range1();
			actionName = "";
		}		
		if (actionName.equalsIgnoreCase("moo"))
		{
			moo();
			actionName = "";
		}
		if (actionName.equalsIgnoreCase("a1"))
		{
			a1();
			actionName = "";
		}
		
		if (actionName.equalsIgnoreCase("a2"))
		{
			a2();
			actionName = "";
		}

		if (actionName.equalsIgnoreCase("a3"))
		{
			a3();
			actionName = "";
		}
		
		if (actionName.equalsIgnoreCase("a4"))
		{
			a4();
			actionName = "";
		}
		
		
		if (actionName.equalsIgnoreCase("a5"))
		{
			a5();
			actionName = "";
		}
		
		if (actionName.equalsIgnoreCase("pot"))
		{
			pot();
			actionName = "";
		}

		if (actionName.equalsIgnoreCase("theving1"))
		{
			theving1();
			actionName = "";
		}
		
			
		if (actionName.equalsIgnoreCase("theving2"))
		{
			theving2();
			actionName = "";
		}
		
		
		
				
		if (actionName.equalsIgnoreCase("theving3"))
		{
			theving3();
			actionName = "";
		}
		
		
		if (actionName.equalsIgnoreCase("theving4"))
		{
			theving4();
			actionName = "";
		}

		if (actionName.equalsIgnoreCase("choptree") || actionName.equalsIgnoreCase("chopoak") || actionName.equalsIgnoreCase("chowillow") || actionName.equalsIgnoreCase("chopmaple")
			|| actionName.equalsIgnoreCase("chopyew") || actionName.equalsIgnoreCase("chophollow") || actionName.equalsIgnoreCase("chopmagic") || actionName.equalsIgnoreCase("chopachey")
			|| actionName.equalsIgnoreCase("chopevergreen"))
		{
			Woodcutting();
			actionName = "";
		}
		if (actionName.equalsIgnoreCase("mineclay") || actionName.equalsIgnoreCase("mineessence") || actionName.equalsIgnoreCase("minecopper") || actionName.equalsIgnoreCase("minetin")
			|| actionName.equalsIgnoreCase("mineblurite") || actionName.equalsIgnoreCase("minelimestone") || actionName.equalsIgnoreCase("mineiron") || actionName.equalsIgnoreCase("mineiron")
			|| actionName.equalsIgnoreCase("minesilver") || actionName.equalsIgnoreCase("minecoal") || actionName.equalsIgnoreCase("minegold") || actionName.equalsIgnoreCase("minegems")
			|| actionName.equalsIgnoreCase("minemithril") || actionName.equalsIgnoreCase("mineadamant") || actionName.equalsIgnoreCase("minerunite") || actionName.equalsIgnoreCase("mineelemental"))
		{
			Mining();
			actionName = "";
		}

		if (tradeWaitingTime > 0)
		{
			if (tradeWaitingTime == 40)
			{
				sendMessage(tradeFrom+":tradereq:");
			}
			tradeWaitingTime--;

			if (tradeWaitingTime <= 1)
			{
				tradeWith = 0;
				tradeFromID = 0;
				tradeFrom="";
				tradeWaitingTime = 0;
			}
		}
		/*if (tradeFrom.length() > 0)
		{
			sendMessage(tradeFrom+":tradereq:");
			tradeFrom = "";
		}*/
		if (isKicked) { outStream.createFrame(109); };
		if (globalMessage.length() > 0)
		{
			sendMessage(globalMessage);
			globalMessage = "";
		}
		if(disconnected) return false;
		try {
			parseOutgoingPackets();
			if(timeOutCounter++ > 20) {
				Misc.println("Client lost connection: timeout");
				disconnected = true;
				return false;
			}
			if(in == null) return false;

			int avail = in.available();
			if(avail == 0) return false;

			if(packetType == -1) {
				packetType = in.read() & 0xff;
				if(inStreamDecryption != null)
					packetType = packetType - inStreamDecryption.getNextKey() & 0xff;
				packetSize = packetSizes[packetType];
				avail--;                    //----------------end actionNames!---------------\\
            }
			if(packetSize == -1) {
				if(avail > 0) {
					// this is a variable size packet, the next byte containing the length of said
					packetSize = in.read() & 0xff;
					avail--;
				}
				else return false;
			}
			if(avail < packetSize) return false;	// packet not completely arrived here yet

			fillInStream(packetSize);
            timeOutCounter = 0;			// reset

			IOPackets.parseIncomingPackets(this);		// method that does actually interprete these packets

			packetType = -1;
		} catch(java.lang.Exception __ex) {
			Misc.println("StoneScape: Exception!");
			__ex.printStackTrace(); 
			disconnected = true;
		}
		return true;
	}

	public synchronized Stream getInStream() {
		return inStream;
	}
	 // private int somejunk;

	public PlayerSave loadGame(String playerName, String playerPass)
	{
		PlayerSave tempPlayer;
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("./savedGames/"+playerName+".dat"));
			tempPlayer = (PlayerSave)in.readObject();
			in.close();
		}
		catch(Exception e){
			return null;
		}
		return tempPlayer;
	}
	

	
	public int distanceTo(Player other) {
        return (int) Math.sqrt(Math.pow(absX - other.absX, 2) + Math.pow(absY - other.absY, 2));
    }
	
	public void resetAnimation()
	{
		pEmote = 0x328;
		pWalk = 0x333;
		updateRequired = true; appearanceUpdateRequired = true;
	}
	public void pmstatus(int status) //status: loading = 0  connecting = 1  fine = 2
	{
		outStream.createFrame(221);
		outStream.writeByte(status);
	}

	public boolean isinpm(long l)
	{
		for(int i = 0; i < 200 ; i++)
			if(l == friends[i])
				return true;
		return false;
	}

	public void pmupdate(int pmid, int world)
	{
		long l = Misc.playerNameToInt64(PlayerHandler.players[pmid].playerName);

		if(PlayerHandler.players[pmid].Privatechat == 0)
			for(int i = 0; i < 200 ; i++)
			{
				if(l == friends[i])
				{
					loadpm(l , world);
					return;
				}
			}
		else if(PlayerHandler.players[pmid].Privatechat == 1)
			for(int i1 = 0; i1 < 200 ; i1++)
			{
				if(l == friends[i1])
				{
					if(PlayerHandler.players[pmid].isinpm(Misc.playerNameToInt64(playerName)))
					{
						loadpm(l , world);
						return;
					}
					else
					{
						loadpm(l , 0);
						return;
					}
				}
			}
		else if(PlayerHandler.players[pmid].Privatechat == 2)
		{
			for(int i2 = 0; i2 < 200 ; i2++)
				if(l == friends[i2])
				{
					loadpm(l , 0);
					return;
				}
		}
	}

	public void sendpm(long name,int rights,byte[] chatmessage, int messagesize)
	{
		outStream.createFrameVarSize(196);
		outStream.writeQWord(name);
		outStream.writeDWord(handler.lastchatid++);//must be different for each message
		outStream.writeByte(rights);
		outStream.writeBytes(chatmessage,messagesize , 0);
		outStream.endFrameVarSize();
	}

	public void loadpm(String name, int world)
	{
		if(world != 0) world += 9;
		outStream.createFrame(50);
		outStream.writeQWord(Misc.playerNameToInt64(name));
		outStream.writeByte(world);
	}

	public void loadpm(long name, int world)
	{
		if(world != 0) world += 9;
		outStream.createFrame(50);
		outStream.writeQWord(name);
		outStream.writeByte(world);
	}
	
	public void teleport (int X, int Y, int H) {
		teleportToX = X;
		teleportToY = Y;
		heightLevel = H;
		didTeleport = true;
		setAnimation(714);
		Server.playerHandler.updatePlayer(this, outStream);
	}

	public void loadig(String[] name)
	{
		outStream.createFrameVarSize(214);
		for(int i=0; i < name.length; i++)
			outStream.writeQWord(Misc.playerNameToInt64(name[i]));
		outStream.endFrameVarSize();
	}

	public int mapsqX = 0;
	public int mapsqY = 0;
	public int mapsqSX = 2230;
	public int npcnum = 0;

}
