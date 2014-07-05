/**
 * 
 */
package stonekingdom;

import core.Misc;
import core.Server;
import core.Task;






/**
 * @author David
 *
 */
public class Combat {
	/*public static void engageCombat(final String attacker, final String defender, final int attackIndex, final int defendIndex) { //player, npc | npc, player | player, player | npc, npc
		final boolean isAttackerPlayer = false;
		final boolean isDefenderPlayer = false;
		Server.getTaskScheduler().schedule(new Task(1, true) {

			protected void execute() {
				
				int enemyHP = getInformation ("HP", isDefenderPlayer, defendIndex);
				if (enemyHP > 0) {
					if (attacker.equalsIgnoreCase("player")) {
						isAttackerPlayer = true;
					}
					if(defender.equalsIgnoreCase("player")) {
						isDefenderPlayer = true;
					}						
					int attackX = getInformation ("X", isAttackerPlayer, attackIndex);
					int attackY = getInformation ("Y", isAttackerPlayer, attackIndex);
					int attackH = getInformation ("H", isAttackerPlayer, attackIndex);
					int attackHP = getInformation ("HP", isAttackerPlayer, attackIndex);
					int enemyX = getInformation ("X", isDefenderPlayer, defendIndex);
					int enemyY = getInformation ("Y", isDefenderPlayer, defendIndex);
					int enemyH = getInformation ("H", isDefenderPlayer, defendIndex);
					
					if(attackH == enemyH) {
						sendMessage ("Your info: X: "+attackX+" Y: "+attackY+" H: "+attackH+" HP: "+attackHP, isAttackerPlayer, attackIndex);
						sendMessage ("Enemy info: X: "+enemyX+" Y: "+enemyY+" H: "+enemyH+" HP: "+enemyHP+" INDEX:"+defendIndex, isAttackerPlayer, attackIndex);
						setAnimation (isAttackerPlayer, isDefenderPlayer, attackIndex, defendIndex);
						dealDamage (isAttackerPlayer, isDefenderPlayer, attackIndex, defendIndex);
					} else {
						sendMessage ("Your enemy is not on the same height level", isAttackerPlayer, attackIndex);
					}
				} else {
					PlayerHandler.messageToAll = "BANG!";
					stop();
				}
			}
		});
		
	}*/
	public static void dealDamage (boolean isAttackerPlayer, boolean isDefenderPlayer, int attackIndex, int defendIndex) {
		if(isAttackerPlayer && isDefenderPlayer) { //PVP
			
		} else if (isAttackerPlayer && isDefenderPlayer == false){
			if (PlayerHandler.players[attackIndex] != null) {
				Client c = (Client)PlayerHandler.players[attackIndex];
				int hitDiff = Misc.random(c.playerMaxHit);
				int enemyHP = getInformation ("HP", isDefenderPlayer, defendIndex);
				if (NPCHandler.npcs[c.attackNPCID].IsDead == true) {
					c.ResetAttackNPC();
					Misc.println_debug("CODE FAILURE");
				} else {
					c.actionAmount++;
					if ((enemyHP - hitDiff) < 0) {
						hitDiff = enemyHP;
					}
				NPCHandler.npcs[c.attackNPCID].TurnNPCTo(c.absX, c.absY);
				NPCHandler.npcs[c.attackNPCID].hitDiff = hitDiff;
				NPCHandler.npcs[c.attackNPCID].Killing[c.playerId] += hitDiff;
				NPCHandler.npcs[c.attackNPCID].updateRequired = true;
				NPCHandler.npcs[c.attackNPCID].hitUpdateRequired = true;
				double TotalExp = 0;
				if (c.fightType != 3) {
					TotalExp = (double)(4 * hitDiff);
					TotalExp = (double)(TotalExp * Config.combatEXPRate);
					//addSkillXP((int)(TotalExp), SkillID);
				} else {
					TotalExp = (double)(1.33 * hitDiff);
					TotalExp = (double)(TotalExp * Config.combatEXPRate);
					c.addSkillXP((int)(TotalExp), "Attack");
					c.addSkillXP((int)(TotalExp), "Defense");
					c.addSkillXP((int)(TotalExp), "Strength");
				}
				TotalExp = (double)(1.33 * hitDiff);
				TotalExp = (double)(TotalExp * Config.combatEXPRate);
				c.addSkillXP((int)(TotalExp), "HP");
				c.actionTimer = 7;
				}
			}
		}
	}
	public static void setAnimation (boolean isAttackerPlayer, boolean isDefenderPlayer, int attackIndex, int defendIndex) {
		if(isAttackerPlayer){
			if (PlayerHandler.players[attackIndex] != null) {
				Client c = (Client)PlayerHandler.players[attackIndex];
				switch(c.playerWeapon) {
				case 3:
					c.startAnimation(422);
					break;
				default:
					c.sendMessage("Weapon ID "+c.playerWeapon);
					break;
				}
			}
		} else {
			//npc animation here
		}
		if(isDefenderPlayer){
			if (PlayerHandler.players[defendIndex] != null) {
				Client c = (Client)PlayerHandler.players[defendIndex];
				c.setAnimation(424);
			}
		} else {
			if(NPCHandler.npcs[defendIndex] != null){
				NPCHandler.npcs[defendIndex].animNumber = 424;
				NPCHandler.npcs[defendIndex].animUpdateRequired = true;
				NPCHandler.npcs[defendIndex].updateRequired = true;
			}
			//npc animation here
		}
	}
	
