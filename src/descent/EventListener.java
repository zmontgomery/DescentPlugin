package descent;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.mojang.datafixers.util.Pair;

import descent.champions.Alchemist;
import descent.champions.Avatar;
import descent.champions.Beserker;
import descent.champions.Champ;
import descent.champions.Deputy;
import descent.champions.Generic;
import descent.champions.Hunter;
import descent.champions.Impaler;
import descent.champions.Knight;
import descent.champions.Ninja;
import descent.champions.ShaneLee;
import descent.threads.FoodSet;
import descent.threads.Regen;
import net.minecraft.server.v1_16_R3.EntityParrot;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import net.minecraft.server.v1_16_R3.NBTTagCompound;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.WorldServer;

public class EventListener implements Listener {

	/**
	 * RAW DAMAGE DEALING
	 * 
	 * @param event
	 */
	@EventHandler
	public static void playerDamageEvent(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player)
			event.setCancelled(true);
		Champ plattack;
		Champ pldefend;
		Projectile projectile;
		// MELEE DAMAGE
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			plattack = Champ.getChamp((Player) event.getDamager());
			pldefend = Champ.getChamp((Player) event.getEntity());
			plattack.abilityMelee(pldefend);
		}
		// RANGED DAMAGE
		if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
			projectile = (Projectile) event.getDamager();
			plattack = Champ.getChamp((Player) projectile.getShooter());
			pldefend = Champ.getChamp((Player) event.getEntity());
			if (plattack != pldefend) {
				plattack.abilityRanged(pldefend, projectile);
			}
		}
	}

	@EventHandler
	public static void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Champ user = Champ.getChamp(player);
		Action click = event.getAction();
		if (player.getGameMode() == GameMode.SURVIVAL && user != null) {
			user.use(click);
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.CRIMSON_WALL_SIGN) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(1).equals("[Impaler]"))
					new Impaler(player);
				if (sign.getLine(1).equals("[Beserker]"))
					new Beserker(player);
				if (sign.getLine(1).equals("[Knight]"))
					new Knight(player);
				if (sign.getLine(1).equals("[Deputy]"))
					new Deputy(player);
				if (sign.getLine(1).equals("[Hunter]"))
					new Hunter(player);
				if (sign.getLine(1).equals("[Ninja]"))
					new Ninja(player);
				if (sign.getLine(1).equals("[Avatar]"))
					new Avatar(player);
				if (sign.getLine(1).equals("[Shane Lee]"))
					new ShaneLee(player);
				if (sign.getLine(1).equals("[Alchemist]"))
					new Alchemist(player);
			}
		}
	}

	@EventHandler
	public static void entityShootBowEvent(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
			Player player = (Player) event.getEntity();
			Champ user = Champ.getChamp(player);
			user.bow(event.getForce());
		}
	}

	@EventHandler
	public static void playerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Champ.clearChamp(player);
		new Generic(player);
		player.getInventory().clear();
		player.setLevel(0);
		player.setHealth(20);

		Thread foodThread = new Thread(new FoodSet(player));
		foodThread.start();
		Thread regen = new Thread(new Regen(player));
		regen.start();

		player.setFlySpeed(0.1f);
		player.setWalkSpeed(0.2f);
		player.setGameMode(GameMode.SURVIVAL);
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(64);
		player.setAllowFlight(false);
		for (PotionEffect p : player.getActivePotionEffects())
			player.removePotionEffect(p.getType());
		player.teleport(player.getWorld().getSpawnLocation().add(new Location(player.getWorld(), 0.5, 0, 0.5)));
	}

	@EventHandler
	public static void playerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Champ.clearChamp(player);
		player.getInventory().clear();
		player.setLevel(0);
		player.setHealth(20);
		player.setFlySpeed(0.1f);
		player.setWalkSpeed(0.2f);
		player.setGameMode(GameMode.SURVIVAL);
		player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(64);
		player.setAllowFlight(false);
		for (PotionEffect p : player.getActivePotionEffects())
			player.removePotionEffect(p.getType());
		PlayerTeams.addToTeam(player, "spec");
		player.teleport(player.getWorld().getSpawnLocation().add(new Location(player.getWorld(), 0.5, 0, 0.5)));
	}

	@EventHandler
	public static void playerDeathEvent(PlayerDeathEvent event) {
		event.setDeathMessage("");
	}

	@EventHandler
	public static void playerRespawnEvent(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		Champ champ = Champ.getChamp(player);
		if (champ != null) {
			champ.initialize();
		}
		synchronized (player) {
			player.notifyAll();
		}
	}

	@EventHandler
	public static void damageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			// if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ||
			// event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			event.setCancelled(true);
			// }
		}
	}

	@EventHandler
	public static void blockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.SURVIVAL && event.getBlock().getType() != Material.STONE_PRESSURE_PLATE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public static void hangingBreakEvent(HangingBreakByEntityEvent event) {
		if (((Player) event.getRemover()).getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	@EventHandler
	public static void playerToggleSneakEvent(PlayerToggleSneakEvent event) {
		// Player player = event.getPlayer();
	}

	@EventHandler
	public static void playerPickupItemEvent(EntityPickupItemEvent event) {
		if (event.getEntity() instanceof Player) {
			Player pl = (Player) event.getEntity();
			if (event.getItem().getItemStack().getType() == Material.DIAMOND) {
				event.setCancelled(true);
				if (pl.getLevel() < Champ.getChamp(pl).MAX_HEALTH) {
					Champ.getChamp(pl).heal(100);
					event.getItem().remove();
					if (event.getItem().getCustomName().equals("bot"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), 0.5, 21, 76.5);
					if (event.getItem().getCustomName().equals("top"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), 0.5, 29, 124.5);
					if (event.getItem().getCustomName().equals("blue"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), 31.5, 24, 104.5);
					if (event.getItem().getCustomName().equals("red"))
						ControlPointGamemode.healthPackSpawning(pl.getWorld(), -30.5, 24, 104.5);
				}
			}
		}
	}

	@EventHandler
	public static void itemDespawnEvent(ItemDespawnEvent event) {
		if (event.getEntity().getItemStack().getType() == Material.DIAMOND) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public static void potionSplashEvent(PotionSplashEvent event) {

		if (Champ.getChamp((Player) event.getPotion().getShooter()) instanceof Alchemist) {
			for (Entity ent : event.getAffectedEntities()) {
				if (ent instanceof Player) {
					
					Player pl = (Player) ent;
					
					Champ c = Champ.getChamp(pl);
					
					c.heal(Alchemist.POTION_HEAL);
					
				}
			}
		} else if (Champ.getChamp(Bukkit.getPlayerExact(event.getPotion().getCustomName())) instanceof Deputy) {
			for (Entity ent : event.getAffectedEntities()) {
				if (ent instanceof Player) {
					// Player pl = (Player)ent;
					// DamageSystem.stunPlayer(pl, 20);
				}
			}
		}
	}
	@EventHandler
	public static void playerDropItemEvent(PlayerDropItemEvent event) {
		if(event.getItemDrop().getItemStack().getType() == Material.GOLDEN_SWORD) {
			event.setCancelled(true);
			event.getPlayer().setInvisible(true);
			event.getPlayer().getWorld()
			.spawnParticle(Particle.SMOKE_NORMAL, 
					event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + 1, event.getPlayer().getLocation().getZ(),
					75, 0.5, 1, 0.5, 0, null, true);
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE, 100, 1);
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true));
			
			Main.sendEquipmentInvisiblePacket(event.getPlayer(), true);
			
	        new BukkitRunnable() {
	            
	            @Override
	            public void run() {
	            	event.getPlayer().setInvisible(false);
	            	Main.sendEquipmentInvisiblePacket(event.getPlayer(), false);
	            }
	            
	        }.runTaskLater(Main.getPlugin(Main.class), 100);
		}
		if(event.getItemDrop().getItemStack().getType() == Material.APPLE) {
			event.setCancelled(true);
			
			Entity transformation = event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.PARROT);
			net.minecraft.server.v1_16_R3.Entity nmsTrans = ((CraftEntity) transformation).getHandle();
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInt("NoAI", 1);
			nmsTrans.a_(nbt);
			
			Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
			    @Override
			    public void run() {
			        transformation.teleport(event.getPlayer());
			    }
			}, 0L, 1L);
			
			event.getPlayer().getWorld()
			.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, 
					event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + 1, event.getPlayer().getLocation().getZ(),
					75, 0.5, 1, 0.5, 0, null, true);
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE, 100, 1);
			
			event.getPlayer().setInvisible(true);
			Main.sendEquipmentInvisiblePacket(event.getPlayer(), true);
		}
		if(event.getItemDrop().getItemStack().getType() == Material.GOLDEN_APPLE) {
			event.setCancelled(true);
			
			Entity transformation = event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.IRON_GOLEM);
			LivingEntity t = (LivingEntity) transformation;
			t.setAI(false);
			t.setInvulnerable(true);

			Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
			    @Override
			    public void run() {
			        transformation.teleport(event.getPlayer());
			    }
			}, 0L, 1L);
			
			PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(transformation.getEntityId());
			
			PlayerConnection conn = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;
			conn.sendPacket(packet);

			event.getPlayer().getWorld()
			.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, 
					event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + 1, event.getPlayer().getLocation().getZ(),
					75, 0.5, 1, 0.5, 0, null, true);
			event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE, 100, 1);
			
			event.getPlayer().setInvisible(true);
			Main.sendEquipmentInvisiblePacket(event.getPlayer(), true);
		}
	}
}