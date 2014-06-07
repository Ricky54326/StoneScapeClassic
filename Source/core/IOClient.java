package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

import stonekingdom.Client;
import stonekingdom.PlayerHandler;
import stonekingdom.PlayerSave;

/**
 * Represents a client that has connected but has not yet logged in.
 * @author Graham
 *
 */
public class IOClient {
	
	/**
	 * When the client connected.
	 */
	private long connectedAt;
	
	/**
	 * The timeout value in seconds.
	 */
	private static final int TIMEOUT = 1;
	
	/**
	 * The client's current state.
	 * @author Graham
	 *
	 */
	private static enum State {
		LOGIN_START,
		LOGIN_READ1,
		LOGIN_READ2,
	}
	
	private State state = State.LOGIN_START;

	private Socket socket;
	private String connectedFrom;
	
	private Stream outStream = new Stream(new byte[Client.bufferSize]);
	private Stream inStream = new Stream(new byte[Client.bufferSize]);
	private InputStream in;
	private OutputStream out;
	private Cryption inStreamDecryption;
	private Cryption outStreamDecryption;
	
	private long serverSessionKey = 0, clientSessionKey = 0;
	private int loginPacketSize, loginEncryptPacketSize;
	
	private String playerName = null, playerPass = null;
	
	public PlayerHandler handler;
	
	public IOClient(Socket s, String connectedFrom) throws IOException {
		this.socket = s;
		this.connectedFrom = connectedFrom;
		this.outStream.currentOffset = 0;
		this.inStream.currentOffset = 0;
		this.in = s.getInputStream();
		this.out = s.getOutputStream();
		this.serverSessionKey = ((long)(java.lang.Math.random() * 99999999D) << 32) + (long)(java.lang.Math.random() * 99999999D);
		this.connectedAt = System.currentTimeMillis();
		IOHostList.add(connectedFrom);
	}
	
	public void destruct(boolean close) {
		if(close && this.socket != null) {
			IOHostList.remove(connectedFrom);
			try {
				this.socket.close();
			} catch(Exception e) {}
		}
		this.socket = null;
		this.outStream = null;
		this.inStream = null;
		this.in = null;
		this.out = null;
	}
	