	public static void sendMessage (String msg, boolean attackerPlayer, int index) {
		if (PlayerHandler.players[index] != null) {
			Client c = (Client)PlayerHandler.players[index];
			if(attackerPlayer){
				c.sendMessage(msg);
			}
		}
	}
	
	public static int getInformation (String request, boolean isAttackingPlayer, int index) {
		//Loads all of the information needed from both entities 
		Player p = PlayerHandler.players[index];
		NPC n = NPCHandler.npcs[index];
		if(request == "X") {
			if(isAttackingPlayer && p != null) {
				return p.absX;
			} else if (isAttackingPlayer == false && n != null) {
				return n.absX;
			}
		}
		if(request == "Y") {
			if(isAttackingPlayer && p != null) {
				return p.absY;
			} else if (isAttackingPlayer == false && n != null) {
				return n.absY;
			}
		}
		if(request == "H") {
			if(isAttackingPlayer && p != null) {
				return p.heightLevel;
			} else if (isAttackingPlayer == false && n != null) {
				return n.heightLevel;
			}
		}
		if(request == "HP") {
			if(isAttackingPlayer && p != null) {
				return p.playerLevel[p.playerHitpoints];
			} else if (isAttackingPlayer == false && n != null) {
				return n.HP;
			}
		}
		return -1;
	}
	public static void CalculateMaxHit(Client c) {
		double MaxHit = 0;
		int StrBonus = c.playerBonus[10]; //Strength Bonus
		int Strength = c.playerLevel[c.playerStrength]; //Strength
		if (c.fightType == 1 || c.fightType == 4) { //Accurate & Defensive
			MaxHit += (double)(1.05 + (double)((double)(StrBonus * Strength) * 0.00175));
		} else if (c.fightType == 2) { //Aggresive
			MaxHit += (double)(1.35 + (double)((double)(StrBonus) * 0.00525));
		} else if (c.fightType == 3) { //Controlled
			MaxHit += (double)(1.15 + (double)((double)(StrBonus) * 0.00175));
		}
		MaxHit += (double)(Strength * 0.1);
		/*if (StrPotion == 1) { //Strength Potion
			MaxHit += (double)(Strength * 0.0014);
		} else if (StrPotion == 2) { //Super Strength Potion
			MaxHit += (double)(Strength * 0.0205);
		}
		if (StrPrayer == 1) { //Burst Of Strength
			MaxHit += (double)(Strength * 0.005);
		} else if (StrPrayer == 2) { //Super Human Strength
			MaxHit += (double)(Strength * 0.01);
		} else if (StrPrayer == 3) { //Ultimate Strength
			MaxHit += (double)(Strength * 0.015);
		}*/
		c.playerMaxHit = (int)Math.floor(MaxHit);
	}


