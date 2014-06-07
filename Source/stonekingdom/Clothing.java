package stonekingdom;
//floogerscape2.0's clothing.java split into parts like all the other .java files to make it
//easier to edit for the noobs
public class Clothing 
{

	//the body pieces very helpfull for making new clothing commands
	public static int pMHead[]  = {0,1,2,3,4,5,6,7,8,9};
	public static int pMBeard[] = {10,11,12,13,14,15,16,17};
	public static int pMTorso[] = {18,19,20,21,22,23,24,25};
	public static int pMArms[]  = {26,27,28,29,30,31,32};
	public static int pMHands[] = {33,34,35};
	public static int pMLegs[]  = {36,37,38,39,40,41};
	public static int pMFeet[]  = {42,43,44};
	public static int pFHead[]  = {45,46,47,48,49,50,51,52,53,54,55};
	public static int pFTorso[] = {56,57,58,59,60};
	public static int pFArms[]  = {61,62,63,64,65,66};
	public static int pFHands[] = {67,68,69};
	public static int pFLegs[]  = {70,71,72,73,74,75,76,77,78};
	public static int pFFeet[]  = {79,80,81};


	public static int randomMHead()
	{
		return pMHead[(int)(Math.random()*pMHead.length)];
	}
	public static int randomMBeard()
	{
		return pMBeard[(int)(Math.random()*pMBeard.length)];
	}
	public static int randomMTorso()
	{
		return pMTorso[(int)(Math.random()*pMTorso.length)];
	}
	public static int randomMArms()
	{
		return pMArms[(int)(Math.random()*pMArms.length)];
	}
	public static int randomMHands()
	{
		return pMHands[(int)(Math.random()*pMHands.length)];
	}
	public static int randomMLegs()
	{
		return pMLegs[(int)(Math.random()*pMLegs.length)];
	}
	public static int randomMFeet()
	{
		return pMFeet[(int)(Math.random()*pMFeet.length)];
	}
	public static int randomFHead()
	{
		return pFHead[(int)(Math.random()*pFHead.length)];
	}
	public static int randomFTorso()
	{
		return pFTorso[(int)(Math.random()*pFTorso.length)];
	}
	public static int randomFArms()
	{
		return pFArms[(int)(Math.random()*pFArms.length)];
	}
	public static int randomFHands()
	{
		return pFHands[(int)(Math.random()*pFHands.length)];
	}
	public static int randomFLegs()
	{
		return pFLegs[(int)(Math.random()*pFLegs.length)];
	}
	public static int randomFFeet()
	{
		return pFFeet[(int)(Math.random()*pFFeet.length)];
	}
}	