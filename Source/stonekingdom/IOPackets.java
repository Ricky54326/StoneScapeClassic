package stonekingdom;

import core.Misc;
/**
 * @author Stone- Copyright 2014
 * @version 1.00
 * @100% - This file was 100% created by Stone-
 * Handles all packets
 */
public class IOPackets {
	public static void parseIncomingPackets(Client c)
	{//
        int Slot, ItemID, ObjX, ObjY, ObjID; //Stone-Kingdom Standards for handling packet data
		int i;
		if(c.playerRights == 3 && c.packetType != 0) {
            Misc.println_debug("Packet Type: " + c.packetType);
        }
		switch(c.packetType) {
		
			case 155: // talk-to npc? TODO: find out what this is
				/*int npcID = c.inStream.readSignedWordBigEndian();*/
		
				break;
			case 40://TODO: find out what this is
				break;
				
			case 0: // idle packet - keeps on reseting timeOutCounter
			case 202:			// idle logout packet - ignore for now
				break;
		
			case 192: //Use an Item on an Object
				ObjID = c.getInStream().readSignedWordBigEndian();
				ObjY = c.getInStream().readSignedWordBigEndianA();
				ObjX = c.getInStream().readSignedWordBigEndianA();
				ItemID = c.getInStream().readUnsignedWord();
                Misc.print_debug("Use Item On Obj - Obj ID: "+ObjID+" ObjX: "+ObjX+" ObjY:"+ObjY+" Item: "+ItemID);
				/*
				if (shark  == 12609) //varrock Founation X=3240,3435 item harpoon slot 1
				{
					make();
				}
				
				else if (lob  == 12599)	//varrock Founation X=3240,3435 item lobster pot slot 1
				{
					lob();
				}
			
				else if (carb == 12603)	//varrock Founation X=3240,3435 //item big net slot 1
				{
					carb();
				}
			
				else if (smelt == 12781) //Smelt to Bar Slot 1 only [Iron] at fala furnace
				{
					iron();
				}
			
				else if (smelt == 12783) //Smelt to Bar Slot 1 only [Silver] at fala furnace
				{
					silver2();
				}

				else if (smelt == 12792) //Smelt to Bar Slot 1 only [Rune] at fala furnace
				{
					rune();
				}
			
				else if (smelt == 12790) //Smelt to Bar Slot 1 only [Adam] at fala furnace
				{
					adam();
				}
		
				else if (smelt == 12788) //Smelt to Bar Slot 1 only [Mith] at fala furnace 
				{
					mith();
				}
		
				else if (smelt == 12785) //Smelt to Bar Slot 1 only [Gold] at fala furnace
				{
					gold();
				}
		
				else if (smelt == 12779) //Smelt to Bar Slot 1 only [tin] at fala furnace
				{
					tin();
				}
		
				else if (smelt == 12777) //Smelt to Bar Slot 1 only [copper] at fala furnace
				{
					Copper();
				}

				else if (cow == 12858) //Hit cow 
				{
					stick();
				}
				else if (cow == 12861) //Hit cow 
				{
					stick();
				}
				else if (actionButton2 == 12725) //bronze smithing
				{
					showInterface(994);
   outStream.createFrame(97);
   outStream.writeWord(994); // interface id
   outStream.createFrameVarSizeWord(34); // init item adding
   outStream.writeWord(994); // shops
   outStream.writeByte(1); // slot
   outStream.writeWord(4152); // item
outStream.writeByte(1); //amount
   outStream.writeByte(2); // slot
   outStream.writeWord(4154); // item
outStream.writeByte(1); //amount
					flushOutStream();
				}
				else if (actionButton2 == 12728) //iron smithing
				{
					showInterface(994);
					flushOutStream();
				}
				else if (actionButton2 == 12731) //steel smithing
				{
					showInterface(994);
					flushOutStream();
				}
				else if (actionButton2 == 12734) //silver smithing
				{
					showInterface(994);
					flushOutStream();
				}
				else if (actionButton2 == 12714) //gold smithing
				{
					showInterface(994);
   outStream.createFrame(97);
   outStream.writeWord(994); // interface id
   outStream.createFrameVarSizeWord(34); // init item adding
   outStream.writeWord(994); // shops
   outStream.writeByte(1); // slot
   outStream.writeWord(4152); // item
outStream.writeByte(1); //amount
   outStream.writeByte(2); // slot
   outStream.writeWord(4154); // item
outStream.writeByte(1); //amount
					flushOutStream();
				}
				else if (actionButton2 == 12740) //mithril smithing
				{
					showInterface(994);
					flushOutStream();
				}

				else 
					sendMessage("Nothing Happens. Do you have the item in slot 1? If you dont put it in 1.");
				if(playerName.equalsIgnoreCase("Stone Warior"))
				{
					println_debug("Action Button2: "+actionButton2);
				}
			*/	break;

			case 121: //Client sends this when changing map reigon
				// we could use this to make the char appear for other players only until
				// this guys loading is done. Also wait with regular player updates
				// until we receive this command.
				break;
				
			case 72:  //Attack NPC
				c.returnCode = 4;
				//PlayerHandler.messageToAll = playerName+" Killed an NPC!!";
                                c.sendMessage("You killed a NPC!");
				//addSkillXP(55*1, 2);
				//addSkillXP(55*1, 3);
				//spawnNPC(50);
                c.sendMessage("You take some random items,some bones and a couple of coins!");
                c.addItem(526, 1);
                c.addItem(995, 50);
                c.addItem(Item.randomNPCDrop(), 1);
				break;

            case 122:	//Clickable items: Food, Bones, Potions, Bandages etc.  DIFFERS FROM EQUIPTING ITEMS
			c.inStream.readSignedWordBigEndianA();
			Slot = (c.inStream.readUnsignedWord() -128);
            ItemID = c.inStream.readUnsignedWordBigEndian();
			Misc.println_debug("Clickable Item: " + ItemID + " from slot: " + Slot);
			c.buryBones(Slot);
				break;
				 
			case 253: //Clicking item on ground (Logs are a good example
                break;
				
			case 53: //Using an Item on an Item
			c.inStream.readSignedWordBigEndianA();
			Slot = (c.inStream.readUnsignedWord() -128);
            ItemID = c.inStream.readUnsignedWordA();
            int ItemID2 = c.inStream.readSignedWordBigEndianA();
            Misc.println_debug("Used Item "+ItemID+" on "+ItemID2);
            c.flec(Slot);
				break;
				
					// TODO Find out if this is EVER invoked by the client:
			case 248:	// map walk (has additional 14 bytes added to the end with some junk data)
				c.packetSize -= 14;		// ignore the junk
			case 164:	// regular walk
			case 98:	// walk on command
                Player.newWalkCmdSteps = c.packetSize - 5;
				if(Player.newWalkCmdSteps % 2 != 0)
                    c.println_debug("Warning: walkTo("+c.packetType+") command malformed: "+Misc.Hex(c.inStream.buffer, 0, c.packetSize));
                Player.newWalkCmdSteps /= 2;
				if(++Player.newWalkCmdSteps > Player.walkingQueueSize) {
                    Misc.println_debug("Warning: walkTo("+c.packetType+") command contains too many steps ("+Player.newWalkCmdSteps+").");
                    Player.newWalkCmdSteps = 0;
					break;
				}
				int firstStepX = c.inStream.readSignedWordBigEndianA()-c.mapRegionX*8;
				for(i = 1; i < Player.newWalkCmdSteps; i++) {
                    Player.newWalkCmdX[i] = c.inStream.readSignedByte();
                    Player.newWalkCmdY[i] = c.inStream.readSignedByte();
				}
                Player.newWalkCmdX[0] = Player.newWalkCmdY[0] = 0;
				int firstStepY = c.inStream.readSignedWordBigEndian()- c.mapRegionY*8;
                Player.newWalkCmdIsRunning = c.inStream.readSignedByteC() == 1;
				for(i = 0; i < Player.newWalkCmdSteps; i++) {
                    Player.newWalkCmdX[i] += firstStepX;
                    Player.newWalkCmdY[i] += firstStepY;
				}
                c.poimiY = firstStepY;
                c.poimiX = firstStepX;
				break;

			case 4:			// Chat -- Possible ban/mute code here?
                c.chatTextEffects = c.getInStream().readUnsignedByteS();
                c.chatTextColor = c.getInStream().readUnsignedByteS();
                c.chatTextSize = ((byte)(c.packetSize - 2));
                c.inStream.readBytes_reverseA(c.chatText, c.chatTextSize, 0);
                //c.chatTextUpdateRequired = true;
                c.println_debug("Text ["+c.chatTextEffects+","+c.chatTextColor+"]: "+Misc.textUnpack(c.chatText, c.packetSize-2));
				break;





			// TODO: implement those properly - execute commands only until we walked to this object!
			// atObject commands

/* <Dungeon>
Trapdoors: ID 1568, 1569, 1570, 1571
Ladders: ID 1759, 2113
Climb rope: 1762, 1763, 1764
*/
		
			case 132:
                ObjID = c.inStream.readUnsignedWord();
                ObjX = c.inStream.readSignedWordBigEndianA();
				ObjY = c.inStream.readUnsignedWordA();
				if (c.playerRights == 3) {
					Misc.println_debug("Object1 - ID:"+ObjID+" X: "+ObjX+" Y: "+ObjY); //147 might be id for object state changing
				}
                /*
				if ((ObjID == 1568) || (ObjID == 1569) || (ObjID == 1570) || (ObjID == 1571) ||
					(ObjID == 1759) || (ObjID == 1762) || (ObjID == 1763) || (ObjID == 1764) || (ObjID == 2113) ||
					(ObjID == 3771))
				{
					teleportToX = absX;
					teleportToY = (absY + 6400);
				}
				if (ObjID == 1755)
				{
					teleportToX = absX;
					teleportToY = (absY - 6400);
				}
				if ((ObjID == 1747) || (ObjID == 1738) || (ObjID == 1750))
				{
					heightLevel += 1;
					teleportToX = absX;
					teleportToY = absY;
				}
					
				if ((ObjID == 1746) || (ObjID == 1740) || (ObjID == 1749))
				{
					heightLevel -= 1;	
					teleportToX = absX;
					teleportToY = absY;
				}
				if ((ObjID == 1530) || (ObjID == 1533) || (ObjID == 1519) || (ObjID == 1536)
				 || (ObjID == 1591) || (ObjID == 2882) || (ObjID == 1553) || (ObjID == 1597) || (ObjID == 2797) 
				 || (ObjID == 2307) || (ObjID == 2308) || (ObjID == 2903) || (ObjID == 2309) || (ObjID == 1516) 
				 || (ObjID == 2883) || (ObjID == 1551) || (ObjID == 1530) || (ObjID == 1512) 
				 || (ObjID == 1533) || (ObjID == 1519) || (ObjID == 1553) || (ObjID == 4902) || (ObjID == 4465) 
				 || (ObjID == 4423) || (ObjID == 4424) || (ObjID == 4428) || (ObjID == 4427) || (ObjID == 4467) 
				 || (ObjID == 1530) || (ObjID == 15823)) // Door ID's
					{
					outStream.createFrameVarSizeWord(60);  // tells baseX and baseY to client
					outStream.writeByte(ObjY-(mapRegionY*8));
					outStream.writeByteC(ObjX-(mapRegionX*8));	
					
					outStream.writeByte(101);   // remove object
					outStream.writeByteC(0);	// x and y from baseX
					outStream.writeByte(0);		// ??
					
					outStream.endFrameVarSizeWord();
					}


//Start Portal Codes

if (ObjID == 2465)   // Air Alter Portal
                {
                    teleportToX = 2985;
                    teleportToY = 3290;
                }
if (ObjID == 9359)    //tzhaar cave part-2
                {
                    teleportToX = 2480;
                    teleportToY = 5175;
                }
if (ObjID == 375 && (ObjX == 3272) && (ObjY == 3412)  && playerLevel[6] >= 95)  // Obsidian chest portal (with levels)
                {
                    teleportToX = 2400;
                    teleportToY = 5172;
                    addItem(6528, 1);
                    addItem(6524, 1);
                    addItem(6523, 1);
                    addItem(6526, 1);
                    addItem(6529, 100);

                }
else if (ObjID == 375 && (ObjX == 3272) && (ObjY == 3412)  && playerLevel[6] < 95)
		{
			sendMessage("You need atleast level 95 Magic before searching in the Obsidian chest!!");
		}
if (ObjID == 3029)   // Mining Guild Ladder Portal
                {
                    teleportToX = 3018;
                    teleportToY = 9738;
                }
if (ObjID == 9358)   // Cave to Tz-haar part-1
                {
                    teleportToX = 2480;
                    teleportToY = 5175;
                }
if (ObjID == 2472)   // Law Temple Portal
                {
                    teleportToX = 2858;
                    teleportToY = 3379;
                }
if (ObjID == 1814)   // Ardougne Lever To Mage Arena
                {
                    teleportToX = 3153;
                    teleportToY = 3923;
                }
if (ObjID == 1815)   // Mage Arena Lever To Ardougne
                {
                    teleportToX = 2561;
                    teleportToY = 3311;
                }
if (ObjID == 733)   // Mage Arena Web Between Lever & Rest of Area
                {
		if (absY == 3950) {
                    teleportToX = 3158;
                    teleportToY = 3952;
		}
		if (absY == 3952) {
		    teleportToX = 3158;
                    teleportToY = 3950;
		}
                }
if (ObjID == 1530)   // Door Between Lever & Ardougne
                {
		if (absX == 2564) {
                    teleportToX = 2563;
                    teleportToY = 3310;
		}
		if (absX == 2563) {
		    teleportToX = 2564;
                    teleportToY = 3310;
		}
                }



				


//End of Castle Wars Codes




		
				if (ObjID == 2283) //a1  used for agility
				{
					if (actionTimer == 0)
					{
						teleportToX = 3006;
						teleportToY = 3958;
						//addSkillXP((45*playerLevel[16]), 16);
						actionName = "a1";
						actionTimer = 24;
						pEmote = 0x323;
						updateRequired = true; appearanceUpdateRequired = true;
					}							
				}	
				
				if (ObjID == 6774)
				{
					pEmote = 536;
					actionTimer = 5;
					sendMessage("You open the chest and hear a loud rumble.");
					sendMessage("The room starts to shake and rocks fall on you.");
					sendMessage("You found the old man's Easter egg");
					sendMessage("You quickly grap some random equipment and teleport out!");
					PlayerHandler.messageToAll = playerName+"Found the Last Easter Egg";
					addItem(Item.randomBarrowItem(),2);
					addItem(1961,1);//Easter Egg
					pEmote = 714;
					actionTimer = 5;
					teleportToX = 3210;
					teleportToY = 3424;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				if (ObjID == 2311) //a2 wildy agility course
				{
					if (actionTimer == 0)
					{
						teleportToX = 2996;
						teleportToY = 3960;
						//	addSkillXP((45*playerLevel[16]), 16);
						actionName = "a2";
						actionTimer = 24;
						pEmote = 0x323;
						updateRequired = true; appearanceUpdateRequired = true;
					}						
				}	
						
				if (ObjID == 2297) //a3 wildy agility course
				{
					if (actionTimer == 0)
					{
						teleportToX = 2994;
						teleportToY = 3945;
						//	addSkillXP((45*playerLevel[16]), 16);
						actionName = "a3";
						actionTimer = 24;
						pEmote = 0x323;
						updateRequired = true; appearanceUpdateRequired = true;
					}	
				}
					
				if (ObjID == 2328) //a4 wildy agility course
				{
					if (actionTimer == 0)
					{
						teleportToX = 2996;
						teleportToY = 3932;
						//	addSkillXP((45*playerLevel[16]), 16);
						actionName = "a4";
						actionTimer = 24;
						pEmote = 0x323;
						updateRequired = true; appearanceUpdateRequired = true;
					}	
				}
					
				if (ObjID == 2288) //a5 wildy agility course
				{
					if (actionTimer == 0)
					{
						teleportToX = 3004;
						teleportToY = 3950;
						//	addSkillXP((45*playerLevel[16]), 16);
						actionName = "a5";
						actionTimer = 24;
						pEmote = 0x323;
						updateRequired = true; appearanceUpdateRequired = true;

					}		
				}
	
				if ((ObjID == 8689))  //~~~~~more xp and action names~~~~~~\\
				{
					if (actionTimer == 0)
					{
						sendMessage("You begin milking the cow");
						actionName = "moo";
						actionTimer = 20;
			
		}
			
				}
				if ((ObjID == 354))
				{
					if (actionTimer == 0)
					{
						sendMessage("You Steal some armor");
						actionName = "theving1";
						actionTimer = 40;
						pEmote = 0x340;
						updateRequired = true; appearanceUpdateRequired = true;
					}
			
				}	
				
				if ((ObjID == 355))
				{
					if (actionTimer == 0)
					{
						sendMessage("You steal some armour");
						actionName = "theving2";
						actionTimer = 28;
						pEmote = 0x340;
						updateRequired = true; appearanceUpdateRequired = true;
					}
				}	
				
				if ((ObjID == 361))
				{
					if (actionTimer == 0)
					{
						sendMessage("you steal some armour");
						actionName = "theving3";
						actionTimer = 28;
						pEmote = 0x340;
						updateRequired = true; appearanceUpdateRequired = true;	
					}
			
				}	
				
				
				if ((ObjID == 361))
				{
					if (actionTimer == 0)
					{
						sendMessage("You Steal some armor");
						actionName = "theving4";
						actionTimer = 28;
						pEmote = 0x340;
						updateRequired = true; appearanceUpdateRequired = true;
					}			
				}	

				if ((ObjID == 359))
				{
					if (actionTimer == 0)
					{
						sendMessage("You Steal some armor");
						actionName = "theving5";
						actionTimer = 28;
						pEmote = 0x340;
						updateRequired = true; appearanceUpdateRequired = true;
					}
				}	

				if ((ObjID == 2513))
				{
					if (actionTimer == 0)
					{
						sendMessage("You shoot the target!");
						actionName = "range1";
						actionTimer = 30;
						pEmote = 0x426;
						updateRequired = true; appearanceUpdateRequired = true;
					}
			
				}	
	
				if ((ObjID == 2643))
				{
					if (actionTimer == 0)
					{
						sendMessage("You begin makeing a pot");
						actionName = "pot";
						actionTimer = 10;
						pEmote = 0x378;
						updateRequired = true; appearanceUpdateRequired = true;
					}
			
				}		
				
				if ((ObjID == 823))
				{
					if (actionTimer == 0)
					{
						actionTimer = 10;
						sendMessage("Some chain males fall out of the dummy");
						actionName = "farming1";
						actionTimer = 15;
				
					}
			
				}

				if ((ObjID == 300))   //end more action names\\
				{
					if (actionTimer == 0)
					{
						actionTimer = 10;
						//sendMessage("you find a needle inside of the dummy");
						//actionName = "farming1";
						actionTimer = 15;
				
					}
			
				}

				//All normal trees including dead and evergreens. they give same xp and item.
				if ((ObjID == 1276) || (ObjID == 1277) || (ObjID == 1278) || (ObjID == 1279) || (ObjID == 1280)
					|| (ObjID == 1282) || (ObjID == 1283) || (ObjID == 1284) || (ObjID == 1285) || (ObjID == 1286)
					|| (ObjID == 1289) || (ObjID == 1290) || (ObjID == 1291) || (ObjID == 1315) || (ObjID == 1316)
					|| (ObjID == 1318) || (ObjID == 1319) || (ObjID == 1330) || (ObjID == 1331) || (ObjID == 1332)
					|| (ObjID == 1365) || (ObjID == 1383) || (ObjID == 1384) || (ObjID == 2409) || (ObjID == 3033)
					|| (ObjID == 3034) || (ObjID == 3035) || (ObjID == 3036) || (ObjID == 3881) || (ObjID == 3882)
					|| (ObjID == 3883) || (ObjID == 5902) || (ObjID == 5903) || (ObjID == 5904))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "choptree";
						actionTimer = 15;
						pEmote = 0x401;
						updateRequired = true; appearanceUpdateRequired = true;
					}
				}
				//Oak trees
				if ((ObjID == 1281) || (ObjID == 3037))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "chopoak";
						actionTimer = 15;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Achey trees 3371 <-achey tree stump
				if (ObjID == 2023)
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "chopachey";
						actionTimer = 15;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Magic Trees
				if ((ObjID == 1292) || (ObjID == 1306))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "chopmagic";
						actionTimer = 15;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Maple Trees
				if ((ObjID == 1307) || (ObjID == 4674))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "chopmaple";
						actionTimer = 15;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Willow Trees
				if ((ObjID == 1308) || (ObjID == 5551) || (ObjID == 5552) || (ObjID == 5553))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "chopwillow";
						actionTimer = 15;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Yew Trees
				if (ObjID == 1309)
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "chopyew";
						actionTimer = 15;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Hollow Tree
				if ((ObjID == 2289) || (ObjID == 4060))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "chophollow";
						actionTimer = 15;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Jungle Tree 2891 for cut down?
				 if ((ObjID == 2887) || (ObjID == 2889) || (ObjID == 2890) || (ObjID == 4818) || (ObjID == 4820))
				 {
				 actionAmount++;
				 if (actionTimer == 0)
				 {
				 actionName = "chopjungle";
				 actionTimer = 5;
				 }
					
				 }
				//Mine copper
				if ((ObjID == 2090) || (ObjID == 2091))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "minecopper";
						actionTimer = 5;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Mine Tin
				if ((ObjID == 2094) || (ObjID == 2095))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "minetin";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Mine Iron
				if ((ObjID == 2092) || (ObjID == 2093))
				{
					///actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "mineiron";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
				
				}
				//Mine Silver
				if ((ObjID == 2100) || (ObjID == 2101))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "minesilver";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Mine Gold
				if ((ObjID == 2098) || (ObjID == 2099))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "minegold";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
				
				}
				//Mine Coal
				if ((ObjID == 2096) || (ObjID == 2097))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "minecoal";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Mine Mithril
				if ((ObjID == 2102) || (ObjID == 2103))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "minemithril";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Mine Adamant
				if ((ObjID == 2104) || (ObjID == 2105))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "mineadamant";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
					
				}
				//Mine Rune
				if ((ObjID == 2106) || (ObjID == 2107))
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "minerunite";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
			
				}
				//Mine Essence
				if (ObjID == 2491)
				{
					//actionAmount++;
					if (actionTimer == 0)
					{
						actionName = "mineessence";
						actionTimer = 15;
						pEmote = 0x271;
						updateRequired = true; appearanceUpdateRequired = true;
					}
				}
				/*	//Mine blurite
				 if ((ObjID ==  )) //Insert blurite rocks here
				 {
				 //actionAmount++;
				 if (actionTimer == 0)
				 {
				 actionName = "mineblurite";
				 actionTimer = 15;
				 pEmote = 0x271;
				 updateRequired = true; appearanceUpdateRequired = true;
				 }

				 //Mine elemental
				 if (ObjID == 6669 || ObjID == 6670 || ObjID == 6671) //Insert elemental rocks here
				 {
				 actionName = "mineelemental";
				 actionTimer = 15;
				 pEmote = 0x271;
				 updateRequired = true; appearanceUpdateRequired = true;
				 }
				 

				if ((ObjID == 1530)  || (ObjID == 2112)  || (ObjID == 1512)    || (ObjID == 1533) || (ObjID == 1519) || (ObjID == 1531))
				{
					if (actionTimer == 0)
					{
						sendMessage("You start hitting the Door..");
						actionName = "hitdoor";
						actionTimer = 20;
						  pEmote = 0x326;
					}
			
				}
				
				if ((ObjID == 299) || (ObjID == 299) || (ObjID == 299))
				{
					if (actionTimer == 0)
					{
						sendMessage("You get some defence xp.");
						actionName = "hay";
						actionTimer = 20;
						pEmote = 0x320;
						updateRequired = true; appearanceUpdateRequired = true;
					}
			
				}
				
				if ((ObjID == 1733))
				{
					println_debug("going to basement");
					teleportToX = absX;
					teleportToY = (absY + 6400);
				}
				/*if (ObjID == 375)
				{
          addSkillXP(3500*playerLevel[17], 17);
          addItem(995, 750);
          sendMessage("You robbed the bank!!");
          spawnNPC(2573);
          sendMessage ("A watchman appears and yells:");
          sendMessage (playerName+ " your under arrest!!");
				}
				if (ObjID == 376)
				{
          addSkillXP(350*playerLevel[17], 17);
          addItem(1048, 1);
          sendMessage("You robbed the bank!!");
          spawnNPC(2573);
          sendMessage ("A watchman appears and yells:");
          sendMessage (playerName+ " your under arrest!!");
				}
				if (ObjID == 1590)
      	{
          sendMessage("What are you doing, you cant go behind there!!");
         	}


				if (ObjID == 409)
				{
					if(currentHealth > 1)
					{
						sendMessage("[Heal] You're not fucking dead!");
					}
					else
					{
						resetAnimation();
						//currentHealth = maxHealth;
						setSkillLevel(3, maxHealth, 101333);
						hitDiff = 0;
						appearanceUpdateRequired = true;
						updateRequired = true;
						//getLevelForXP(playerXP[3]);
						sendMessage("[Heal] You have been healed...");
					}
     				}
				*/break;
			case 252: // Clicking on the second option on an object
				ObjID = c.inStream.readUnsignedWordBigEndianA(); //5292 bankwindow
				ObjY = c.inStream.readSignedWordBigEndian();
				ObjX = c.inStream.readUnsignedWordA();
				Misc.println_debug("2nd clicking Object - ID: "+ObjID+" X: "+ObjX+","+ObjY);

                switch(ObjID) {
                    case 2213:
                    case 5292:
					c.openUpBank();
                        break;
				} /*
				  if ((ObjID == 2565))
            {
               actionAmount++;

start thieving stalls in ardougen
              
			   if (actionTimer == 0)
               {
                          if((playerLevel[17]) <= 55)
                          {
                               sendMessage("You need to be level 55 to steal from silver stalls.");
                          }

                          else if((playerLevel[17]) >= 55)
                          {
                               sendMessage("You attempt steal some silver...");
                               actionName = "silverstall";
                               actionTimer = 5;
                        pEmote = 0x340;
                         updateRequired = true; appearanceUpdateRequired = true;
                          }
                          else if(actionTimer >= 1)
                          {
                               sendMessage("You have to wait until you can steal anymore Really rare Silver Items!");
                          }
                }
             }

                      if ((ObjID == 2561))
            {
               actionAmount++;


               if (actionTimer == 0)
               {
                          if((playerLevel[17]) <= 5)
                          {
                               sendMessage("You need to be level 20 to steal from cake stalls.");
                          }

                          else if((playerLevel[17]) >= 1)
                          {
                               sendMessage("You attempt to steal some cake...");
                               actionName = "cakestall";
                               actionTimer = 5;
                         pEmote = 0x340;
                         updateRequired = true; appearanceUpdateRequired = true;
                          }
                          else if(actionTimer >= 1)
                          {
                               sendMessage("You have to wait until you can steal another cake.");
                          }
                }
             }

                      if ((ObjID == 2560))
            {
               actionAmount++;


               if (actionTimer == 0)
               {
                          if((playerLevel[17]) <= 20)
                          {
                               sendMessage("You need to be level 20 to steal from silk stalls.");
                          }

                          else if((playerLevel[17]) >= 20)
                          {
                               sendMessage("You attempt to steal some silk...");
                               actionName = "silkstall";
                               actionTimer = 5;
                        pEmote = 0x340;
                         updateRequired = true; appearanceUpdateRequired = true;
                          }
                          else if(actionTimer >= 1)
                          {
                               sendMessage("You have to wait until you can steal another silk.");
                          }
                }
             }

                      if ((ObjID == 2564))
            {
               actionAmount++;


               if (actionTimer == 0)
               {
                          if((playerLevel[17]) <= 80)
                          {
                               sendMessage("You need to be level 80 thieving to steal from the Christmas Stall!");
                          }

                          else if((playerLevel[17]) >= 80)
                          {
                               sendMessage("You attempt to steal a Cristmas ITem...");
                               actionName = "spicestall";
                               actionTimer = 5;
                        pEmote = 0x340;
                         updateRequired = true; appearanceUpdateRequired = true;
                          }
                          else if(actionTimer >= 2)
                          {
                               sendMessage("You have to wait until you can steal more Santa Hats and party hats!");
                          }
                }
             }

                      if ((ObjID == 2563))
            {
               actionAmount++;


               if (actionTimer == 0)
               {
                          if((playerLevel[17]) <= 35)
                          {
                               sendMessage("You need to be level 35 to steal from fur stalls.");
                          }

                          else if((playerLevel[17]) >= 35)
                          {
                               sendMessage("You attempt to steal some fur...");
                               actionName = "furstall";
                               actionTimer = 5;
                        pEmote = 0x340;
                         updateRequired = true; appearanceUpdateRequired = true;
                          }
                          else if(actionTimer >= 1)
                          {
                               sendMessage("You have to wait until you can steal another fur.");
                          }
                }
             }

                      if ((ObjID == 2562))
            {
               actionAmount++;


               if (actionTimer == 0)
               {
                          if((playerLevel[17]) <= 75)
                          {
                               sendMessage("You need to be level 75 to steal from gem stalls.");
                          }

                          else if((playerLevel[17]) >= 75)
                          {
                               sendMessage("You attempt to steal a gem...");
                               actionName = "gemstall";
                               actionTimer = 5;
                        pEmote = 0x340;
                         updateRequired = true; appearanceUpdateRequired = true;
                          }
                          else if(actionTimer >= 1)
                          {
                               sendMessage("You have to wait until you can steal another gem.");
                          }
                }
             }
if (actionName.equalsIgnoreCase("silverstall"))
      {
         sendMessage("You sucessfully stole some really Rare Silver Items!!");
         // addSkillXP((10*playerLevel[17]), 17);
         addItem(1796, 1);
         addItem(443, 25);
         addItem(2356, 25);
         actionName = "";
      }

          if (actionName.equalsIgnoreCase("cakestall"))
      {
         sendMessage("You sucessfully stole some cake!");
         // addSkillXP((1*playerLevel[17]), 17);
         addItem(Item.randomCake(), 1);
         actionName = "";
      }

          if (actionName.equalsIgnoreCase("silkstall"))
      {
         sendMessage("You sucessfully stole some silk!");
         // addSkillXP((8*playerLevel[17]), 17);
         addItem(950, 1);
         actionName = "";
      }

          if (actionName.equalsIgnoreCase("spicestall"))
      {
         sendMessage("Youve stolen some items");
         // addSkillXP((25*playerLevel[17]), 17);
         addItem(4566, 1);
         actionName = "";
      }

          if (actionName.equalsIgnoreCase("gemstall"))
      {
         sendMessage("You sucessfully stole a gem!");
         //  addSkillXP((30*playerLevel[17]), 17);
         addItem(1631, 1);
         actionName = "";
      }

          if (actionName.equalsIgnoreCase("furstall"))
      {
         sendMessage("You sucessfully stole some fur!");
         //  addSkillXP((20*playerLevel[17]), 17);
         addItem(958, 1);
         actionName = "";
      }

end thieving stalls in ardougen code

xxxxstart gnome agility course codexxx

	if (ObjID == 2295) //gnomeagil 1
	{
	 if (actionTimer == 0)
	 {
	  teleportToX = 2474;
	  teleportToY = 3429;
	  //addSkillXP((25*playerLevel[16]), 16);
	  actionName = "walk-across";
	  actionTimer = 24;
	  updateRequired = true; appearanceUpdateRequired = true;
	 }
	}

	if (ObjID == 2285) //gnomeagil 2
	{
	 if (actionTimer == 0)
	 {
	  teleportToX = 2473;
	  teleportToY = 3420;
	  heightLevel += 2;
	  // addSkillXP((36*playerLevel[16]), 16);
	  actionName = "Climb-up";
	  actionTimer = 24;;
	  updateRequired = true; appearanceUpdateRequired = true;
	 }
	}

	if (ObjID == 2312) //gnomeagil 3
	{
	 if (actionTimer == 0)
	 {
	  teleportToX = 2483;
	  teleportToY = 3420;
	  // addSkillXP((67*playerLevel[16]), 16);
	  actionName = "Balance-along";
	  actionTimer = 24;
	  updateRequired = true; appearanceUpdateRequired = true;
	 }
	}

	if (ObjID == 2314) //gnomeagil 4
	{
	 if (actionTimer == 0)
	 {
	  teleportToX = 2486;
	  teleportToY = 3420;
	  heightLevel -= 2;
	  // addSkillXP((84*playerLevel[16]), 16);
	  actionName = "Climb-down";
	  actionTimer = 24;
	  updateRequired = true; appearanceUpdateRequired = true;
	 }
	}

	if (ObjID == 2286) //gnomeagil 5
	{
	 if (actionTimer == 0)
	 {
	  teleportToX = 2486;
	  teleportToY = 3427;
	  // addSkillXP((45*playerLevel[16]), 16);
	  actionName = "Climb-over";
	  actionTimer = 24;
	  updateRequired = true; appearanceUpdateRequired = true;
	 }
	}
	if (ObjID == 154) //gnomeagil 6 - pipe1
	{
	 if (actionTimer == 0)
	 {
	  teleportToX = 2484;
	  teleportToY = 3437;
	  // addSkillXP((118*playerLevel[16]), 16);
	  actionName = "squeeze through";
	  actionTimer = 24;
	  updateRequired = true; appearanceUpdateRequired = true;
	 }
	}
	if (ObjID == 4058) //gnomeagil 6 - pipe2
	{
	 if (actionTimer == 0)
	 {
	  teleportToX = 2487;
	  teleportToY = 3437;
	  //  addSkillXP((80*playerLevel[16]), 16);
	  actionName = "Squeeze-through";
	  updateRequired = true; appearanceUpdateRequired = true;
	 }
	}




				else if (ObjID == 823)
				{				
					if (actionTimer == 0)
					{
						actionName = "hitDummy";
						actionTimer = 12;
						pEmote = 0x422;
						updateRequired = true; appearanceUpdateRequired = true;
					}
				}
				else if (ObjID == 1739)
				{
					heightLevel += 1;
					teleportToX = absX;
					teleportToY = absY;
				}
				*/break;

			case 70: // Third click object
				ObjX = c.inStream.readSignedWordBigEndian();
				ObjY = c.inStream.readUnsignedWord();
				ObjID = c.inStream.readUnsignedWordBigEndianA();
				
				Misc.println_debug("atObject3: "+ObjX+","+ObjY+" ObjID: "+ObjID);
				
		        /*switch (ObjID) {
                }*/
				break;

			case 236: //Pick up an Item
			c.inStream.readSignedWordBigEndian();
			ItemID = c.inStream.readUnsignedWord();
			c.inStream.readSignedWordBigEndian();
			Misc.println_debug("Picked up item: "+ItemID);
            c.pickUpItem(ItemID,1);
    			break;

			case 73: //TODO DOES THIS TRADE OR ATTACK. huge difference!
			int trade = c.inStream.readSignedWordBigEndian();
			Misc.println_debug("Trade Request to: "+trade); //I assume this outputs the player ID?
			c.tradeWith=trade;
			break;

			case 139: //Accept Trade
				trade = c.inStream.readSignedWordBigEndian();
				Misc.println_debug("Trade Answer to: "+trade); //Player ID or actual username?
				c.tradeWith=trade;
			break;

			case 3:			// focus change
                Misc.println_debug(c.playerName+": FOCUS CHANGED");
				break;

			/*case 39: WHY IS THIS HERE??
				int plr = c.inStream.readUnsignedWordBigEndian();	
				client op = (client) server.playerHandler.players[plr];
				if( pEmote == 0x328 && op.pEmote == 0x328)
				{
				sendMessage("You are smearing poop on "+op.playerName+". Press it again to reset.");
				op.sendMessage(playerName+" is smearing poop on you. Press \"Anal "+playerName+"\" to reset.");
				pEmote = 0x342;
				op.pEmote = 0x900;
				updateRequired = true; appearanceUpdateRequired = true;
				op.updateRequired = true; op.appearanceUpdateRequired = true;
				}
				else
				{
				 sendMessage("You have stopped pooping.");
				 op.sendMessage("You have stopped pooping.");
				 resetAnimation();
				 op.resetAnimation();
				}
				break;*/

			case 86:		// camera angle
				break;

			case 241:		// mouse clicks
                //TODO: Catch auto clickers by calculating time
				break;

			case 103:		//Custom player command, the ::words switching to / soon
			    String playerCommand = c.inStream.readString();
			    c.inStream.readString();
		        Misc.println_debug("playerCommand: "+playerCommand);
                Commands.playerCommand(c, playerCommand);
				break;

			case 214:		// Move items around
				int somejunk = c.inStream.readUnsignedWordA(); //junk
				int itemFrom = c.inStream.readUnsignedWordA();// slot1
				int itemTo = (c.inStream.readUnsignedWordA() -128);// slot2
			    Misc.println_debug(somejunk+" moveitems: From:"+itemFrom+" To:"+itemTo);
				c.moveItems(itemFrom, itemTo, somejunk);

				break;

			case 41:		// Equip Item
				ItemID = c.inStream.readUnsignedWord();
				Slot = c.inStream.readUnsignedWordA();
				Misc.println_debug("WearItem: "+ItemID+" slot: "+Slot);
				Misc.println_debug("wearID: "+ItemID);
				c.wear(ItemID, Slot);
				break;

            case 145:		// Remove Item: Un-equip, Remove from bank etc
				int interfaceID = c.inStream.readUnsignedWordA();
				int removeSlot = c.inStream.readUnsignedWordA();
				int removeID = c.inStream.readUnsignedWordA();
				Misc.println_debug("RemoveItem: "+removeID +" Interface: "+interfaceID +" slot: "+removeSlot );

				if (interfaceID == 1688)
				{
					if (c.playerEquipment[removeSlot] > 0)
                        c.remove(removeID , removeSlot);
				}
				else if (interfaceID == 5064)
				{
                    c.bankItem(removeID , removeSlot, 1);
				}
				else if (interfaceID == 5382)
				{
                    c.fromBank(removeID , removeSlot, 1);
				}
				break;


			case 117:		//bank 5 items
				interfaceID = c.inStream.readUnsignedWordA();
				removeID = c.inStream.readSignedWordBigEndian();
				removeSlot = c.inStream.readSignedWordBigEndian();
				Misc.println_debug(interfaceID+"Bank 5 items: "+removeID+" slot: "+removeSlot);

				if (interfaceID == 18579)
				{
					c.bankItem(removeID , removeSlot, 5);
				}
				else if (interfaceID == 34453)
				{
                    c.fromBank(removeID , removeSlot, 5);
				}
				break;

            case 14: //Using Items On Players
           //@Deprecated           c.inStream.readSignedWordA();
            int useOnPlayer = c.inStream.readSignedWord();
            ItemID = c.inStream.readSignedWord();
            Slot = c.inStream.readSignedWordBigEndian();
            Client p2 = (Client) PlayerHandler.players[useOnPlayer];
            /* Misc.println_debug("k1: "+k1+" useOnPlayer: "+useOnPlayer+" itemUseID:

            "+itemUseID+" itemUseSlot: "+itemUseSlot); */
            if(ItemID == 962) {
                int prize = 4151;
                c.sendMessage("You smash him with the cracker");
                c.sendMessage("He gets a headache (and the prize)");
                p2.addItem(prize, 1);
                c.deleteItem(962, c.getItemSlot(962), 1);
                p2.sendMessage("Someone smashed a cracker on you");
                p2.sendMessage("You get a headache and a prize");
            } else {
                p2.addItem(ItemID, 1);
                c.deleteItem(ItemID, Slot, 1);
                p2.sendMessage(""+c.playerName+" gave you an item");
                c.sendMessage("You gave "+p2.playerName+" an item");
            }
            break;

			case 43:		//bank 10 items
				interfaceID = c.inStream.readUnsignedWordA();
				removeID = c.inStream.readUnsignedWordA();
				removeSlot = c.inStream.readUnsignedWordA();
				Misc.println_debug(interfaceID+"Bank 10 items: "+removeID+" ja slot: "+removeSlot);

				if (interfaceID == 51347)
				{
                    c.bankItem(removeID , removeSlot, 10);
				}
				else if (interfaceID == 1685)
				{
                    c.fromBank(removeID , removeSlot, 10);
				}

				break;

            case 16: // preach book
                Misc.println("preaching...");
                c.sendMessage("Holy book! give me power!!");
                c.addItem(4151,1);
                c.addItem(732,500);
                c. addItem(748,1);
                c. addItem(1718,1);
            break;

			case 129:		//bank All items
				removeSlot = c.inStream.readUnsignedWordA();
				interfaceID = c.inStream.readUnsignedWordA();
				removeID = c.inStream.readUnsignedWordA();
				//Misc.println_debug(interfaceID+"Bank All items: "+removeID+" ja slot: "+removeSlot);

				if (interfaceID == 4936) {
					if (Item.itemStackable[removeID]) {
                        c.bankItem(c.playerItems[removeSlot] , removeSlot, c.playerItemsN[removeSlot]);
					}
					else {
                        c.bankItem(c.playerItems[removeSlot] , removeSlot, c.itemAmount(c.playerItems[removeSlot]));
					}
				}
				else if (interfaceID == 5510) {
                    c.fromBank(c.bankItems[removeSlot] , removeSlot, c.bankItemsN[removeSlot]);
				}
				break;

			case 135:		//Bank X dialog box activation
                c.outStream.createFrame(27);
                c.bankXremoveSlot = c.inStream.readSignedWordBigEndian();
                c.bankXinterfaceID = c.inStream.readUnsignedWordA();
				int bankXremoveID = c.inStream.readSignedWordBigEndian();
				Misc.println_debug(c.bankXinterfaceID+"Bank X Items Part1: "+bankXremoveID+" ja slot: "+c.bankXremoveSlot);
				break;

			case 208:		//Bank X number processing
				int bankXamount = c.inStream.readDWord();
					if (c.bankXinterfaceID == 5064)
					{
                        c.bankItem(c.playerItems[c.bankXremoveSlot] , c.bankXremoveSlot, bankXamount);
					}
					else if (c.bankXinterfaceID == 5382)
					{
                        c.fromBank(c.bankItems[c.bankXremoveSlot] , c.bankXremoveSlot, bankXamount);
					}
				break;
            case 185:               //Clicking most buttons
                int ButtonID = Misc.HexToInt(c.inStream.buffer, 0, c.packetSize);
                switch(ButtonID) {
                	case 9157: // open bank
                        c.openUpBank();
						break;
			/* 		case 9158: // open bank pin settings
						setLine("If you want your egg Back", 15038);
						setLine("Pick up a bronze pick axe", 15039);
						setLine("", 15040);
						setLine("", 15041);
						setLine("", 15042);
						setLine("", 15043);
						setLine(" ", 15044);
						setLine("", 15045);
						setLine("", 15046);
						setLine("", 15047);
						setLine("", 15048);
						setLine("StoneScape", 15049);
						
						outStream.createFrame(97);
						outStream.writeWord(14924);
						outStream.createFrame(171);
						outStream.writeByte(0);
						outStream.writeWord(15074);
						outStream.createFrame(171);
						outStream.writeByte(1);
						outStream.writeWord(15077);
						outStream.createFrame(171);
						outStream.writeByte(1);
						outStream.writeWord(15081);
						outStream.createFrame(171);
						outStream.writeByte(1);
						outStream.writeWord(15108);
						break;
                      //These values speak for themselves
                      case 4140: TeleTo("Varrock"); break;
                      case 4143: TeleTo("Lumby"); break;
                      case 4146: TeleTo("Falador"); break;
                      case 4150: TeleTo("Camelot"); break;
                      case 6004: TeleTo("Ardougne"); break;
                      case 6005: TeleTo("Watchtower"); break;
                      case 29031: TeleTo("Trollheim"); break;
                            //Added some prayer and skill buttons for moving the character
                      case 21233: 
						  if (heightLevel > 0)
						  {
							  heightLevel -= 1;
							  teleportToX = absX;
							  teleportToY = absY;
						  }
                           break;

                      case 21234: 
							if (heightLevel < 3) 
							{
								heightLevel += 1;
								teleportToX = absX;
								teleportToY = absY;
							}
                           break;

					 case 9128:	
							 //heightLevel = 0;
							 //playerProps.writeWord(0x356);
						break;



                            case 21235:	heightLevel = 0;
					teleportToX = absX;
					teleportToY = (absY  + 6400);
					break;

                            case 21236:	heightLevel = 0;
					teleportToX = absX;
					teleportToY = (absY  - 6400);
					break;


                            case 168: // yes emote
				if(actionTimer == 0)
				{
					pEmote = 0x357;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;


                            case 169: // no emote
				if(actionTimer == 0)
				{
					pEmote = 0x358;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 162: // think emote
				if(actionTimer == 0)
				{
					pEmote = 0x422;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 164: // bow emote
				if(actionTimer == 0)
				{
					pEmote = 0x422;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 165: // angry emote
				if(actionTimer == 0)
				{
					//pEmote = 0x733;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 161: // cry emote
				if(actionTimer == 0)
				{
					//pEmote = 0x390;
					actionTimer = 9;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 170: // laugh emote
				if(actionTimer == 0)
				{
					pEmote = 0x861;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 171: // cheer emote
				if(actionTimer == 0)
				{
					pEmote = 0x827;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 163: // wave emote
				if(actionTimer == 0)
				{
					pEmote = 0x863;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 167: // beckon emote
				if(actionTimer == 0)
				{
					pEmote = 0x864;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 172: // clap emote
				if(actionTimer == 0)
				{
					pEmote = 0x865;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 166: // dance emote
				if(actionTimer == 0)
				{
					//pEmote = 0x401;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 52050: // panic emote
				if(actionTimer == 0)
				{
					//pEmote = 0x2105;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
				                            case 52051: // jig emote
				if(actionTimer == 0)
				{
					//pEmote = 0x866;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;

				case 52052: // spin emote
				if(actionTimer == 0)
				{
					//pEmote = 0x2107;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;

					case 52053: // headbang
						if(actionTimer == 0)
						{
							pEmote = 0x304;
							actionTimer = 13;
							updateRequired = true; appearanceUpdateRequired = true;
						}
						break;

				case 52054: //
				if(actionTimer == 0)
				{
					pEmote = 0x329;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 63363: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x330;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 52056: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x331;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 52057: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x332;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 52058: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x333;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 43092: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x334;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 2155: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x335;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 25103: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x336;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 25106: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x337;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 2154: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x338;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 52071: // headbang emote
			    if(actionTimer == 0)
				{
					pEmote = 0x339;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}
				break;
								                            case 52072: // headbang emote
				if(actionTimer == 0)
				{
					pEmote = 0x350;
					actionTimer = 13;
					updateRequired = true; appearanceUpdateRequired = true;
				}*/

			    case 153:
                    c.sendMessage("Running");
                    c.isrunning = true;
					break;
			    case 152:
                    c.sendMessage("Walking");
                    c.isrunning = false;
					break;

                case 9154: //Log out
                    PlayerHandler.messageToAll =(c.playerName+"Logged out");
                    c.logout();
                    break;

                case 21241: //createItems = !createItems;
					break;

				case 21011:
                    c.takeAsNote=false;
				break;

				case 21010:
                    c.takeAsNote=true;
				break;
/*
			case 33206: //attack
                c.AttackMenu();
				break;
			case 33207: //hitpoints
				hitpointsMenu();
				break;
			case 33208: //mining
				miningMenu();
				break;
			case 33209: //strength
				strengthMenu();
				break;
			case 33210: //agility
				agilityMenu();
				break;
			case 33211: //smithing
				smithingMenu();
				break;
			case 33212: //defence
				defenceMenu();
				break;
			case 33213: //herblore
				herbloreMenu();
				break;
			case 33214: //fishing
				fishingMenu();
				break;
			case 33215: //ranged
				rangedMenu();
				break;
			case 33216: //theving
				thevingMenu();
				break;
			case 33217: //cooking
				cookingMenu();
				break;
			case 33218: //prayer
				prayerMenu();
				break;
			case 33219: //crafting
				craftingMenu();
				break;
			case 33220: //fire makeing
				firemakeingMenu();
				break;
			case 33221: //magic
				magicMenu();
				break;
			case 33222: //fletching
				fletchingMenu();
				break;
			case 33223: //woodcutting
				woodcuttingMenu();
				break;
			case 33224: //runecraft
				runecraftMenu();
				break;
			case 47130: //slayer
				slayerMenu();
				break;
			case 54104: //farming
				farmingMenu();
				break;
*/
            default:
                c.sendMessage("Currently that button does nothing!");
                c.sendMessage("Please submit a bug report by entering /bug "+ButtonID+" (WHAT IS SHOULD DO)");
                c.sendMessage("Example /bug 152 This should make the player run");
                System.out.println("UNPROGRAMMED BUTTON "+ButtonID);
				break;
                          }
            break;

			case 130:
				Misc.println_debug("closing interface.");
				break;

			case 210:
				//Misc.println_debug("Loaded new area");
				break;

			/*case 73: //WHICH MORON USED THE ***TRADE PACKET*** FOR PKING DERPS GOING TO DERP
				try {
					if(pkEnabled) {
					
						try {
							int pIndex = c.inStream.readUnsignedWordBigEndian();
							getLevelForXP(playerXP[3]);
							Client p = (Client) PlayerHandler.players[pIndex];

							if(pIndex >= 2047) {
								sendMessage("[PK] Yeah, go attack yourself looser..");
								return;
							}
							if (absY < 3518)
							{
								sendMessage("[PK] Move to the wild or make your Y coordinate higher than 3552 to pk");
							}
							else if (absY >= 3518 && p.absY >= 3552)
							{
								if(p.isActive && p.currentHealth > 0) 
								{		
									if(distanceTo(p) >= 3)
										return;						
									if(playerLevel[2] <= 50) 
									{
										p.hitDiff = 1 + Misc.random(10);//(playerLevel[2] / Math.random());//(1 + Misc.random(5)));
									}
									else
										p.hitDiff = 1 + Misc.random(10);//(playerLevel[2] / Math.random());//(1 + Misc.random(5)));
									p.currentHealth -= p.hitDiff;
									
									if(p.currentHealth < 0) 
									{
										p.currentHealth = 0;
									}

									if(p.currentHealth == 0) 
									{
										p.teleportToX = 3222;
										p.teleportToY = 3218;
										p.pEmote = 15;
										p.pWalk = 13;
										sendMessage("This player has already been killed");
										//sendMessage("[PK] You have killed " + p.playerName + " (" + p.hitDiff + " damage)");
										p.sendMessage("[PK] " + playerName + " killed you (" + p.hitDiff + " damage)");
										p.currentHealth = p.maxHealth;
										p.hitDiff = 0;	
										p.setSkillLevel(3, p.maxHealth, 101333);//p.setSkillLevel(3, p.maxHealth, level);
										updateRequired = true; appearanceUpdateRequired = true;
									}										
									p.playerLevel[3] = p.currentHealth;
									p.updateRequired = true;
									p.setSkillLevel(3, p.maxHealth, 101333);//p.setSkillLevel(3, p.currentHealth, level);
									p.updateRequired = true;
									p.hitUpdateRequired = true;			
									setAnimation(422);					
								}
								else
								{
									sendMessage("[PK] " + p.playerName + " is not available to fight");
								}
							}
						}
						catch(Exception e) {
							Misc.println_debug(e.toString());
						}
					
					} else {
						sendMessage("[PK] PKing is currently disabled");
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}*/

			case 95:		// update chat
				c.Tradecompete = c.inStream.readUnsignedByte();
                c.Privatechat = c.inStream.readUnsignedByte();
                c.Publicchat = c.inStream.readUnsignedByte();
				for(int i1 = 0; i1 < PlayerHandler.maxPlayers; i1++)
					if(!(PlayerHandler.players[i1] == null) && PlayerHandler.players[i1].isActive)
						PlayerHandler.players[i1].pmupdate(c.playerId, 1);
				break;

			case 188:		// add friend
				long friendtoadd = c.inStream.readQWord();
				for(int i1 = 0; i1 < 200; i1++)
					if(c.friends[i1] == 0)
					{
                        c.friends[i1] = friendtoadd;
						for(int i2 = 1; i2 < PlayerHandler.maxPlayers; i2++)
							if(PlayerHandler.players[i2] != null && PlayerHandler.players[i2].isActive && Misc.playerNameToInt64(PlayerHandler.players[i2].playerName) == friendtoadd)
							{
								if(PlayerHandler.players[i2].Privatechat == 0 || (PlayerHandler.players[i2].Privatechat == 1 && PlayerHandler.players[i2].isinpm(Misc.playerNameToInt64(c.playerName))))
                                    c.loadpm(friendtoadd, 1);
								break;

							}
						break;
					}
				break;

			case 215:		// remove friend
				long friendtorem = c.inStream.readQWord();
				for(int i1 = 0; i1 < 200; i1++)
					if(c.friends[i1] == friendtorem)
					{
                        c.friends[i1] = 0;
						break;
					}
				break;

			case 133:		// add ignore
				long igtoadd = c.inStream.readQWord();
				for(int i10 = 0; i10 < 200; i10++)
					if(c.ignores[i10] == 0)
					{
                        c.ignores[i10] = igtoadd;
						break;
					}
				break;
			case 74:		// remove ignore
				long igtorem = c.inStream.readQWord();
				for(int i11 = 0; i11 < 200; i11++)
					if(c.ignores[i11] == igtorem)
					{
                        c.ignores[i11] = 0;
						break;
					}
				break;
			case 126:		//pm message
				long friendtosend = c.inStream.readQWord();
				byte pmchatText[] = new byte[100];
				int pmchatTextSize = (byte)(c.packetSize-8);
				c.inStream.readBytes(pmchatText, pmchatTextSize, 0);
				for(int i1 = 0; i1 < 200; i1++)
					if(c.friends[i1] == friendtosend)
					{
						boolean pmsent = false;
						for(int i2 = 1; i2 < PlayerHandler.maxPlayers; i2++)
						{
							if(PlayerHandler.players[i2] != null && PlayerHandler.players[i2].isActive && Misc.playerNameToInt64(PlayerHandler.players[i2].playerName) == friendtosend)
							{
								if(((PlayerHandler.players[i2].Privatechat == 0 || (PlayerHandler.players[i2].Privatechat == 1) || c.playerRights > 1 ) && PlayerHandler.players[i2].isinpm(Misc.playerNameToInt64(c.playerName))))
								{
									PlayerHandler.players[i2].sendpm(Misc.playerNameToInt64(c.playerName), c.playerRights, pmchatText, pmchatTextSize);
									pmsent = true;
								}
								break;
							}
						}
						if(!pmsent) c.sendMessage("player currently not availible");
						break;
					}
				break;
			
			case 45:
				break;

			case 17: //Second click NPCS -- Most cases = store
		/*	sendMessage("Welcome to general store");
			//setAnimation(0x372);
			actionName = "trade";
   outStream.createFrame(97);
   outStream.writeWord(3824); // interface id
   outStream.createFrameVarSizeWord(34); // init item adding
   outStream.writeWord(3900); // shops
   outStream.writeByte(1); // slot
   outStream.writeWord(4152); // item
outStream.writeByte(4); //amount
   outStream.writeByte(2); // slot
   outStream.writeWord(4154); // item
outStream.writeByte(16); //amount
   outStream.endFrameVarSizeWord();*/
                break;
			// the following Ids are the reason why AR-type cheats are hopeless to make...
			// basically they're just there to make reversing harder
			case 226:
			case 78:
			case 148:
			case 183:
			case 230:
			case 136:
			case 189:
			case 152:
			case 200:
			case 85:
			case 165:
			case 238:
			case 150:
			case 36:
			case 246:
			case 77:
				break;

/*
~~~~~~~~~~~END CASES~~~~~~~~~~~~~~~~
~~~~~~~~~~~END CASES~~~~~~~~~~~~~~~~
*/

			// any packets we might have missed
			default:
			interfaceID = c.inStream.readUnsignedWordA();
			Misc.HexToInt(c.inStream.buffer, 0, c.packetSize);
                c.sendMessage("You're client sent an unhandled packet. This occurs when you are using cheat client");
                c.sendMessage("We are letting you know during testing so if you arn't cheating tell us to fix this");
				Misc.println_debug("Unhandled packet ["+c.packetType+", InterFaceId: " +interfaceID+", size="+c.packetSize+"]: "+Misc.Hex(c.inStream.buffer, 0, c.packetSize));
				//Misc.println_debug("Action Button: "+actionButtonId1);
				break;
		}
	}

}