	/*public static void CalculateMaxHit(Client c) {
		double MaxHit = 0;
		int StrBonus = c.playerBonus[10]; //Strength Bonus
		int Strength = c.playerLevel[c.playerStrength]; //Strength
		if (c.fightType == 1 || c.fightType == 4) { //Accurate & Defensive
			MaxHit += (double)(1.05 + (double)((double)(StrBonus * Strength) * 0.00175));
		} else if (c.fightType == 2) { //Aggresive
			MaxHit += (double)(1.35 + (double)((double)(StrBonus) * 0.00525));
		} else if (c.fightType == 3) { //Controlled
			MaxHit += (double)(1.15 + (double)((double)(StrBonus) * 0.00175));
		}
		MaxHit += (double)(Strength * 0.1);
		/*if (StrPotion == 1) { //Strength Potion
			MaxHit += (double)(Strength * 0.0014);
		} else if (StrPotion == 2) { //Super Strength Potion
			MaxHit += (double)(Strength * 0.0205);
		}
		if (StrPrayer == 1) { //Burst Of Strength
			MaxHit += (double)(Strength * 0.005);
		} else if (StrPrayer == 2) { //Super Human Strength
			MaxHit += (double)(Strength * 0.01);
		} else if (StrPrayer == 3) { //Ultimate Strength
			MaxHit += (double)(Strength * 0.015);
		}
		c.playerMaxHit = (int)Math.floor(MaxHit);
	}*/
	
	public static boolean AttackNPC(Client c) {
		//c.sendMessage("ATTACK ID:"+c.attackNPCID);
			int EnemyX = NPCHandler.npcs[c.attackNPCID].absX;
			int EnemyY = NPCHandler.npcs[c.attackNPCID].absY;
			int EnemyHP = NPCHandler.npcs[c.attackNPCID].HP;
			//c.sendMessage("ATTACK ID:"+c.attackNPCID+" Enemy X: "+EnemyX+" Enemy Y: "+EnemyY);
			int hitDiff = Misc.random(c.playerMaxHit);
			if (c.goodDistance(EnemyX, EnemyY, c.absX, c.absY, 1) == true) {
				if (c.actionTimer == 0) {
					if (NPCHandler.npcs[c.attackNPCID].IsDead == true) {
						c.ResetAttackNPC();
					} else {
						c.actionAmount++;
						setAnimation (true, false, c.playerId,c.attackNPCID);
						if ((EnemyHP - hitDiff) < 0) {
							hitDiff = EnemyHP;
						}
						NPCHandler.npcs[c.attackNPCID].TurnNPCTo(c.absX, c.absY);
						NPCHandler.npcs[c.attackNPCID].StartKilling = c.playerId;
						NPCHandler.AttackPlayer(c.attackNPCID);
						NPCHandler.npcs[c.attackNPCID].hitDiff = hitDiff;
						NPCHandler.npcs[c.attackNPCID].Killing[c.playerId] += hitDiff;
						NPCHandler.npcs[c.attackNPCID].updateRequired = true;
						NPCHandler.npcs[c.attackNPCID].hitUpdateRequired = true;
						double TotalExp = 0;
						if (c.fightType != 3) {
							TotalExp = (double)(4 * hitDiff);
							TotalExp = (double)(TotalExp * Config.combatEXPRate);
							//addSkillXP((int)(TotalExp), SkillID);
						} else {
							TotalExp = (double)(1.33 * hitDiff);
							TotalExp = (double)(TotalExp * Config.combatEXPRate);
							c.addSkillXP((int)(TotalExp), "Attack");
							c.addSkillXP((int)(TotalExp), "Defense");
							c.addSkillXP((int)(TotalExp), "Strength");
						}
						TotalExp = (double)(1.33 * hitDiff);
						TotalExp = (double)(TotalExp * Config.combatEXPRate);
						c.addSkillXP((int)(TotalExp), "HP");
						c.actionTimer = 7;
					}
					return true;
				}
			}
			return false;
		}
	}
