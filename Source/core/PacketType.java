package core;

import stonekingdom.Client;


	
public interface PacketType {
	public void processPacket(Client c, int packetType, int packetSize);
}

