/**
 * 
 */
package stonekingdom;

/**
 * @author WhiteFang
 * @author David
 * @version 1.20
 * Originally ripped from WhiteScape, extremely modified and improved with modern infrastructure.
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import core.Misc;

public class ItemHandler {
	
	public static int DropItemCount = 0;
	public static int MaxDropItems = 100000;
	public static int MaxListedItems = 10000;
							//process() is called evry 500 ms
	public static int MaxDropShowDelay = 120; //120 * 500 = 60000 / 1000 = 60s
	public static int SDID = 90; //90 * 500 = 45000 / 1000 = 45s
					//SDID = Standard Drop Items Delay
	public static int[] DroppedItemsID = new int[MaxDropItems];
	public static int[] DroppedItemsX = new int[MaxDropItems];
	public static int[] DroppedItemsY = new int[MaxDropItems];
	public static int[] DroppedItemsN = new int[MaxDropItems];
	public static int[] DroppedItemsH = new int[MaxDropItems];
	public static int[] DroppedItemsDDelay = new int[MaxDropItems];
	public static int[] DroppedItemsSDelay = new int[MaxDropItems];
	public static int[] DroppedItemsDropper = new int[MaxDropItems];
	public static int[] DroppedItemsDeletecount = new int[MaxDropItems];
	public static boolean[] DroppedItemsAlwaysDrop = new boolean[MaxDropItems];
	public ItemList ItemList[] = new ItemList[MaxListedItems];
	public static int[] globalItemAmount = new int[5001];
	public static boolean[] globalItemStatic = new boolean[5001];
	public static int[] globalItemController = new int[5001];
	public static int[] globalItemID = new int[5001];
	public static boolean[] itemTaken = new boolean[5001];
	public static String[] playerTaken = new String[5001];
	public static int[] globalItemTicks = new int[5001];
	public static int[] globalItemX = new int[5001];
	public static int[] globalItemY = new int[5001];
	public static int[] globalItemH = new int[5001];
	public ItemHandler() {
		for (int i = 0; i <= 5000; i++) {
			globalItemController[i] = 0;
			globalItemID[i] = 0;
			globalItemX[i] = 0;
			globalItemY[i] = 0;
			globalItemH[i] = 0;
			globalItemAmount[i] = 0;
			globalItemTicks[i] = 0;
			globalItemStatic[i] = false;
			itemTaken[i] = false;
		}
		for (int i = 0; i < MaxDropItems; i++) {
			ResetItem(i);
		}
		for (int i = 0; i < MaxListedItems; i++) {
			ItemList[i] = null;
		}
		loadItemList("../data/item.cfg");
		loadDrops("../data/globaldrops.cfg");
	}

	public void process() {
		for(int i = 0; i < MaxDropItems; i++) {
			if (DroppedItemsID[i] > -1) {
				if (DroppedItemsDDelay[i] > -10) {
					DroppedItemsDDelay[i]--;
				}
				if (DroppedItemsSDelay[i] < (MaxDropShowDelay + 10)) {
					DroppedItemsSDelay[i]++;
				}
				if (DroppedItemsSDelay[i] >= MaxDropShowDelay && DroppedItemsAlwaysDrop[i] == false) {
					for (int j = 1; j < PlayerHandler.maxPlayers; j++) {
						if (PlayerHandler.players[j] != null) {
							PlayerHandler.players[j].MustDelete[i] = true;
						}
					}
				}
			}
		}
	}
	
	public static int typeToSlot (String type) { //Converts "Cape" to the slot that the game understands
		int Slot = -1;
		switch (type){
		case "Hat":
		case "Head": 
			Slot = 0;
			break;
		case "Necklace":
			Slot = 2;
			break;
		case "Cape":
			 Slot = 1;
			break;
		case "Arrows":
			Slot = 13;
			break;
		case "Chest":
			Slot = 4;
			break;
		case "Weapon":
			break;
		case "Shield":
			Slot = 5;
			break;
		case "Legs":
		case "Leg":
			Slot = 7;
			break;
		case "Gloves":
			Slot = 9;
			break;
		case "Ring":
			Slot = 12;
			break;
		}
		return Slot; //Will error out and cancel wearing the item
	}
	
	public static void addItem(int itemID, int itemX, int itemY, int itemH, int itemAmount, int itemController, boolean itemStatic) {
		for (int i = 0; i <= 5000; i++) { // Phate: Loop through all item
			// spots
			if (globalItemID[i] == 0) { // Phate: Found blank item spot
				globalItemController[i] = itemController;
				globalItemID[i] = itemID;
				globalItemX[i] = itemX;
				globalItemY[i] = itemY;
				globalItemH[i] = 0;
				globalItemAmount[i] = itemAmount;
				globalItemStatic[i] = itemStatic;
				globalItemTicks[i] = 0;
				
				if ((globalItemController[i] != -1) && (globalItemController[i] != 0))
					dropItem (globalItemID[i], globalItemX[i], globalItemY[i], globalItemH[i], globalItemAmount[i]);
				break;
			}
		}
	}
	public static int itemAmount(int itemID, int itemX, int itemY) {
		for (int i = 0; i <= 5000; i++) { // Phate: Loop through all item
			// spots
			if ((globalItemID[i] == itemID) && (globalItemX[i] == itemX) && (globalItemY[i] == itemY)) // Phate:
				// Found
				// item
				return globalItemAmount[i];
		}
		return 0; // Phate: Item doesnt exist
	}
	public static boolean itemExists(int itemID, int itemX, int itemY) {
		for (int i = 0; i <= 5000; i++) { // Phate: Loop through all item
			// spots
			if ((globalItemID[i] == itemID) && (globalItemX[i] == itemX) && (globalItemY[i] == itemY)) // Phate:
				// Found
				// item
				return true;
		}
		return false; // Phate: Item doesn't exist
	}
	public static void removeItem(int itemID, int itemX, int itemY, int itemAmount) {
		for (int i = 0; i <= 5000; i++) { // Phate: Loop through all item
			// spots
			if ((globalItemID[i] == itemID) && (globalItemX[i] == itemX) && (globalItemY[i] == itemY) && (globalItemAmount[i] == itemAmount)) {
				removeItemAll(globalItemID[i], globalItemX[i], globalItemY[i]);
				globalItemController[i] = 0;
				globalItemID[i] = 0;
				globalItemX[i] = 0;
				globalItemY[i] = 0;
				globalItemAmount[i] = 0;
				globalItemTicks[i] = 0;
				globalItemStatic[i] = false;
				itemTaken[i] = false;
				playerTaken[i] = "MISSINGNAME"+Misc.random(999999999);
				break;
			}
		}
	}
	public static void removeItemAll(int itemID, int itemX, int itemY) {
		// for (Player p : server.playerHandler.players) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Client person = (Client) p;
				if (person.playerName != null) {
					// Misc.println("distance to remove
					// "+person.distanceToPoint(itemX, itemY));
					if (person.distanceToPoint(itemX, itemY) <= 60) {
						person.removeGroundItem(itemX, itemY, itemID);
					}
				}
			}
		}
	}
	public static void resetItemDrop() {
		for (int i = 0; i <= 5000; i++) {
			globalItemID[i] = 0;
		}
	}

	public static void spawnItem(int itemID, int itemX, int itemY, int itemAmount, int playerFor) {
		Client person = (Client) PlayerHandler.players[playerFor];
		person.createGroundItem(itemID, itemX, itemY, itemAmount);
	}
	
	public static void dropItem (int itemID, int itemX, int itemY, int itemH, int itemAmount) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Client person = (Client) p;
				if(person.playerName != null) {
					if (person.distanceToPoint(itemX, itemY) <= 60 && itemH == person.heightLevel) {
						person.createGroundItem(itemID, itemX, itemY, itemAmount);
					}
				}
			}
		}
	}
	
	public static void createItemAll(int itemID, int itemX, int itemY, int itemAmount, int itemController) {
		// for (Player p : server.playerHandler.players) {
		for (Player p : PlayerHandler.players) {
			if (p != null) {
				Client person = (Client) p;
				if ((person.playerName != null) && !(person.playerId == itemController)) {
					// Misc.println("distance to create
					// "+person.distanceToPoint(itemX, itemY));
					if (person.distanceToPoint(itemX, itemY) <= 60) {
						person.createGroundItem(itemID, itemX, itemY, itemAmount);
					}
				}
			}
		}
	}
	public void ResetItem(int ArrayID) {
		DroppedItemsID[ArrayID] = -1;
		DroppedItemsX[ArrayID] = -1;
		DroppedItemsY[ArrayID] = -1;
		DroppedItemsN[ArrayID] = -1;
		DroppedItemsH[ArrayID] = -1;
		DroppedItemsDDelay[ArrayID] = -1;
		DroppedItemsSDelay[ArrayID] = 0;
		DroppedItemsDropper[ArrayID] = -1;
		DroppedItemsDeletecount[ArrayID] = 0;
		DroppedItemsAlwaysDrop[ArrayID] = false;
	}
	
	public boolean loadDrops(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		BufferedReader dropFile = null;
		try {
			dropFile = new BufferedReader(new FileReader("./"+FileName));
		} catch(FileNotFoundException fileex) {
			Misc.println(FileName+": file not found.");
			return false;
		}
		try {
			line = dropFile.readLine();
		} catch(IOException ioexception) {
			Misc.println(FileName+": error loading file.");
			try {
				dropFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		while(EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("drop")) {
					int id = Integer.parseInt(token3[0]);
					int x = Integer.parseInt(token3[1]);
					int y = Integer.parseInt(token3[2]);
					int amount = Integer.parseInt(token3[3]);
					//TODO: Make height work
					//int height = Integer.parseInt(token3[4]);
					for (int i = 0; i < 5000; i++) {
						createItemAll(id, x, y, amount, globalItemController[i]);
					}
				}
			} else {
				if (line.equals("[ENDOFDROPLIST]")) {
					try { dropFile.close(); } catch(IOException ioexception) { }
				}
			}
			try {
				line = dropFile.readLine();
			} catch(IOException ioexception1) { EndOfFile = true; }
		}
		try { dropFile.close(); } catch(IOException ioexception) { }
		return false;
	}

	public void newItemList(int ItemId, String ItemName, String ItemDescription, double ShopValue, double LowAlch, double HighAlch, int Bonuses[]) {
		// first, search for a free slot
		int slot = -1;
		for (int i = 0; i < MaxListedItems; i++) {
			if (ItemList[i] == null) {
				slot = i;
				break;
			}
		}

		if(slot == -1) return;		// no free slot found

		ItemList newItemList = new ItemList(ItemId);
		newItemList.itemName = ItemName;
		newItemList.itemDescription = ItemDescription;
		newItemList.ShopValue = ShopValue;
		newItemList.LowAlch = LowAlch;
		newItemList.HighAlch = HighAlch;
		newItemList.Bonuses = Bonuses;
		ItemList[slot] = newItemList;
	}

	public boolean loadItemList(String FileName) {
		String line = "";
		String token = "";
		String token2 = "";
		String token2_2 = "";
		String[] token3 = new String[10];
		boolean EndOfFile = false;
		BufferedReader itemList = null;
		try {
			itemList = new BufferedReader(new FileReader("./"+FileName));
		} catch(FileNotFoundException fileex) {
			Misc.println(FileName+": file not found.");
			return false;
		}
		try {
			line = itemList.readLine();
		} catch(IOException ioexception) {
			Misc.println(FileName+": error loading file.");
			try {
				itemList.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		while(EndOfFile == false && line != null) {
			line = line.trim();
			int spot = line.indexOf("=");
			if (spot > -1) {
				token = line.substring(0, spot);
				token = token.trim();
				token2 = line.substring(spot + 1);
				token2 = token2.trim();
				token2_2 = token2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token2_2 = token2_2.replaceAll("\t\t", "\t");
				token3 = token2_2.split("\t");
				if (token.equals("item")) {
					int[] Bonuses = new int[12];
					for (int i = 0; i < 12; i++) {
						if (token3[(6 + i)] != null) {
							Bonuses[i] = Integer.parseInt(token3[(6 + i)]);
						} else {
							break;
						}
					}
					newItemList(Integer.parseInt(token3[0]), token3[1].replaceAll("_", " "), token3[2].replaceAll("_", " "), Double.parseDouble(token3[4]), Double.parseDouble(token3[4]), Double.parseDouble(token3[6]), Bonuses);
				}
			} else {
				if (line.equals("[ENDOFITEMLIST]")) {
					try { itemList.close(); } catch(IOException ioexception) { }
				}
			}
			try {
				line = itemList.readLine();
			} catch(IOException ioexception1) { EndOfFile = true; }
		}
		try { itemList.close(); } catch(IOException ioexception) { }
		return false;
	}

}
