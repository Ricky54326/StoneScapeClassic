package stonekingdom;
//  This file is free software; you can redistribute it and/or modify it under
//  the terms of the GNU General Public License version 2, 1991 as published by
//  the Free Software Foundation.

//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
//  details.

//  A copy of the GNU General Public License can be found at:
//    http://www.gnu.org/licenses/gpl.html

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import core.IOClient;
import core.Misc;
import core.Stream;

public class PlayerHandler{

	// Remark: the player structures are just a temporary solution for now
	// Later we will avoid looping through all the players for each player
	// by making use of a hash table maybe based on map regions (8x8 granularity should be ok
	private List<IOClient> ioClients = new ArrayList<IOClient>();
	private Queue<IOClient> add = new ConcurrentLinkedQueue<IOClient>();
	private Queue<IOClient> remove = new ConcurrentLinkedQueue<IOClient>();
	private Queue<IOClient> removeNoClose = new ConcurrentLinkedQueue<IOClient>();
	public static final int maxPlayers = 2000;
	public static Player players[] = new Player[maxPlayers];
	public int playerSlotSearchStart = 1;			// where we start searching at when adding a new player
	public static String kickNick = "";
	public static boolean kickAllPlayers=false;
	public static String messageToAll = "";
	public static int playerCount=0;
	public static String playersCurrentlyOn[] = new String[maxPlayers];
        public static boolean updateAnnounced;
	public static boolean updateRunning;
	public static int updateSeconds;
	public static long updateStartTime;
	public int lastchatid = 1;

	public PlayerHandler()	{
		for(int i = 0; i < maxPlayers; i++) {
			players[i] = null;
		}
	}

	public void processIOClients() {
		synchronized(add) {
			while(true) {
				IOClient toAdd = add.poll();
				if(toAdd == null) break;
				ioClients.add(toAdd);
			}
		}
		synchronized(ioClients) {
			for(IOClient ioc : ioClients) {
				try {
					if(ioc.process()) {
						removeNoClose.add(ioc);
					}

				} catch (Exception e) {
					System.err.println(e.getMessage());
					remove.add(ioc);
				}
			}
		}
		synchronized(remove) {
			while(true) {
				IOClient toRemove = remove.poll();
				if(toRemove == null) break;
				toRemove.destruct(true);
				ioClients.remove(toRemove);
			}
		}
		synchronized(removeNoClose) {
			while(true) {
				IOClient toRemove = removeNoClose.poll();
				if(toRemove == null) break;
				toRemove.destruct(false);
				ioClients.remove(toRemove);
			}
		}
	}
	
	public void newPlayerClient(java.net.Socket s, String connectedFrom) { //New client connected
			IOClient ioc;
			try {
				ioc = new IOClient(s, connectedFrom);
				ioc.handler = this;
			} catch(Exception e) { return; }
			synchronized(add) {
				add.add(ioc);
			}
		}
 
	public int getFreeSlot() {//Determines number of players online to see if there is an opening
		int slot = -1, i = 1;
		do {
			if(players[i] == null) {
				slot = i;
				break;
			}
			i++;
			if(i >= maxPlayers) i = 0;		// wrap around
		} while(i <= maxPlayers);
		return slot;
	}
	
	
	public void addClient(int slot, Client newClient) {
		if(newClient == null) return;
		players[slot] = newClient;
		//players[slot].connectedFrom=connectedFrom;

		// start at next slot when issuing the next search to speed it up
		playerSlotSearchStart = slot + 1;
		if(playerSlotSearchStart > maxPlayers) playerSlotSearchStart = 1;
	}
	
	public void destruct()
	{
		for(int i = 0; i < maxPlayers; i++) {
			if(players[i] == null) continue;
			players[i].destruct();
			players[i] = null;
		}
	}

	public static int getPlayerCount()
	{
		return playerCount;
	}

	public void updatePlayerNames(){
		playerCount=0;
		for(int i = 0; i < maxPlayers; i++) {
			if(players[i] != null)
			{
				playersCurrentlyOn[i] = players[i].playerName;
				playerCount++;
			}
			else
				playersCurrentlyOn[i] = "";
		}
	}

	public static boolean isPlayerOn(String playerName)
	{
		for(int i = 0; i < maxPlayers; i++) {
			if(playersCurrentlyOn[i] != null){
				if(playersCurrentlyOn[i].equalsIgnoreCase(playerName)) return true;
			}
		}
		return false;
	}
	public static int getPlayerID(String playerName) {
		for(int i = 0; i < maxPlayers; i++) {
			if(playersCurrentlyOn[i] != null) {
				if(playersCurrentlyOn[i].equalsIgnoreCase(playerName)) return i;
			}
		}
		return -1;
	}