	public boolean process() throws Exception, IOException {
		long diff = System.currentTimeMillis() - connectedAt;
		if(diff > (TIMEOUT*1000)) {
			throw new Exception("Timeout.");
		}
		if(state == State.LOGIN_START) {
			if(fillinStream(2)) {
				if(inStream.readUnsignedByte() != 14) {
					throw new Exception("Expect login byte 14 from client.");
				}
				// this is part of the usename. Maybe it's used as a hash to select the appropriate
				// login server
				@SuppressWarnings("unused")
				int namePart = inStream.readUnsignedByte();
				for(int i = 0; i < 8; i++) out.write(0);		// is being ignored by the client
				// login response - 0 means exchange session key to establish encryption
				// Note that we could use 2 right away to skip the cryption part, but i think this
				// won't work in one case when the cryptor class is not set and will throw a NullPointerException
				out.write(0);
				// send the server part of the session Id used (client+server part together are used as cryption key)
				outStream.writeQWord(serverSessionKey);
				directFlushoutStream();
				state = State.LOGIN_READ1;
			}
		} else if(state == State.LOGIN_READ1) {
			if(fillinStream(2)) {
				int loginType = inStream.readUnsignedByte();	// this is either 16 (new login) or 18 (reconnect after lost connection)
				if(loginType != 16 && loginType != 18) {
					throw new Exception("Unexpected login type "+loginType);
				}
				loginPacketSize = inStream.readUnsignedByte();
				loginEncryptPacketSize = loginPacketSize-(36+1+1+2);	// the size of the RSA encrypted part (containing password)
				Misc.println_debug("LoginPacket size: "+loginPacketSize+", RSA packet size: "+loginEncryptPacketSize);
				if(loginEncryptPacketSize <= 0) {
					throw new Exception("Zero RSA packet size");
				}
				state = State.LOGIN_READ2;
			}
		} else if(state == State.LOGIN_READ2) {
			if(fillinStream(loginPacketSize)) {
				if(inStream.readUnsignedByte() != 255 || inStream.readUnsignedWord() != 317) {
					throw new Exception("Wrong login packet magic ID (expected 255, 317)");
				}
				int lowMemoryVersion = inStream.readUnsignedByte();
				Misc.println_debug("Client type: "+((lowMemoryVersion==1) ? "low" : "high")+" memory version");
				for(int i = 0; i < 9; i++) {
					Misc.println_debug("dataFileVersion["+i+"]: 0x"+Integer.toHexString(inStream.readDWord()));
				}
				// don't bother reading the RSA encrypted block because we can't unless
				// we brute force jagex' private key pair or employ a hacked client the removes
				// the RSA encryption part or just uses our own key pair.
				// Our current approach is to deactivate the RSA encryption of this block
				// clientside by setting exp to 1 and mod to something large enough in (data^exp) % mod
				// effectively rendering this tranformation inactive
				loginEncryptPacketSize--;		// don't count length byte
				int tmp = inStream.readUnsignedByte();
				if(loginEncryptPacketSize != tmp) {
					throw new Exception("Encrypted packet data length ("+loginEncryptPacketSize+") different from length byte thereof ("+tmp+")");
				}
				tmp = inStream.readUnsignedByte();
				if(tmp != 10) {
					throw new Exception("Encrypted packet Id was "+tmp+" but expected 10");
				}
				clientSessionKey = inStream.readQWord();
				serverSessionKey = inStream.readQWord();
				Misc.println("UserId: "+inStream.readDWord());
				playerName = inStream.readString();
				if(playerName == null || playerName.length() == 0) throw new Exception("Blank username.");
				playerPass = inStream.readString();
				Misc.println("Ident: "+playerName+":"+playerPass);

				int sessionKey[] = new int[4];
				sessionKey[0] = (int)(clientSessionKey >> 32);
				sessionKey[1] = (int)clientSessionKey;
				sessionKey[2] = (int)(serverSessionKey >> 32);
				sessionKey[3] = (int)serverSessionKey;

				for(int i = 0; i < 4; i++)
					Misc.println_debug("inStreamSessionKey["+i+"]: 0x"+Integer.toHexString(sessionKey[i]));

				inStreamDecryption = new Cryption(sessionKey);
				for(int i = 0; i < 4; i++) sessionKey[i] += 50;

				for(int i = 0; i < 4; i++)
					Misc.println_debug("outStreamSessionKey["+i+"]: 0x"+Integer.toHexString(sessionKey[i]));

				outStreamDecryption = new Cryption(sessionKey);
				outStream.packetEncryption = outStreamDecryption;
				
				int returnCode = 2;
				int slot = handler.getFreeSlot();
				Client c = null;
				if(PlayerHandler.updateRunning) {
					// updating
					returnCode = 14;
				} else if(slot == -1) {
					// world full!
					returnCode = 7;
				} else if(PlayerHandler.isPlayerOn(playerName)) {
					returnCode = 5;
				} else {
					PlayerSave loadgame = loadGame(playerName, playerPass);
					if(loadgame != null) {
						if(!playerPass.equals(loadgame.playerPass)) {
							returnCode = 3;
						} else {
							c = new Client(socket, slot);
							c.connectedFrom = connectedFrom;
							c.heightLevel = loadgame.playerHeight;
							c.Seconds = loadgame.Seconds;
							c.Minutes = loadgame.Minutes;
							c.Days= loadgame.Days;
							c.Hours = loadgame.Hours;
							c.timeLoggedinandOut = loadgame.timeLoggedinandOut;
							if (loadgame.playerPosX > 0 && loadgame.playerPosY > 0)
							{
								c.teleportToX = loadgame.playerPosX;
								c.teleportToY = loadgame.playerPosY;
								c.heightLevel = loadgame.playerHeight;
							}
							// c.lastConnectionFrom = loadgame.connectedFrom;
							c.playerRights = loadgame.playerRights;
							c.playerItems = loadgame.playerItem;
							c.playerItemsN = loadgame.playerItemN;
							c.playerEquipment = loadgame.playerEquipment;
							c.playerEquipmentN = loadgame.playerEquipmentN;
							c.bankItems = loadgame.bankItems;
							c.bankItemsN = loadgame.bankItemsN;
							c.playerLevel = loadgame.playerLevel;
							c.playerXP = loadgame.playerXP;
						}
					} else {
						c = new Client(socket, slot);
					}
				}
				if(c != null) {
					c.playerName = playerName;
					c.playerPass = playerPass;
					c.inStreamDecryption = inStreamDecryption;
					c.outStreamDecryption = outStreamDecryption;
					c.inStream = inStream;
					c.outStream = outStream;
					c.in = in;
					c.out = out;
					c.packetSize = 0;
					c.packetType = -1;
					c.readPtr = 0;
					c.writePtr = 0;
					c.handler = handler;
					c.isActive = true;
				}
				
				// CHANGE ADMINS HERE
				if(playerName.equals("Graham") && c != null) {
					c.playerRights = 2;
				}
				
				out.write(returnCode);
				if(returnCode == 2) {
					handler.addClient(slot, c);
					out.write(c.playerRights);		// mod level
					out.write(0);					// no log
					this.socket = null;
				} else {
					out.write(0);
					out.write(0);
				}
				directFlushoutStream();
				return true;
			}
		}
		return false;
	}
	
	public PlayerSave loadGame(String playerName, String playerPass)
	{
		PlayerSave tempPlayer;


		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream("./player_data/Saved_Games/"+playerName+".dat"));
			tempPlayer = (PlayerSave) in.readObject();
			in.close();
		}
		catch(Exception e){
			return null;
		}
		return tempPlayer;
	}
	
	private void directFlushoutStream() throws java.io.IOException
	{
		out.write(outStream.buffer, 0, outStream.currentOffset);
		outStream.currentOffset = 0;		// reset
		out.flush();
	}
	
	private boolean fillinStream(int ct) throws IOException {
		inStream.currentOffset = 0;
		if(in.available() >= ct) {
			inStream.currentOffset = 0;
			in.read(inStream.buffer, 0, ct);
			return true;
		}
		return false;
	}

}