package descent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class DamageSystem {
	
	public static void damagePlayerMelee(Player plattack, Player pldefend) {
		
		if(plattack.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
			
			if(System.currentTimeMillis() - ChampCooldowns.swordSwingCooldown.get(plattack) > (1000 * ChampList.swordSwingCooldown)) {
			
				int damage = ChampList.sword.baseDamage;
				
				ChampCooldowns.swordSwingCooldown.replace(plattack, System.currentTimeMillis());
				
				plattack.playSound(pldefend.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
				pldefend.playSound(pldefend.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			
				damagePlayer(plattack, pldefend, damage);
				
			}
		}
		if(plattack.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE) {
			
			if(System.currentTimeMillis() - ChampCooldowns.axeSwingCooldown.get(plattack) > (1000 * ChampList.axeSwingCooldown)) {
			
				double velocity = Math.abs(plattack.getVelocity().getX()) + Math.abs(plattack.getVelocity().getY()) + Math.abs(plattack.getVelocity().getZ());
				
				plattack.sendMessage(velocity + "");
				
				int damage;
				
				if(velocity > 1) {
					damage = ChampList.axe.baseDamage + ChampList.axeVelocityDamage;
					plattack.playSound(pldefend.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
				} else {
					damage = ChampList.axe.baseDamage;
					plattack.playSound(pldefend.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
				}
				
				ChampCooldowns.axeSwingCooldown.replace(plattack, System.currentTimeMillis());
				
				pldefend.playSound(pldefend.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			
				damagePlayer(plattack, pldefend, damage);
				
			}
		}
	}
	public static void damagePlayerProjectile(Player plattack, Player pldefend, Projectile p) {
		
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getMainScoreboard();
		
		if(b.getEntryTeam(plattack.getName()).getName() != "spec" && b.getEntryTeam(pldefend.getName()).getName() != "spec") {
			if(b.getEntryTeam(plattack.getName()).getName() != b.getEntryTeam(pldefend.getPlayer().getName()).getName()) {
			
				if(ChampList.playerChamp.get(plattack) == ChampList.knife) {
			
					int damage = ChampList.knife.baseDamage;
			
					plattack.playSound(plattack.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
					pldefend.playSound(pldefend.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			
					p.remove();
			
					damagePlayer(plattack, pldefend, damage);
		
				}
				if(ChampList.playerChamp.get(plattack) == ChampList.bow) {
					
					int damage = ChampList.bow.baseDamage;
					double distance = plattack.getLocation().distance(pldefend.getLocation());
					
					damage = damage + (int)distance;
					
					plattack.sendMessage(damage + "");
			
					plattack.playSound(plattack.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
					pldefend.playSound(pldefend.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			
					p.remove();
			
					damagePlayer(plattack, pldefend, damage);
		
				}
			}
		}
	}
	public static void damagePlayer(Player plattack, Player pldefend, int damage) {
		
		if(pldefend.isBlocking() == true) {
			
			int shieldHealth = ChampCooldowns.swordShieldHealth.get(pldefend);
		
			ChampCooldowns.swordShieldHealth.replace(pldefend, shieldHealth - damage);
			
			pldefend.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Shield Health: " + (shieldHealth - damage) + "/" + ChampList.swordShieldMaxHealth, ChatColor.YELLOW));
			
			if(ChampCooldowns.swordShieldHealth.get(pldefend) <= 0) {
				
				pldefend.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Shield Broken!", ChatColor.YELLOW));
				
				pldefend.getInventory().getItemInOffHand().setType(Material.BROWN_DYE);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
				    @Override
				    public void run() {
				    	pldefend.getInventory().getItemInOffHand().setType(Material.SHIELD);
				    	ChampCooldowns.swordShieldHealth.replace(pldefend, ChampList.swordShieldMaxHealth);
				    }
				}, 250L);
				
			}
			
		} else {
			
			if(pldefend.getLevel() <= damage) {
				
				playerKill(plattack, pldefend);
				
			} else {
				
				pldefend.setLevel(pldefend.getLevel() - damage);
				ChampConstructor cc = ChampList.playerChamp.get(pldefend);
				pldefend.setHealth(20 * ((double)pldefend.getLevel()/cc.maxHealth));
				
			}
		}	
	}
	public static void healPlayer(Player healer, Player healee, int heal) {
		
		if(healee.getLevel() + heal < ChampList.playerChamp.get(healee).maxHealth) {
			
			healee.setLevel(healee.getLevel() + heal);
			healee.setHealth(20 * ((double)healee.getLevel()/ChampList.playerChamp.get(healee).maxHealth));
			
		} else {
			
			healee.setLevel(ChampList.playerChamp.get(healee).maxHealth);
			healee.setHealth(20);
			
		}
			
	}
	@SuppressWarnings("deprecation")
	public static void playerKill(Player plattack, Player pldefend) {
		
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getMainScoreboard();
		
		ChatColor placolor = ChatColor.WHITE;
		ChatColor pldcolor = ChatColor.WHITE;
		
		if(b.getEntryTeam(plattack.getName()).getName() != "spec") {
			if(b.getEntryTeam(plattack.getName()).getName() == "blue")
				placolor = ChatColor.BLUE;
			if(b.getEntryTeam(plattack.getName()).getName() == "red")
				placolor = ChatColor.RED;
		}
		if(b.getEntryTeam(pldefend.getName()).getName() != "spec") {
			if(b.getEntryTeam(pldefend.getName()).getName() == "blue")
				pldcolor = ChatColor.BLUE;
			if(b.getEntryTeam(pldefend.getName()).getName() == "red")
				pldcolor = ChatColor.RED;
		}
		//Displays proper kill message.
		Bukkit.broadcastMessage(placolor + plattack.getName() + ChatColor.YELLOW + " eliminated " + pldcolor + pldefend.getName());
		
		pldefend.playSound(pldefend.getLocation(), Sound.ENTITY_GENERIC_DEATH, 1f, 1f);
		pldefend.setGameMode(GameMode.SPECTATOR);
		pldefend.setFlySpeed(0);
		pldefend.setSpectatorTarget(plattack);
		pldefend.sendTitle(ChatColor.YELLOW + "You Died!", "Respawning...");
		
		if(ChampList.playerChamp.get(pldefend.getPlayer()) == ChampList.sword) {
			
    		pldefend.getInventory().getItemInOffHand().setType(Material.SHIELD);
    		ChampCooldowns.swordShieldHealth.replace(pldefend, ChampList.swordShieldMaxHealth);
    		
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
		    @Override
		    public void run() {
				ChampConstructor cc = ChampList.playerChamp.get(pldefend);
				pldefend.setLevel(cc.maxHealth);
				pldefend.setHealth(20);
				pldefend.setGameMode(GameMode.SURVIVAL);
				pldefend.setFlySpeed(0.1f);
				if(b.getEntryTeam(pldefend.getName()).getName().equals("blue")) {
					pldefend.teleport(new Location(pldefend.getWorld(), Main.blueX, Main.blueY, Main.blueZ));
				} else if (b.getEntryTeam(pldefend.getName()).getName().equals("red")) {
					pldefend.teleport(new Location(pldefend.getWorld(), Main.redX, Main.redY, Main.redZ));
				}
		    }
		}, 180L);
	}
}
