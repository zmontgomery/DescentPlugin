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
		Player user = event.getPlayer();

		if(user.getGameMode() == GameMode.SURVIVAL) {
			//LEFT CLICK ARROW
			if(user.getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
				Champ shooter = Champ.getChamp(user);
				shooter.shoot();
			}
		
			//RIGHT CLICK GOLDEN AXE (LEAP)
			if(user.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				if(System.currentTimeMillis() - ChampCooldowns.axeLeapCooldown.get(user) > (1000 * ChampList.axeLeapCooldown)) {
				
					user.setVelocity(new Vector(user.getLocation().getDirection().getX()*ChampList.axeLeapStrengthHoriz, user.getLocation().getDirection().getY()*ChampList.axeLeapStrengthVert, user.getLocation().getDirection().getZ()*ChampList.axeLeapStrengthHoriz));
					ChampCooldowns.axeLeapCooldown.replace(user, System.currentTimeMillis());
				
				}
			}
		
			//LEFT CLICK NETHERITE HOE (SHOOT)
			if(user.getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
				
				if(System.currentTimeMillis() - ChampCooldowns.gunShootCooldown.get(user) > (1000 * ChampList.gunShootCooldown)) {
			
					user.getWorld().playSound(user.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f);
				
					Ray.playerRayCast(user, 99, ChampList.gun.baseDamage);
					
					ChampCooldowns.gunShootCooldown.replace(user, System.currentTimeMillis());
				
				}
			}
		
			//RIGHT CLICK NETHERITE HOE (HEAL)
			if(user.getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			
				if(System.currentTimeMillis() - ChampCooldowns.gunHealCooldown.get(user) > (1000 * ChampList.gunHealCooldown)) {
				/*
					user.playSound(user.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
				
					Collection<Entity> entities = user.getWorld().getNearbyEntities(user.getLocation(), 6, 6, 5);
					Entity[] entityArray = entities.toArray(new Entity[entities.size()]);
				
					for(int i = 0; i < entityArray.length; i++) {
					
						if(entityArray[i] instanceof Player) {
						
							Player pl = (Player)entityArray[i];
						
							if(b.getEntryTeam(entityArray[i].getName()).getName() != "spec") {
								if(b.getEntryTeam(pl.getName()).getName() == b.getEntryTeam(user.getName()).getName()) {
							
									DamageSystem.healPlayer(user, pl, 60);
									user.getWorld().spawnParticle(Particle.HEART, pl.getEyeLocation(), 10, 0.5, 1, 0.5, 0);
							
								}
							}
						}
					}
				
					ChampCooldowns.gunHealCooldown.replace(user, System.currentTimeMillis());
				*/
					
					ThrownPotion pot = user.launchProjectile(ThrownPotion.class);
					pot.setVelocity(user.getLocation().getDirection());
					pot.setCustomName(user.getName());
					
					ChampCooldowns.gunHealCooldown.replace(user, System.currentTimeMillis());
					
				}
			
			}
			//RIGHT CLICK BLOCK WOODEN SWORD (TRAP)
			if(user.getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(System.currentTimeMillis() - ChampCooldowns.knifeTrapCooldown.get(user) > (1000 * ChampList.knifeTrapCooldown)) {
					
					Location block = event.getClickedBlock().getLocation();
					Location trap = block;
					trap.setY(trap.getY() + 1);
					
					if(trap.getBlock().getType() == Material.AIR && event.getClickedBlock().getType() == Material.POLISHED_ANDESITE) {
						
						trap.getBlock().setType(Material.STONE_PRESSURE_PLATE);
						
						if(ChampCooldowns.knifeTrapLocation.containsKey(user))
							user.getWorld().getBlockAt(ChampCooldowns.knifeTrapLocation.get(user)).setType(Material.AIR);
						else
							ChampCooldowns.knifeTrapLocation.put(user, trap);
						
						ChampCooldowns.knifeTrapLocation.replace(user, trap);
						
						ChampCooldowns.knifeTrapCooldown.replace(user, System.currentTimeMillis());
						
					}
				}
			}
			//LEFT CLICK BOW (DISENGANGE)
			if(user.getInventory().getItemInMainHand().getType() == Material.BOW && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
				if(System.currentTimeMillis() - ChampCooldowns.bowPotCooldown.get(user) > (1000 * ChampList.bowPotCooldown)) {
					ThrownPotion pot = user.launchProjectile(ThrownPotion.class);
					pot.setVelocity(user.getLocation().getDirection());
					pot.setCustomName(user.getName());
					
					ChampCooldowns.bowPotCooldown.replace(user, System.currentTimeMillis());
				}
			}
			//HIT PRESSURE PLATE EVENT
			if(event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PRESSURE_PLATE) {
				
				event.setCancelled(true);
				event.getClickedBlock().setType(Material.AIR);

				user.getWorld().playSound(user.getLocation(), Sound.BLOCK_CHEST_LOCKED, 1.0f, 0.5f);
				
				DamageSystem.stunPlayer(user, 40);
				
			}
			//PICK CHAMP SIGN EVENT
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				//PICK TEAM
				if(event.getClickedBlock().getType() == Material.OAK_WALL_SIGN) {
			
					Sign sign = (Sign) event.getClickedBlock().getState();
					
					if(sign.getLine(1).equals("[Blue]"))
						PlayerTeams.addToTeam(user, "blue");
					if(sign.getLine(1).equals("[Red]"))
						PlayerTeams.addToTeam(user, "red");

				}
				//BLUE TEAM
				if(event.getClickedBlock().getType() == Material.WARPED_WALL_SIGN) {
					
					Sign sign = (Sign) event.getClickedBlock().getState();
					
					if(b.getEntryTeam(user.getName()).getName() == "blue") {
						if(sign.getLine(1).equals("[Impaler]"))
							ChampList.setChamp(user, ChampList.knife);
						if(sign.getLine(1).equals("[Beserker]"))
							ChampList.setChamp(user, ChampList.axe);
						if(sign.getLine(1).equals("[Knight]"))
							ChampList.setChamp(user, ChampList.sword);
						if(sign.getLine(1).equals("[Deputy]"))
							ChampList.setChamp(user, ChampList.gun);
						if(sign.getLine(1).equals("[Archer]"))
							ChampList.setChamp(user, ChampList.bow);
					}
				}
				//RED TEAM
				if(event.getClickedBlock().getType() == Material.CRIMSON_WALL_SIGN) {
					
					Sign sign = (Sign) event.getClickedBlock().getState();
					
					if(b.getEntryTeam(user.getName()).getName() == "red") {
						if(sign.getLine(1).equals("[Impaler]"))
							ChampList.setChamp(user, ChampList.knife);
						if(sign.getLine(1).equals("[Beserker]"))
							ChampList.setChamp(user, ChampList.axe);
						if(sign.getLine(1).equals("[Knight]"))
							ChampList.setChamp(user, ChampList.sword);
						if(sign.getLine(1).equals("[Deputy]"))
							ChampList.setChamp(user, ChampList.gun);
						if(sign.getLine(1).equals("[Archer]"))
							ChampList.setChamp(user, ChampList.bow);
					}
				}
			}
		}
	}
	@EventHandler
	public static void playerJoinEvent(PlayerJoinEvent event) {
		
		ChampList.playerChamp.remove(user);
		user.getInventory().clear();
		user.setLevel(0);
		user.setHealth(20);
		user.setFoodLevel(5);
		user.setFlySpeed(0.1f);
		user.setWalkSpeed(0.2f);
		user.setGameMode(GameMode.SURVIVAL);
		user.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(64);
		user.setAllowFlight(false);
		
		for(PotionEffect p : user.getActivePotionEffects())
			user.removePotionEffect(p.getType());
		
		ChampCooldowns.knifeSwingCooldown.put(user, (long)0);
		ChampCooldowns.knifeTrapCooldown.put(user, (long)0);
		ChampCooldowns.swordSwingCooldown.put(user, (long)0);
		ChampCooldowns.axeSwingCooldown.put(user, (long)0);
		ChampCooldowns.axeLeapCooldown.put(user, (long)0);
		ChampCooldowns.gunShootCooldown.put(user, (long)0);
		ChampCooldowns.gunHealCooldown.put(user, (long)0);
		ChampCooldowns.bowPotCooldown.put(user, (long)0);
		
		PlayerTeams.addToTeam(user, "spec");
		
		user.teleport(user.getWorld().getSpawnLocation().add(new Location(user.getWorld(), 0.5, 0, 0.5)));
		
	}
	@EventHandler
	public static void playerQuitEvent(PlayerQuitEvent event) {
		
		ChampCooldowns.knifeSwingCooldown.remove(user);
		ChampCooldowns.knifeTrapCooldown.remove(user);
		ChampCooldowns.swordSwingCooldown.remove(user);
		ChampCooldowns.axeSwingCooldown.remove(user);
		ChampCooldowns.axeLeapCooldown.remove(user);
		ChampCooldowns.gunShootCooldown.remove(user);
		ChampCooldowns.gunHealCooldown.remove(user);
		ChampCooldowns.bowPotCooldown.remove(user);
		
		if(ChampCooldowns.knifeTrapLocation.containsKey(user))
			user.getWorld().getBlockAt(ChampCooldowns.knifeTrapLocation.get(user)).setType(Material.AIR);
		
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
		        user.setFoodLevel(5);
		    }
		}, 1L);
		if(b.getEntryTeam(user.getName()).getName().equals("blue")) {
			event.setRespawnLocation(new Location(user.getWorld(), Main.blueX, Main.blueY, Main.blueZ));
		} else if (b.getEntryTeam(user.getName()).getName().equals("red")) {
			event.setRespawnLocation(new Location(user.getWorld(), Main.redX, Main.redY, Main.redZ));
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
		if(user.getGameMode() == GameMode.SURVIVAL && event.getBlock().getType() != Material.STONE_PRESSURE_PLATE) {
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
		
		if(ChampList.playerChamp.get(user) == ChampList.sword && user.getInventory().getItemInOffHand().getType() == Material.SHIELD) {
			new BukkitRunnable() {
            	public void run() {
                
            		if(user.isSneaking()) {
            			
            			if(ChampCooldowns.swordShieldHealth.get(user) < ChampList.swordShieldMaxHealth) {
            				
            				ChampCooldowns.swordShieldHealth.replace(user, ChampCooldowns.swordShieldHealth.get(user) + 5);
            				
            				if(ChampCooldowns.swordShieldHealth.get(user) > ChampList.swordShieldMaxHealth) {
            					ChampCooldowns.swordShieldHealth.replace(user, ChampList.swordShieldMaxHealth);
            				}
            				
            				user.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText("Shield Health: " + ChampCooldowns.swordShieldHealth.get(user) + "/" + ChampList.swordShieldMaxHealth, ChatColor.YELLOW));
            				
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