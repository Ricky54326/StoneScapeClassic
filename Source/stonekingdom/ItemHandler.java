/**
 * 
 */
package stonekingdom;

/**
 * @author David Hakki
 * @version 1.00
 */
public class ItemHandler {
	
	public static void wearItem (Client c,int itemID, String type) { //For NON-stacking items
		int targetSlot = typeToSlot (type); //Converts the type (like "Shield") to a slot the game understands
		c.playerEquipment[targetSlot]=itemID;
		c.playerEquipmentN[targetSlot]=1; //NONSTACKING
        c.updateRequired = true; c.appearanceUpdateRequired = true;
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
	
}