	public void process()
	{
		updatePlayerNames();
		if (messageToAll.length() > 0)
		{
			int msgTo=1;
			do {
				if(players[msgTo] != null) {
					players[msgTo].globalMessage=messageToAll;
				}
				msgTo++;
			} while(msgTo < maxPlayers);
			messageToAll="";
		}
		if (kickAllPlayers)
		{
			int kickID = 1;
			do {
				if(players[kickID] != null) {
					players[kickID].isKicked = true;
				}
				kickID++;
			} while(kickID < maxPlayers);
			kickAllPlayers = false;
		}

		// at first, parse all the incoming data
		// this has to be seperated from the outgoing part because we have to keep all the player data
		// static, so each client gets exactly the same and not the one clients are ahead in time
		// than the other ones.
		for(int i = 0; i < maxPlayers; i++) {
			if(players[i] == null || !players[i].isActive) continue;

			players[i].actionAmount--;

			players[i].preProcessing();
			while(players[i].process());
			players[i].postProcessing();

			players[i].getNextPlayerMovement();

			if(players[i].playerName.equalsIgnoreCase(kickNick))
			{
				players[i].kick();
				kickNick="";
			}
			if(players[i].disconnected) {
				if(saveGame(players[i])){ 
					System.out.println("Game saved for player "+players[i].playerName); 
					} else { 
						System.out.println("Could not save for "+players[i].playerName); 
					};
				removePlayer(players[i]);
				players[i] = null;
			}
		}

		// loop through all players and do the updating stuff
		for(int i = 0; i < maxPlayers; i++) {
			if(players[i] == null || !players[i].isActive) continue;

			if(players[i].disconnected) {
				if(saveGame(players[i])){ System.out.println("Game saved for player "+players[i].playerName); } else { System.out.println("Could not save for "+players[i].playerName); };
				removePlayer(players[i]);
				players[i] = null;
			}
			else {
				if(!players[i].initialized) {
					players[i].initialize();
					players[i].initialized = true;
				}
				else players[i].update();
			}
		}

if (updateRunning && !updateAnnounced)
		{
			updateAnnounced = true;
		}

		if (updateRunning && System.currentTimeMillis() - updateStartTime > (updateSeconds*1000))
		{
			kickAllPlayers = true;
		}

		// post processing
		for(int i = 0; i < maxPlayers; i++) {
			if(players[i] == null || !players[i].isActive) continue;

			players[i].clearUpdateFlags();
		}
	}

	private Stream updateBlock = new Stream(new byte[10000]);
	// should actually be moved to client.java because it's very client specific
	public void updatePlayer(Player plr, Stream str) {
		synchronized(plr) {
			updateBlock.currentOffset = 0;
			if(updateRunning && !updateAnnounced) {
				str.createFrame(114);
				str.writeWordBigEndian(updateSeconds*50/30);
			}
			plr.updateThisPlayerMovement(str);		
			plr.appendPlayerUpdateBlock(updateBlock);
			str.writeBits(8, plr.playerListSize);
			int size = plr.playerListSize;
			if (size > 255)
				size = 255;
			plr.playerListSize = 0;	
			for(int i = 0; i < size; i++) {			
				if(!plr.didTeleport && !plr.playerList[i].didTeleport && plr.withinDistance(plr.playerList[i])) {
					plr.playerList[i].updatePlayerMovement(str);
					plr.playerList[i].appendPlayerUpdateBlock(updateBlock);
					plr.playerList[plr.playerListSize++] = plr.playerList[i];
				} else {
					int id = plr.playerList[i].playerId;
					plr.playerInListBitmap[id>>3] &= ~(1 << (id&7));
					str.writeBits(1, 1);
					str.writeBits(2, 3);
				}
			}
		
			for(int i = 0; i < Config.MAX_PLAYERS; i++) {
				if(players[i] == null || !players[i].isActive || players[i] == plr)
					continue;
				int id = players[i].playerId;
				if((plr.playerInListBitmap[id>>3]&(1 << (id&7))) != 0)
					continue;	
				if(!plr.withinDistance(players[i])) 
					continue;		
				plr.addNewPlayer(players[i], str, updateBlock);
			}
	
			if(updateBlock.currentOffset > 0) {
				str.writeBits(11, 2047);	
				str.finishBitAccess();				
				str.writeBytes(updateBlock.buffer, updateBlock.currentOffset, 0);
			}
			else str.finishBitAccess();
	
			str.endFrameVarSizeWord();
		}
	}

	private void removePlayer(Player plr)
	{
		if(plr.Privatechat != 2)
			for(int i = 1; i < maxPlayers; i++)
			{
				if(players[i] == null || !players[i].isActive) continue;
				players[i].pmupdate(plr.playerId, 0);
			}
		// anything can be done here like unlinking this player structure from any of the other existing structures
		plr.destruct();
	}

	public static boolean saveGame(Player plr)
	{
		PlayerSave tempSave = new PlayerSave(plr);
		try
		{
			Misc.print_debug("Saving");
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./player_data/Saved_Games/"+tempSave.playerName+".dat"));
			out.writeObject((PlayerSave)tempSave);
			out.close();
		}
		catch(Exception e){
			Misc.print_debug("Save failed");
			return false;
		}
		return true;
	}
}
