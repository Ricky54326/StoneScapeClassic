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

// a collection of item methods
//floogerscapes item.java it has alot more items in it (randomphat gem and barrows item ect)
import java.io.*;
public class Item {

	//Few item types for equipping
           public static int randomPhat[] = {1038,1040,1042,1044,1046,1048,2422,962,962};
		public static int randomthing[] = {4566,4079,1050};
		   public static int randomCake[] = {1891,1899,1897};
           public static int randomGem[] = {1625,1623,1627,1629,1621,1631,6571,4671,4673,1601};
	public static int randomBarrowItem[] = {4716,4718,4716,4718,4720,4722,4708,4710,4712,4714,4753,4755,4757,4759,4745,4747,4749,4751,4732,4734,4736,4738,4726,4728,4730,4724};
	public static int capes[] = {6111,6570,6568,1007,1019,1021,1023,1027,1029,1031,1052,2412,2413,2414,4304,4315,4317,4319,4321,4323,4325,4327,4329,4331,4333,4335,4337,4339,4341,4343,4345,4347,4349,4351,4353,4355,4357,4359,4361,4363,4365,4367,4369,4371,4373,4375,4377,4379,4381,4383,4385,4387,4389,4391,4393,4395,4397,4399,4401,4403,4405,4407,4409,4411,4413,4514,4516,6070,6568,6570};
	public static int boots[] = {6619,6061,6106,88,89,626,628,630,632,634,1061,1837,1846,2577,2579,2894,2904,2914,2924,2934,3061,3105,3107,3791,4097,4107,4117,4119,4121,4123,4125,4127,4129,4131,4310,5062,5063,5064,5345,5557,6069,6106,6143,6145,6147,6328};
	public static int gloves[] = {24911065,2487,2489,3060,1495,775,777,778,6708,1059,1063,1065,1580,2487,2489,2491,2902,2912,2922,2932,2942,3060,3799,4095,4105,4115,4308,5556,6068,6110,6149,6151,6153};
	public static int shields[] = {6631,1171,1173,1175,1177,1179,1181,1183,1185,1187,1189,1191,1193,1195,1197,1199,1201,1540,2589,2597,2603,2611,2621,2629,2659,2667,2675,2890,3122,3488,3758,3839,3840,3841,3842,3843,3844,4072,4156,4224,4225,4226,4227,4228,4229,4230,4231,4232,4233,4234,4302,4507,4512,6215,6217,6219,6221,6223,6225,6227,6229,6231,6233,6235,6237,6239,6241,6243,6245,6247,6249,6251,6253,6255,6257,6259,6261,6263,6265,6267,6269,6271,6273,6275,6277,6279,6524,6633};
	public static int hats[] = {2633,2635,2637,4502,4732,4753,4611,6188,6182,4511,4056,4071,4724,2639,2641,2643,2665,6109,5525,5527,5529,5531,5533,5535,5537,5539,5541,5543,5545,5547,5549,5551,74,579,656,658,660,662,664,740,1017,1037,1038,1040,1042,1044,1046,1048,1050,1053,1055,1057,1137,1139,1141,1143,1145,1147,1149,1151,1153,1155,1157,1159,1161,1163,1165,1506,1949,2422,2581,2587,2595,2605,2613,2619,2627,2631,2651,2657,2673,2900,2910,2920,2930,2940,2978,2979,2980,2981,2982,2983,2984,2985,2986,2987,2988,2989,2990,2991,2992,2993,2994,2995,3057,3385,3486,3748,3749,3751,3753,3755,3797,4041,4042,4071,4089,4099,4109,4164,4302,4506,4511,4513,4515,4551,4567,4708,4716,4724,4745,4753,4856,4857,4858,4859,4880,4881,4882,4883,4904,4905,4906,4907,4952,4953,4954,4955,4976,4977,4978,4979,5013,5014,5554,5574,6109,6128,6131,6137,6182,6188,6335,6337,6339,6345,6355,6365,6375,6621};
	public static int amulets[] = {6585,86,87,295,421,552,589,1478,1692,1694,1696,1698,1700,1702,1704,1706,1708,1710,1712,1725,1727,1729,1731,4021,4081,4250,4677,6040,6041,6208};
	public static int arrows[] = {78,598,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,942,2532,2533,2534,2535,2536,2537,2538,2539,2540,2541,2866,4160,4172,4173,4174,4175,4740,5616,5617,5618,5619,5620,5621,5622,5623,5624,5625,5626,5627,6061,6062};
	public static int rings[] = {773,1635,1637,1639,1641,1643,1645,2550,2552,2554,2556,2558,2560,2562,2564,2566,2568,2570,2572,4202,4657,6465};
	public static int body[] = {1035,540,5553,4757,1833,1835,6388,6384,2501,2499,4111,4101,4091,6186,6184,6180,3058,4509,4504,4069,4728,4736,4712,6107,2661,3140,1101,1103,1105,1107,1109,1111,1113,1115,1117,1119,1121,1123,1125,1127,1129,1131,1133,1135,2499,2501,2503,2583,2591,2599,2607,2615,2623,2653,2669,3387,3481,4712,4720,4728,4749,4892,4893,4894,4895,4916,4917,4918,4919,4964,4965,4966,4967,6107,6133,6322};
	public static int legs[] = {1033,6404,6396,542,538,1033,5555,4759,6386,6390,2497,2495,2493,1099,4113,4103,4093,6187,6185,6181,3059,4510,4505,4070,6108,538,542,548,1011,1013,1015,1067,1069,1071,1073,1075,1077,1079,1081,1083,1085,1087,1089,1091,1093,2585,2593,2601,2609,2617,2625,2655,2663,2671,3059,3389,3472,3473,3474,3475,3476,3477,3478,3479,3480,3483,3485,3795,4087,4585,4712,4714,4722,4730,4738,4751,4759,4874,4875,4876,4877,4898,4899,4900,4901,4922,4923,4924,4925,4946,4947,4948,4949,4970,4971,4972,4973,4994,4995,4996,4997,5048,5050,5052,5576,6107,6130,6187,6390,6625};
	public static int platebody[] = {544,1035,6617,1035,540,5553,4757,1833,1835,6388,6384,2501,2499,4111,4101,4091,6186,6184,6180,3058,4509,4504,4069,4728,4736,4712,6107,2661,3140,1115,1117,1119,1121,1123,1125,1127,2583,2591,2599,2607,2615,2623,2653,2669,3481,4720,4728,4749,2661,6615};
	public static int fullHelm[] = {6623,4732,4753,6188,4511,4056,4071,4724,6109,2665,1153,1155,1157,1159,1161,1163,1165,2587,2595,2605,2613,2619,2627,2657,2673,3486};
	public static int fullMask[] = {4732,5554,4753,4611,6188,3507,4511,4056,4071,4724,2665,6109,1053,1055,1057};
	//All other IDs are weapons (I hope)
	public static int randomNPCDrop[] = {995,668,438,441,441,441,1237,1337,1337,5016,4580,4315,4319,4347,4348,4363};

