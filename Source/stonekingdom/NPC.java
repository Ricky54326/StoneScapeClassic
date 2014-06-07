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
//the NPC.java coded by....some people of mythscape and other people

public class NPC {

	public int idx;
	public int ID;
	public int absX;
	public int absY;
	public int heightLevel;
	public int dir;

	public int walkingQueueSize = 20;
	public int[] walkingQueueX = new int[walkingQueueSize];
	public int[] walkingQueueY = new int[walkingQueueSize];
	public int qReadPtr;
	public int qWritePtr;

	public boolean randomQueue = false;

	public boolean requiresUpdate = false;
	public boolean chatUpdate = false;
	public boolean animUpdate = false;
	public boolean someUpdate = false;

	public String chatMessage;
	public int animNumber;
	public int animDelay = 0;

	public int qPosX;
	public int qPosY;

	public int cyclesSS = (int)(Math.random() * 20D);

	public int qRelPos = 0;

	NPC(int npcID, int startX, int startY, int height, int index)
	{
		idx = index;
		ID = npcID;
		absX = startX;
		absY = startY;
		heightLevel = height;
		qPosX = startX;
		qPosY = startY;
	}

	public void addToQueue(int x, int y)
	{
		int next = (qWritePtr+1) % walkingQueueSize;
		if(next == qWritePtr) return;
		walkingQueueX[qWritePtr] = x;
		walkingQueueY[qWritePtr] = y;
		qWritePtr = next;
		qPosX = x;
		qPosY = y;
		qRelPos++;
	}


	public void getNextDir()
	{
		if(qReadPtr == qWritePtr) { dir = -1; return; }
		do {
			dir = Misc.direction(absX, absY, walkingQueueX[qReadPtr], walkingQueueY[qReadPtr]);
			if(dir == -1) qReadPtr = (qReadPtr+1) % walkingQueueSize;
			else if((dir&1) != 0) {
				return;
			}
		} while(dir == -1 && qReadPtr != qWritePtr);
		if(dir == -1) return;
		dir >>= 1;
		absX += Misc.directionDeltaX[dir];
		absY += Misc.directionDeltaY[dir];
		qRelPos--;
	}

	public void appendChatUpdate(Stream str)
	{
		str.writeString(chatMessage);
	}

	public void appendAnimUpdate(Stream str)
	{
		str.writeWordBigEndian(animNumber);
		str.writeByte(animDelay);
	}

	public void clearFlags()      //mostly for  npc talking chat and anims
	{
		chatMessage = "";
		chatUpdate = false;
		animUpdate = false;
		animNumber = -1;
		animDelay = 0;
		someUpdate = false;
		requiresUpdate = false;
	}

}