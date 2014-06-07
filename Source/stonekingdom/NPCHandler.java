package stonekingdom;

import core.Misc;
import core.Stream;
//  This file is free software; you can redistribute it and/or modify it under
//  the terms of the GNU General Public License version 2, 1991 as published by
//  the Free Software Foundation.

//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
//  details.

//  A copy of the GNU General Public License can be found at:
//    http://www.gnu.org/licenses/gpl.html

public class NPCHandler {

	public static final int maxNPCs = 400;
	public NPC npcs[] = new NPC[maxNPCs];
	public int npcs_idx = 0;

	
        public NPCHandler(){
		for(int i = 0; i < maxNPCs; i++) {
			npcs[i] = null;
		}

	}

	public void updateNPCs(Player plr, Stream str, boolean firstNPCpacket)
        {
		if(firstNPCpacket)
		{
			str.createFrameVarSizeWord(65);
			str.initBitAccess();
			str.writeBits(8, 0);
			for(int i = 0; i < maxNPCs; i++)
				if(npcs[i] != null)
					if(withinDistance(plr, npcs[i]) && npcs[i] != null)
					{
						createNPC(plr, npcs[i], str, i);
					}
			str.finishBitAccess();
			str.endFrameVarSizeWord();
		} else {
			str.createFrameVarSizeWord(65);
			str.initBitAccess();
			str.writeBits(8, plr.existingNPCs);
			for(int i = 0; i < maxNPCs; i++)
				if(npcs[i] != null)
					if(plr.npcArray[i] != null){
						if(withinDistance(plr, npcs[i])){
							int dir = npcs[i].dir;
							if(dir != -1){
								str.writeBits(1, 1);
								str.writeBits(2, 1);
								str.writeBits(3, Misc.xlateDirectionToClient[dir]);
								if(npcs[i].requiresUpdate)
									str.writeBits(1, 1);
								else
									str.writeBits(1, 0);
								plr.npcArray[i].absX = npcs[i].absX;
								plr.npcArray[i].absY = npcs[i].absX;
							} else {
								if(npcs[i].requiresUpdate){
									str.writeBits(1, 1);
									str.writeBits(2, 0);
								} else
									str.writeBits(1, 0);
							}
						} else {
							// should really remove here but there's some
							// weird bugs in npc pos when recreating so...
							str.writeBits(1, 0);
							//str.writeBits(1, 1);
							//str.writeBits(2, 3);
							//plr.removeNPC(npcs[i].idx);
						}

					} else {
						if(withinDistance(plr, npcs[i]))
							createNPC(plr, npcs[i], str, i);
					}

			boolean updateBlockFollows = false;
			for(int k = 0; k <= plr.npcArray_idx; k++){
				if(plr.npcArray[k] != null)
					if(npcs[plr.npcArray[k].idx] != null)
						if(npcs[plr.npcArray[k].idx].requiresUpdate && withinDistance(plr, npcs[plr.npcArray[k].idx])){
							updateBlockFollows = true;
							break;
						}
			}

			if(updateBlockFollows){

				str.writeBits(14, 16383);
				str.finishBitAccess();

				for(int j = 0; j < plr.npcArray.length; j++){
					if(plr.npcArray[j] != null){
						NPC npc = npcs[plr.npcArray[j].idx];
						if(npc.requiresUpdate && withinDistance(plr, npc)){
							int updateMask = 0;
							if(npc.animUpdate)
								updateMask |= 0x10;
							if(npc.chatUpdate)
								updateMask |= 1;

							str.writeByte(updateMask);

							if(npc.animUpdate)
								npc.appendAnimUpdate(str);
							if(npc.chatUpdate)
								npc.appendChatUpdate(str);

						}
					}
				}
			} else {
				str.finishBitAccess();
			}

			str.endFrameVarSizeWord();

		}
	}

	public boolean withinDistance(Player plr, NPC npc)
	{
		if(plr.heightLevel != npc.heightLevel) return false;
		int deltaX = npc.absX-plr.absX, deltaY = npc.absY-plr.absY;
		return deltaX <= 15 && deltaX >= -16 && deltaY <= 15 && deltaY >= -16;
	}

	public void createNPC(Player plr, NPC npc, Stream str, int npcnum)
	{
		int deltaX = npc.absX-plr.absX;
		if(deltaX < 0)
			deltaX += 32;
		int deltaY = npc.absY-plr.absY;
		if(deltaY < 0)
			deltaY += 32;
		str.writeBits(14, npcnum);
		str.writeBits(5, deltaY);
		str.writeBits(5, deltaX);
		str.writeBits(1, 1);
		str.writeBits(12, npc.ID);
		str.writeBits(1, 0);

		plr.addNPC(npc);
	}


	public void randomizeWalkingQueue(int npcnum)
	{
	if(npcs[npcnum] != null && Client.enableWalking)

               if(npcs[npcnum] != null){
			int randX = (int)(Math.random() * 10D) - 5;
			int randY = (int)(Math.random() * 10D) - 5;
			//System.out.println(randX+", "+randY);
			if(randX < 0)
				for(int j = -1; j >= randX; j--)
					npcs[npcnum].addToQueue(npcs[npcnum].absX+j, npcs[npcnum].qPosY);
			if(randX > 0)
				for(int j = 1; j <= randX; j++)
					npcs[npcnum].addToQueue(npcs[npcnum].absX+j, npcs[npcnum].qPosY);

			if(randY < 0)
				for(int j = -1; j >= randY; j--)
					npcs[npcnum].addToQueue(npcs[npcnum].qPosX, npcs[npcnum].absY+j);
			if(randY > 0)
				for(int j = 1; j <= randY; j++)
					npcs[npcnum].addToQueue(npcs[npcnum].qPosX, npcs[npcnum].absY+j);
			npcs[npcnum].randomQueue = false;
		}
	}


	 public void process()
	{
		for(int i = 0; i < maxNPCs; i++)
			if(npcs[i] != null){
				if(npcs[i].qRelPos == 0 && npcs[i].cyclesSS > 20){
					randomizeWalkingQueue(i);
					npcs[i].cyclesSS = 0;
				} else
					npcs[i].cyclesSS++;

				npcs[i].clearFlags();
				npcs[i].getNextDir(); 
			}
	}
}
