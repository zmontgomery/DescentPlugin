package descentmodel;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

public class EventListener implements Listener {
	
	public static ScoreboardManager m = Bukkit.getScoreboardManager();
	public static Scoreboard b = m.getMainScoreboard();

	/**
	 * RAW DAMAGE DEALING
	 * @param event
	 */
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
				plattack.abilityInstant(pldefend);
		}
		//RANGED DAMAGE
		if(event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
			projectile = (Projectile) event.getDamager();
			plattack = Champ.getChamp(Bukkit.getPlayerExact(projectile.getCustomName()));
			pldefend = Champ.getChamp((Player)event.getEntity());
			if(b.getEntryTeam(pldefend.getName()).getName() != "spec")
				plattack.abilityRanged(pldefend, projectile);
		}
	}

	@EventHandler
	public static void playerInteractEvent(PlayerInteractEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			Action click = event.getAction();
			Player player = event.getPlayer();
			Champ user = Champ.getChamp(player);
			user.use(click);
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
	public static void playerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Champ.clearChamp(player);
		player.getInventory().clear();
		player.setLevel(0);
		player.setHealth(20);
		player.setFoodLevel(5);
		player.setFlySpeed(0.1f);
		player.setWalkSpeed(0.2f);
		player.setGameMode(GameMode.SURVIVAL);
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(64);
		player.setAllowFlight(false);
		
		for(PotionEffect p : player.getActivePotionEffects())
			player.removePotionEffect(p.getType());
		PlayerTeams.addToTeam(player, "spec");
		player.teleport(player.getWorld().getSpawnLocation().add(new Location(player.getWorld(), 0.5, 0, 0.5)));
	}

	@EventHandler
	public static void playerQuitEvent(PlayerQuitEvent event) {
		//Player player = event.getPlayer();
	}

	@EventHandler
	public static void playerDeathEvent(PlayerDeathEvent event) {
		event.setDeathMessage("");
	}

	@EventHandler
	public static void playerRespawnEvent(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
		    @Override
		    public void run() {
		        player.setFoodLevel(5);
		    }
		}, 1L);
		if(b.getEntryTeam(player.getName()).getName().equals("blue")) {
			event.setRespawnLocation(new Location(player.getWorld(), Main.blueX, Main.blueY, Main.blueZ));
		} else if (b.getEntryTeam(player.getName()).getName().equals("red")) {
			event.setRespawnLocation(new Location(player.getWorld(), Main.redX, Main.redY, Main.redZ));
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
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.SURVIVAL && event.getBlock().getType() != Material.STONE_PRESSURE_PLATE) {
			event.setCancelled(true);
		} 
	}

	@EventHandler
	public static void hangingBreakEvent(HangingBreakByEntityEvent event) {
		if(((Player)event.getRemover()).getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}
	
	@EventHandler
	public static void playerToggleSneakEvent(PlayerToggleSneakEvent event) {
		//Player player = event.getPlayer();
	}

	@EventHandler
	public static void playerPickupItemEvent(EntityPickupItemEvent event) {
		if(event.getEntity() instanceof Player) {
			Player pl = (Player)event.getEntity();
			if(event.getItem().getItemStack().getType() == Material.DIAMOND) {
				event.setCancelled(true);
				if(pl.getLevel() < Champ.getChamp(pl).MAX_HEALTH) {
					Champ.getChamp(pl).heal(100);
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
		
		if(Champ.getChamp(Bukkit.getPlayerExact(event.getPotion().getCustomName())) instanceof Impaler) {
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
		} else if(Champ.getChamp(Bukkit.getPlayerExact(event.getPotion().getCustomName())) instanceof Deputy) {
			for(Entity ent : event.getAffectedEntities()) {
				if(ent instanceof Player) {
					//Player pl = (Player)ent;
					//DamageSystem.stunPlayer(pl, 20);
				}
			}
		}
	}
}