	public static boolean[] itemStackable = new boolean[7000];
	public static boolean[] itemIsNote = new boolean[7000];
	static {
		int counter = 0;
		int c;
		
		try {
			FileInputStream dataIn = new FileInputStream(new File("data/stackable.dat"));
			while ((c = dataIn.read()) != -1)
			{
				if (c == 0)
					itemStackable[counter] = false;
				else
					itemStackable[counter] = true;
				counter++;
			}
			dataIn.close();
		} catch (IOException e) {
			System.out.println("Critical error while loading stackabledata! Trace:");
			e.printStackTrace();
		}

		counter = 0;
		
		try {
			FileInputStream dataIn = new FileInputStream(new File("data/notes.dat"));
			while ((c = dataIn.read()) != -1)
			{
				if (c == 0)
					itemIsNote[counter] = true;
				else
					itemIsNote[counter] = false;
				counter++;
			}
			dataIn.close();
		} catch (IOException e) {
			System.out.println("Critical error while loading notedata! Trace:");
			e.printStackTrace();
		}

	}

	public static int randomNPCDrop()
	{
		return randomNPCDrop[(int)(Math.random()*randomNPCDrop.length)];
	}
		public static int randomBarrowItem()
	{
	return randomBarrowItem[(int)(Math.random()*randomBarrowItem.length)];
	}
	
           public static int randomPhat()
	{
		return randomPhat[(int)(Math.random()*randomPhat.length)];
	}

	public static int randomCape()
	{
		return capes[(int)(Math.random()*capes.length)];
	}
	public static int randomBoots()
	{
		return boots[(int)(Math.random()*boots.length)];
	}
	public static int randomGloves()
	{
		return gloves[(int)(Math.random()*gloves.length)];
	}
	public static int randomShield()
	{
		return shields[(int)(Math.random()*shields.length)];
	}
	public static int randomHat()
	{
		return hats[(int)(Math.random()*hats.length)];
	}
	public static int randomAmulet()
	{
		return amulets[(int)(Math.random()*amulets.length)];
	}
	public static int randomArrows()
	{
		return arrows[(int)(Math.random()*arrows.length)];
	}
	public static int randomRing()
	{
		return rings[(int)(Math.random()*rings.length)];
	}
	public static int randomBody()
	{
		return body[(int)(Math.random()*body.length)];
	}
	public static int randomLegs()
	{
		return legs[(int)(Math.random()*legs.length)];
	}
           public static int randomGem()
	{
		return randomGem[(int)(Math.random()*randomGem.length)];
	}
	public static int randomCake()
	{
		return randomCake[(int)(Math.random()*randomNPCDrop.length)];
	}




	public static boolean isPlate(int itemID)
	{
		for (int i=0; i<platebody.length; i++)
			if (platebody[i] == itemID)
				return true;
		return false;
	}

	public static boolean isFullHelm(int itemID)
	{
		for (int i=0; i<fullHelm.length; i++)
			if (fullHelm[i] == itemID)
				return true;
		return false;
	}

	public static boolean isFullMask(int itemID)
	{
		for (int i=0; i<fullMask.length; i++)
			if (fullMask[i] == itemID)
				return true;
		return false;
	}
}