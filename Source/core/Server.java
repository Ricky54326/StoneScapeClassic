package core;
//  This file is free software; you can redistribute it and/or modify it under
//  the terms of the GNU General Public License version 2, 1991 as published by
//  the Free Software Foundation.

//  This program is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
//  details.

//  A copy of the GNU General Public License can be found at:
//    http://www.gnu.org/licenses/gpl.html

import stonekingdom.Config;
import stonekingdom.NPCHandler;
import stonekingdom.PlayerHandler;
public class Server implements Runnable {

	public Server()
	{
		// the current way of controlling the server at runtime and a great debugging/testing tool
		//jserv js = new jserv(this);
		//js.start();

	}

	/**
	 * The task scheduler.
	 */
	private static final TaskScheduler scheduler = new TaskScheduler();

	/**
	 * Gets the task scheduler.
	 * @return The task scheduler.
	 */
	public static TaskScheduler getTaskScheduler() {
		return scheduler;
	}
	
	public static void main(java.lang.String args[])
	{
		clientHandler = new Server();
		(new Thread(clientHandler)).start();			// launch server listener

		playerHandler = new PlayerHandler();
		npcHandler = new NPCHandler();
		ioThread = new IOThread();
		(new Thread(ioThread)).start();

		scheduler.schedule(new Task() {
			@Override
			protected void execute() {
			// could do game updating stuff in here...
			// maybe do all the major stuff here in a big loop and just do the packet
			// sending/receiving in the client subthreads. The actual packet forming code
			// will reside within here and all created packets are then relayed by the subthreads.
			// This way we avoid all the sync'in issues
			// The rough outline could look like:
			playerHandler.process();			// updates all player related stuff
			npcHandler.process();
			// doObjects()
			// doWhatever()
			}
		});
	}
	public static Server clientHandler = null;			// handles all the clients
	public static java.net.ServerSocket clientListener = null;
	public static boolean shutdownServer = false;		// set this to true in order to shut down and kill the server
	public static boolean shutdownClientHandler;			// signals ClientHandler to shut down
	
	public static int serverlistenerPort = Config.PORT_NUMBER; //43594=default

	public static PlayerHandler playerHandler = null;
	public static NPCHandler npcHandler = null;
	public static IOThread ioThread = null;


	public void run() {
		// setup the listener
		try {
			shutdownClientHandler = false;
			clientListener = new java.net.ServerSocket(serverlistenerPort, 1, null);
			Misc.println("Starting server on "+clientListener.getInetAddress().getHostAddress()+":" + clientListener.getLocalPort());
			while(true) {
				java.net.Socket s = clientListener.accept();
				s.setTcpNoDelay(true);
				String connectingHost = s.getInetAddress().getHostName();
				if(!IOHostList.has(connectingHost,3)) {
					Misc.println("ClientHandler: Accepted from "+connectingHost+":"+s.getPort());
					playerHandler.newPlayerClient(s, connectingHost);
				} else {
					Misc.println("ClientHandler: Rejected from "+connectingHost+":"+s.getPort());
					s.close();
				}
			}
		} catch(java.io.IOException ioe) {
			if(!shutdownClientHandler) {
				Misc.println("Error: Unable to startup listener on "+serverlistenerPort+" - port already in use?");
			} else {
				Misc.println("ClientHandler was shut down.");
			}
		}
	}

	public void killServer()
	{
		try {
			shutdownClientHandler = true;
			if(clientListener != null) clientListener.close();
			clientListener = null;
		} catch(java.lang.Exception __ex) {
			__ex.printStackTrace();
		}
	}
}

