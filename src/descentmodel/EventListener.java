package descentmodel;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class EventListener implements Listener {
	
	public static ScoreboardManager m = Bukkit.getScoreboardManager();
	public static Scoreboard b = m.getMainScoreboard();


	
	@EventHandler
	public static void playerDamageEvent(EntityDamageByEntityEvent event) {
		
		if(event.getEntity() instanceof Player)
			event.setCancelled(true);
		
		Champ plattack;
		Champ pldefend;
		Projectile projectile;
		//MELEE DAMAGE
		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			
			plattack = Champ.getChamp((Player)event.getDamager());
			pldefend = Champ.getChamp((Player)event.getEntity());
			
			if(b.getEntryTeam(pldefend.getName()).getName() != "spec")
				plattack.abilityMelee(pldefend);
			
		}
		//RANGED DAMAGE
		if(event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
			
			projectile = (Projectile) event.getDamager();
			plattack = Champ.getChamp(Bukkit.getPlayerExact(projectile.getCustomName()));
			pldefend = Champ.getChamp((Player)event.getEntity());
			
			if(b.getEntryTeam(pldefend.getName()).getName() != "spec")
				DamageSystem.damagePlayerProjectile(plattack, pldefend, projectile);
			
		}
	}

	@EventHandler
	public static void entityShootBowEvent(EntityShootBowEvent event) {
		
		if(event.getEntity() instanceof Player) {
			
			Player pl = (Player)event.getEntity();
			
			if(event.getForce() == 1.0f) {
				
				event.setCancelled(true);
				
				Arrow arrow = pl.getWorld().spawnArrow(pl.getEyeLocation(), pl.getLocation().getDirection(), 5, 0);
				
				arrow.setCritical(true);
				arrow.setCustomName(pl.getName());

				
			} else {
				event.setCancelled(true);
			}
		}
	}


	@EventHandler
	public static void playerInteractEvent(PlayerInteractEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			//LEFT CLICK ARROW
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
			
				if(System.currentTimeMillis() - ChampCooldowns.knifeSwingCooldown.get(event.getPlayer()) > (1000 * ChampList.knifeSwingCooldown)) {
				
					Arrow knife1 = event.getPlayer().getWorld().spawnArrow(new Location(event.getPlayer().getWorld(), event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + event.getPlayer().getEyeHeight(), event.getPlayer().getLocation().getZ()), event.getPlayer().getLocation().getDirection(), 3, 0);
					knife1.setCustomName(event.getPlayer().getName());	
					knife1.setBounce(false);
					event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.5f, 1f);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
					    @Override
					    public void run() {
					    	Arrow knife2 = event.getPlayer().getWorld().spawnArrow(new Location(event.getPlayer().getWorld(), event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + event.getPlayer().getEyeHeight(), event.getPlayer().getLocation().getZ()), event.getPlayer().getLocation().getDirection(), 3, 0);
					    	knife2.setCustomName(event.getPlayer().getName());
							knife2.setBounce(false);
							event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.5f, 1f);
					    }
					}, 3L);

					ChampCooldowns.knifeSwingCooldown.replace(event.getPlayer(), System.currentTimeMillis());
				
				}
			}
		
			//RIGHT CLICK GOLDEN AXE (LEAP)
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				if(System.currentTimeMillis() - ChampCooldowns.axeLeapCooldown.get(event.getPlayer()) > (1000 * ChampList.axeLeapCooldown)) {
				
					event.getPlayer().setVelocity(new Vector(event.getPlayer().getLocation().getDirection().getX()*ChampList.axeLeapStrengthHoriz, event.getPlayer().getLocation().getDirection().getY()*ChampList.axeLeapStrengthVert, event.getPlayer().getLocation().getDirection().getZ()*ChampList.axeLeapStrengthHoriz));
					ChampCooldowns.axeLeapCooldown.replace(event.getPlayer(), System.currentTimeMillis());
				
				}
			}
		
			//LEFT CLICK NETHERITE HOE (SHOOT)
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
				
				if(System.currentTimeMillis() - ChampCooldowns.gunShootCooldown.get(event.getPlayer()) > (1000 * ChampList.gunShootCooldown)) {
			
					event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f);
				
					Ray.playerRayCast(event.getPlayer(), 99, ChampList.gun.baseDamage);
					
					ChampCooldowns.gunShootCooldown.replace(event.getPlayer(), System.currentTimeMillis());
				
				}
			}
		
			//RIGHT CLICK NETHERITE HOE (HEAL)
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			
				if(System.currentTimeMillis() - ChampCooldowns.gunHealCooldown.get(event.getPlayer()) > (1000 * ChampList.gunHealCooldown)) {
				/*
					event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
				
					Collection<Entity> entities = event.getPlayer().getWorld().getNearbyEntities(event.getPlayer().getLocation(), 6, 6, 5);
					Entity[] entityArray = entities.toArray(new Entity[entities.size()]);
				
					for(int i = 0; i < entityArray.length; i++) {
					
						if(entityArray[i] instanceof Player) {
						
							Player pl = (Player)entityArray[i];
						
							if(b.getEntryTeam(entityArray[i].getName()).getName() != "spec") {
								if(b.getEntryTeam(pl.getName()).getName() == b.getEntryTeam(event.getPlayer().getName()).getName()) {
							
									DamageSystem.healPlayer(event.getPlayer(), pl, 60);
									event.getPlayer().getWorld().spawnParticle(Particle.HEART, pl.getEyeLocation(), 10, 0.5, 1, 0.5, 0);
							
								}
							}
						}
					}
				
					ChampCooldowns.gunHealCooldown.replace(event.getPlayer(), System.currentTimeMillis());
				*/
					
					ThrownPotion pot = event.getPlayer().launchProjectile(ThrownPotion.class);
					pot.setVelocity(event.getPlayer().getLocation().getDirection());
					pot.setCustomName(event.getPlayer().getName());
					
					ChampCooldowns.gunHealCooldown.replace(event.getPlayer(), System.currentTimeMillis());
					
				}
			
			}
			//RIGHT CLICK BLOCK WOODEN SWORD (TRAP)
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(System.currentTimeMillis() - ChampCooldowns.knifeTrapCooldown.get(event.getPlayer()) > (1000 * ChampList.knifeTrapCooldown)) {
					
					Location block = event.getClickedBlock().getLocation();
					Location trap = block;
					trap.setY(trap.getY() + 1);
					
					if(trap.getBlock().getType() == Material.AIR && event.getClickedBlock().getType() == Material.POLISHED_ANDESITE) {
						
						trap.getBlock().setType(Material.STONE_PRESSURE_PLATE);
						
						if(ChampCooldowns.knifeTrapLocation.containsKey(event.getPlayer()))
							event.getPlayer().getWorld().getBlockAt(ChampCooldowns.knifeTrapLocation.get(event.getPlayer())).setType(Material.AIR);
						else
							ChampCooldowns.knifeTrapLocation.put(event.getPlayer(), trap);
						
						ChampCooldowns.knifeTrapLocation.replace(event.getPlayer(), trap);
						
						ChampCooldowns.knifeTrapCooldown.replace(event.getPlayer(), System.currentTimeMillis());
						
					}
				}
			}
			//LEFT CLICK BOW (DISENGANGE)
			if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.BOW && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
				if(System.currentTimeMillis() - ChampCooldowns.bowPotCooldown.get(event.getPlayer()) > (1000 * ChampList.bowPotCooldown)) {
					ThrownPotion pot = event.getPlayer().launchProjectile(ThrownPotion.class);
					pot.setVelocity(event.getPlayer().getLocation().getDirection());
					pot.setCustomName(event.getPlayer().getName());
					
					ChampCooldowns.bowPotCooldown.replace(event.getPlayer(), System.currentTimeMillis());
				}
			}
			//HIT PRESSURE PLATE EVENT
			if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {
				
				event.setCancelled(true);
				event.getClickedBlock().setType(Material.AIR);

				event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 0.5f);
				
				DamageSystem.stunPlayer(event.getPlayer(), 40);
				
			}
			//PICK CHAMP SIGN EVENT
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				//PICK TEAM
				if(event.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
			
					Sign sign = (Sign) event.getClickedBlock().getState();
					
					if(sign.getLine(1).equals("[Blue]"))
						PlayerTeams.addToTeam(event.getPlayer(), "blue");
					if(sign.getLine(1).equals("[Red]"))
						PlayerTeams.addToTeam(event.getPlayer(), "red");

				}
				//BLUE TEAM
				if(event.getClickedBlock().getType() == Material.WARPED_WALL_SIGN) {
					
					Sign sign = (Sign) event.getClickedBlock().getState();
					
					if(b.getEntryTeam(event.getPlayer().getName()).getName() == "blue") {
						if(sign.getLine(1).equals("[Impaler]"))
							ChampList.setChamp(event.getPlayer(), ChampList.knife);
						if(sign.getLine(1).equals("[Beserker]"))
							ChampList.setChamp(event.getPlayer(), ChampList.axe);
						if(sign.getLine(1).equals("[Knight]"))
							ChampList.setChamp(event.getPlayer(), ChampList.sword);
						if(sign.getLine(1).equals("[Deputy]"))
							ChampList.setChamp(event.getPlayer(), ChampList.gun);
						if(sign.getLine(1).equals("[Archer]"))
							ChampList.setChamp(event.getPlayer(), ChampList.bow);
					}
				}
				//RED TEAM
				if(event.getClickedBlock().getType() == Material.CRIMSON_WALL_SIGN) {
					
					Sign sign = (Sign) event.getClickedBlock().getState();
					
					if(b.getEntryTeam(event.getPlayer().getName()).getName() == "red") {
						if(sign.getLine(1).equals("[Impaler]"))
							ChampList.setChamp(event.getPlayer(), ChampList.knife);
						if(sign.getLine(1).equals("[Beserker]"))
							ChampList.setChamp(event.getPlayer(), ChampList.axe);
						if(sign.getLine(1).equals("[Knight]"))
							ChampList.setChamp(event.getPlayer(), ChampList.sword);
						if(sign.getLine(1).equals("[Deputy]"))
							ChampList.setChamp(event.getPlayer(), ChampList.gun);
						if(sign.getLine(1).equals("[Archer]"))
							ChampList.setChamp(event.getPlayer(), ChampList.bow);
					}
				}
			}
		}
	}
	@EventHandler
	public static void playerJoinEvent(PlayerJoinEvent event) {
		
		ChampList.playerChamp.remove(event.getPlayer());
		event.getPlayer().getInventory().clear();
		event.getPlayer().setLevel(0);
		event.getPlayer().setHealth(20);
		event.getPlayer().setFoodLevel(5);
		event.getPlayer().setFlySpeed(0.1f);
		event.getPlayer().setWalkSpeed(0.2f);
		event.getPlayer().setGameMode(GameMode.SURVIVAL);
		event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(64);
		event.getPlayer().setAllowFlight(false);
		
		for(PotionEffect p : event.getPlayer().getActivePotionEffects())
			event.getPlayer().removePotionEffect(p.getType());
		
		ChampCooldowns.knifeSwingCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.knifeTrapCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.swordSwingCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.axeSwingCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.axeLeapCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.gunShootCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.gunHealCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.bowPotCooldown.put(event.getPlayer(), (long)0);
		
		PlayerTeams.addToTeam(event.getPlayer(), "spec");
		
		event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation().add(new Location(event.getPlayer().getWorld(), 0.5, 0, 0.5)));
		
	}
	@EventHandler
	public static void playerQuitEvent(PlayerQuitEvent event) {
		
		ChampCooldowns.knifeSwingCooldown.remove(event.getPlayer());
		ChampCooldowns.knifeTrapCooldown.remove(event.getPlayer());
		ChampCooldowns.swordSwingCooldown.remove(event.getPlayer());
		ChampCooldowns.axeSwingCooldown.remove(event.getPlayer());
		ChampCooldowns.axeLeapCooldown.remove(event.getPlayer());
		ChampCooldowns.gunShootCooldown.remove(event.getPlayer());
		ChampCooldowns.gunHealCooldown.remove(event.getPlayer());
		ChampCooldowns.bowPotCooldown.remove(event.getPlayer());
		
		if(ChampCooldowns.knifeTrapLocation.containsKey(event.getPlayer()))
			event.getPlayer().getWorld().getBlockAt(ChampCooldowns.knifeTrapLocation.get(event.getPlayer())).setType(Material.AIR);
		
	}
	@EventHandler
	public static void playerDeathEvent(PlayerDeathEvent event) {
		
		event.setDeathMessage("");
		
	}
	@EventHandler
	public static void playerRespawnEvent(PlayerRespawnEvent event) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
		    @Override
		    public void run() {
		        event.getPlayer().setFoodLevel(5);
		    }
		}, 1L);
		if(b.getEntryTeam(event.getPlayer().getName()).getName().equals("blue")) {
			event.setRespawnLocation(new Location(event.getPlayer().getWorld(), Main.blueX, Main.blueY, Main.blueZ));
		} else if (b.getEntryTeam(event.getPlayer().getName()).getName().equals("red")) {
			event.setRespawnLocation(new Location(event.getPlayer().getWorld(), Main.redX, Main.redY, Main.redZ));
		}
	}
	@EventHandler
	public static void damageEvent(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public static void blockBreakEvent(BlockBreakEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL && event.getBlock().getType() != Material.STONE_PRESSURE_PLATE) {
			event.setCancelled(true);
		} else {
			
		}
	}
	@EventHandler
	public static void hangingBreakEvent(HangingBreakByEntityEvent event) {
		if(((Player)event.getRemover()).getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}
	@EventHandler
	public static void playerToggleSneakEvent(PlayerToggleSneakEvent event) {
		
		if(ChampList.playerChamp.get(event.getPlayer()) == ChampList.sword && event.getPlayer().getInventory().getItemInOffHand().getType() == Material.SHIELD) {
			new BukkitRunnable() {
            	public void run() {
                
            		if(event.getPlayer().isSneaking()) {
            			
            			if(ChampCooldowns.swordShieldHealth.get(event.getPlayer()) < ChampList.swordShieldMaxHealth) {
            				
            				ChampCooldowns.swordShieldHealth.replace(event.getPlayer(), ChampCooldowns.swordShieldHealth.get(event.getPlayer()) + 5);
            				
            				if(ChampCooldowns.swordShieldHealth.get(event.getPlayer()) > ChampList.swordShieldMaxHealth) {
            					ChampCooldowns.swordShieldHealth.replace(event.getPlayer(), ChampList.swordShieldMaxHealth);
            				}
            				
            				event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Shield Health: " + ChampCooldowns.swordShieldHealth.get(event.getPlayer()) + "/" + ChampList.swordShieldMaxHealth, ChatColor.YELLOW));
            				
            			}
            			
            		} else {
            			
            			cancel();

            		}
            	}
        	}.runTaskTimer(Main.getPlugin(Main.class), 4, 4);
		}
	}
	@EventHandler
	public static void playerPickupItemEvent(EntityPickupItemEvent event) {
		
		if(event.getEntity() instanceof Player) {
			
			Player pl = (Player)event.getEntity();
			
			if(event.getItem().getItemStack().getType() == Material.DIAMOND) {
				
				event.setCancelled(true);
				
				if(pl.getLevel() < ChampList.playerChamp.get(pl).maxHealth) {
					
					DamageSystem.healPlayer(pl, pl, 100);
					event.getItem().remove();
				
					if(event.getItem().getCustomName().equals("bot"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), 0.5, 21, 76.5);
					if(event.getItem().getCustomName().equals("top"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), 0.5, 29, 124.5);
					if(event.getItem().getCustomName().equals("blue"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), 31.5, 24, 104.5);
					if(event.getItem().getCustomName().equals("red"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), -30.5, 24, 104.5);
				}
			}
		}
	}
	@EventHandler
	public static void itemDespawnEvent(ItemDespawnEvent event) {
		
		if(event.getEntity().getItemStack().getType() == Material.DIAMOND) {
			
			event.setCancelled(true);
			
		}
	}
	
	@EventHandler
	public static void potionSplashEvent(PotionSplashEvent event) {
		
		if(ChampList.playerChamp.get(Bukkit.getPlayerExact(event.getPotion().getCustomName())) == ChampList.bow) {
			for(Entity ent : event.getAffectedEntities()) {
				if(ent instanceof Player) {
				
					Player pl = (Player)ent;
				
					double x = pl.getLocation().getX() - event.getPotion().getLocation().getX();
					double z = pl.getLocation().getZ() - event.getPotion().getLocation().getZ();
				
					final double r = 4.125;
					final double m = 3.0;
				
					Vector pushBack = new Vector();

					pushBack.setX(m*(x/r));
					pushBack.setZ(m*(z/r));
					pushBack.setY(1.25*((r - Math.abs(x))/r)*((r - Math.abs(z))/r));

					pl.setVelocity(pushBack);
				
				}
			}
		} else if(ChampList.playerChamp.get(Bukkit.getPlayerExact(event.getPotion().getCustomName())) == ChampList.gun) {
			for(Entity ent : event.getAffectedEntities()) {
				if(ent instanceof Player) {
				
					Player pl = (Player)ent;
				
					DamageSystem.stunPlayer(pl, 20);
					
				}
			}
		}
	}
}