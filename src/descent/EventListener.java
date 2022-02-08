package descent;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import descent.champions.Alchemist;
import descent.champions.Beserker;
import descent.champions.Monkey;
import descent.champions.Champ;
import descent.champions.Deputy;
import descent.champions.Generic;
import descent.champions.Hunter;
import descent.champions.Impaler;
import descent.champions.Knight;
import descent.champions.Pyromancer;
import descent.champions.Trainer;
import descent.items.Teleport;
import descent.items.Item;
import descent.champions.Ninja;
import descent.champions.Fighter;
import descent.threads.Regen;

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
	public static void swapHand(PlayerSwapHandItemsEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public static void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard board = manager.getMainScoreboard();
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.CRIMSON_WALL_SIGN
					&& board.getEntryTeam(player.getName()).getName().equals("red")) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(1).equals("[Select Champ]")) {
					Champ.getChamp(player).champSelect();
				}
			}
			if (event.getClickedBlock().getType() == Material.WARPED_WALL_SIGN
					&& board.getEntryTeam(player.getName()).getName().equals("blue")) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				if (sign.getLine(1).equals("[Select Champ]")) {
					Champ.getChamp(player).champSelect();
				}
			}
		}
		Champ user = Champ.getChamp(player);
		Action click = event.getAction();
		if (player.getGameMode() == GameMode.SURVIVAL && user != null  && event.getHand() != EquipmentSlot.OFF_HAND) {
			if(Item.getItem(user.PLAYER.getInventory().getItemInMainHand()) != null) {
				Item item = Item.getItem(user.PLAYER.getInventory().getItemInMainHand());
				item.use();
				
			} else {
				user.use(click);
			}
		}
	}
	
	@EventHandler
	public static void BlockPlace(BlockPlaceEvent event) {
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) {
			event.setCancelled(true);
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
		player.teleport(player.getWorld().getSpawnLocation().add(new Location(player.getWorld(), 0.5, 0, 0.5)));
	}

