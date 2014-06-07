package stonekingdom;
//randoms.java made for random events and originally coded by aaron1654
   
   
public class Randoms    
   
   {
	public static int SRandomCash[]  = {1,540,2104,301,3001,51062,1000000};  //the cash values
    public static int SRandomTime[]  = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};   //the random times
	 public static int SRandomTime2[]  = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};      //the second other random times

    public static int SRandomNumberOfTimes[]  = {1,2,3};
		
		
public static int RandomCash()
	{
		return SRandomCash[(int)(Math.random()*SRandomCash.length)];
	}
	
		public static int RandomTime()
	
	{
		return SRandomTime[(int)(Math.random()*SRandomTime.length)];
	}
			public static int RandomTime2()
	
	{
		return SRandomTime2[(int)(Math.random()*SRandomTime2.length)];
	}

	
	
			public static int RandomNumberOfTimes()
	
	{
		return SRandomNumberOfTimes[(int)(Math.random()*SRandomNumberOfTimes.length)];
	}
   }