//	@EventHandler
//	public static void playerDeathEvent(PlayerDeathEvent event) {
//		event.setDeathMessage("");
////		if(event.getEntity() instanceof Player) {
////			Player player = (Player)event.getEntity();
////		}
//	}
//
//	@EventHandler
//	public static void playerRespawnEvent(PlayerRespawnEvent event) {
//		//event.setRespawnLocation(Main.gamemode.respawnLocation(player));
//	}

	@EventHandler
	public static void damageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			event.setCancelled(true);
			Player player = (Player) event.getEntity();
			Champ champ = Champ.getChamp(player);
			if (event.getEntity() instanceof Player) {
				if (event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.FIRE) {
					champ.takeDamage(Champ.FIRE_DAMAGE);
				} else if (event.getCause() == DamageCause.VOID) {
					player.teleport(
							player.getWorld().getSpawnLocation().add(new Location(player.getWorld(), 0.5, 0, 0.5)));
					champ.kill();
				}
			}
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
		Player player = event.getPlayer();
		Champ champ = Champ.getChamp(player);

		if (player.getGameMode() == GameMode.SURVIVAL && champ != null) {
			champ.onSneak();
		}
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
		Player player = (Player) event.getPotion().getShooter();
		Champ champ = Champ.getChamp(player);

		if (player.getGameMode() == GameMode.SURVIVAL && champ != null) {
			champ.abilityPotion(event.getPotion(), event.getAffectedEntities());
		}

	}

	@EventHandler
	public static void playerDropItemEvent(PlayerDropItemEvent event) {
		event.setCancelled(true);

		Player player = event.getPlayer();
		Champ champ = Champ.getChamp(player);

		if (player.getGameMode() == GameMode.SURVIVAL && champ != null) {
			champ.onDrop(event.getItemDrop().getItemStack().getType());
		}

		/*
		 * if(event.getItemDrop().getItemStack().getType() == Material.APPLE) {
		 * event.setCancelled(true);
		 * 
		 * Entity transformation =
		 * event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(),
		 * EntityType.PARROT); net.minecraft.server.v1_16_R3.Entity nmsTrans =
		 * ((CraftEntity) transformation).getHandle(); NBTTagCompound nbt = new
		 * NBTTagCompound(); nbt.setInt("NoAI", 1); nmsTrans.a_(nbt);
		 * 
		 * Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class),
		 * new Runnable() {
		 * 
		 * @Override public void run() { transformation.teleport(event.getPlayer()); }
		 * }, 0L, 1L);
		 * 
		 * event.getPlayer().getWorld() .spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
		 * event.getPlayer().getLocation().getX(),
		 * event.getPlayer().getLocation().getY() + 1,
		 * event.getPlayer().getLocation().getZ(), 75, 0.5, 1, 0.5, 0, null, true);
		 * event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
		 * Sound.ITEM_FIRECHARGE_USE, 100, 1);
		 * 
		 * event.getPlayer().setInvisible(true);
		 * Main.sendEquipmentInvisiblePacket(event.getPlayer(), true); }
		 * if(event.getItemDrop().getItemStack().getType() == Material.GOLDEN_APPLE) {
		 * event.setCancelled(true);
		 * 
		 * Entity transformation =
		 * event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(),
		 * EntityType.IRON_GOLEM); LivingEntity t = (LivingEntity) transformation;
		 * t.setAI(false); t.setInvulnerable(true);
		 * 
		 * Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class),
		 * new Runnable() {
		 * 
		 * @Override public void run() { transformation.teleport(event.getPlayer()); }
		 * }, 0L, 1L);
		 * 
		 * PacketPlayOutEntityDestroy packet = new
		 * PacketPlayOutEntityDestroy(transformation.getEntityId());
		 * 
		 * PlayerConnection conn = ((CraftPlayer)
		 * event.getPlayer()).getHandle().playerConnection; conn.sendPacket(packet);
		 * 
		 * event.getPlayer().getWorld() .spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
		 * event.getPlayer().getLocation().getX(),
		 * event.getPlayer().getLocation().getY() + 1,
		 * event.getPlayer().getLocation().getZ(), 75, 0.5, 1, 0.5, 0, null, true);
		 * event.getPlayer().getWorld().playSound(event.getPlayer().getLocation(),
		 * Sound.ITEM_FIRECHARGE_USE, 100, 1);
		 * 
		 * event.getPlayer().setInvisible(true);
		 * Main.sendEquipmentInvisiblePacket(event.getPlayer(), true); }
		 */
	}

	@EventHandler
	public void invClose(InventoryCloseEvent event) {
//		Player player = (Player) event.getPlayer();
//		Champ champ = Champ.getChamp(player);
//		if(champ instanceof Generic) {
//			player.openInventory(event.getInventory());
//		}
	}
	

	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Champ champ = Champ.getChamp(player);
		
		
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		event.setCancelled(true);
		if(event.getInventory().getType().equals(InventoryType.CRAFTING)){
			return;
		}
		ItemStack i = event.getCurrentItem();
		if (i == null) {
			return;
		}
		Material item = i.getType();
		if (item == null) {
			return;
		}
		if (item == Material.NETHERITE_SWORD) {
			new Impaler(player);
			player.closeInventory();
		}
		if (item == Material.SHIELD) {
			new Knight(player);
			player.closeInventory();
		}
		if (item == Material.GOLDEN_AXE) {
			new Beserker(player);
			player.closeInventory();
		}
		if (item == Material.NETHERITE_HOE) {
			new Deputy(player);
			player.closeInventory();
		}
		if (item == Material.BOW) {
			new Hunter(player);
			player.closeInventory();
		}
		if (item == Material.GOLDEN_SWORD) {
			new Ninja(player);
			player.closeInventory();
		}
		if (item == Material.POTION) {
			new Alchemist(player);
			player.closeInventory();
		}
		if (item == Material.GOLDEN_CHESTPLATE) {
			new Fighter(player);
			player.closeInventory();
		}
		if (item == Material.BLAZE_POWDER) {
			new Pyromancer(player);
			player.closeInventory();
		}
		if (item == Material.GOLDEN_BOOTS) {
			new Monkey(player);
			player.closeInventory();
		}
		if (item == Material.YELLOW_DYE) {
			new Trainer(player);
			player.closeInventory();
		}

		if (item == Material.RED_DYE) {
			Main.gamemode.joinTeam("red", player);
			player.teleport(Main.gamemode.respawnLocation(player));
			player.closeInventory();
			champ.champSelect();

		}
		if (item == Material.BLUE_DYE) {
			Main.gamemode.joinTeam("blue", player);
			player.teleport(Main.gamemode.respawnLocation(player));
			player.closeInventory();
			champ.champSelect();
		}
		if (item == Material.SPECTRAL_ARROW) {
			ItemStack dye = new ItemStack(Material.SPECTRAL_ARROW);
			new Teleport(champ, dye);
			player.getInventory().setItem(8, dye);
			player.closeInventory();
		}
	}

	@EventHandler
	public void playerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Champ champ = Champ.getChamp(player);

		// stamp
		if (champ instanceof Knight && ((int) event.getFrom().getX() != (int) event.getTo().getX()
				|| (int) event.getFrom().getZ() != (int) event.getTo().getZ())) {
			Knight knight = (Knight) champ;
			knight.stampede();
		}
	}
}