package descent.champions;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;
import descent.Main;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.level.border.WorldBorder;


public abstract class Champ {

	private static Map<Player, Champ> players = new HashMap<>();
	public static final Random RNG = new Random();
	public static final ScoreboardManager MANAGER = Bukkit.getScoreboardManager();
	public static final Scoreboard BOARD = MANAGER.getMainScoreboard();
	
	public final Player PLAYER;
	public final String NAME;
	public final String CHAMP_NAME;
	public final double MAX_HEALTH;
	public final float MOVE_SPEED;
	public final double NATURAL_REGEN;
	public final ItemStack[] ITEMS;
	public final ItemStack[] CLOTHES;
	public final ItemStack LEFT_HAND;
	public final Sound HURT_SOUND;
	public final float HURT_PITCH;
	protected double currentHealth;

	public static final Sound KILL_SOUND = Sound.ENTITY_PLAYER_LEVELUP;
	public static final Sound HIT_SOUND = Sound.ENTITY_TURTLE_EGG_BREAK;

	public static final double FIRE_DAMAGE = 15;

	protected Champ(Player player, String champName, float moveSpeed, double naturalRegen, double maxHealth,
			ItemStack[] items, ItemStack[] clothes, ItemStack leftHand, Sound hurtSound, float hurtPitch) {
		clearChamp(player);
		addChamp(player, this);
		this.PLAYER = player;
		this.NAME = player.getName();
		this.CHAMP_NAME = champName;
		this.MOVE_SPEED = moveSpeed;
		this.NATURAL_REGEN = naturalRegen;
		this.MAX_HEALTH = maxHealth;
		this.currentHealth = maxHealth;
		this.ITEMS = items;
		this.CLOTHES = clothes;
		this.LEFT_HAND = leftHand;
		this.HURT_SOUND = hurtSound;
		this.HURT_PITCH = hurtPitch;
		initialize();
	}

	public static void addChamp(Player player, Champ champ) {
		players.put(player, champ);
	}

	public static Champ getChamp(Player player) {
		if (players.containsKey(player)) {
			return players.get(player);
		} else {
			return null;
		}
	}

	public static void clearChamp(Player player) {
		Champ.players.remove(player);
	}

	public void champSelect() {
		Inventory championSelect = Bukkit.createInventory(PLAYER, 18, "Champion Selection");

		championSelect.addItem(new ItemStack(Material.WOODEN_SWORD));
		championSelect.addItem(new ItemStack(Material.SHIELD));
		championSelect.addItem(new ItemStack(Material.GOLDEN_AXE));
		championSelect.addItem(new ItemStack(Material.NETHERITE_HOE));
		championSelect.addItem(new ItemStack(Material.BOW));
		championSelect.addItem(new ItemStack(Material.GOLDEN_SWORD));
		championSelect.addItem(new ItemStack(Material.POTION));
		championSelect.addItem(new ItemStack(Material.GOLDEN_CHESTPLATE));
		championSelect.addItem(new ItemStack(Material.BLAZE_POWDER));
		championSelect.addItem(new ItemStack(Material.GOLDEN_BOOTS));
		championSelect.addItem(new ItemStack(Material.YELLOW_DYE));

		PLAYER.openInventory(championSelect);
	}

	public void teamSelect() {
		Inventory teamSelect = Bukkit.createInventory(PLAYER, 9, "Champion Selection");

		teamSelect.addItem(new ItemStack(Material.AIR));
		teamSelect.addItem(new ItemStack(Material.AIR));
		teamSelect.addItem(new ItemStack(Material.RED_DYE));
		teamSelect.addItem(new ItemStack(Material.AIR));
		teamSelect.addItem(new ItemStack(Material.AIR));
		teamSelect.addItem(new ItemStack(Material.AIR));
		teamSelect.addItem(new ItemStack(Material.BLUE_DYE));
		teamSelect.addItem(new ItemStack(Material.AIR));
		teamSelect.addItem(new ItemStack(Material.AIR));

		PLAYER.openInventory(teamSelect);
	}

	public double getCurrentHealth() {
		return this.currentHealth;
	}

	public void use(Action click) {
		return;
	}

	public void abilityMelee(Champ champ) {
		return;
	}

	public void abilityHitscan(Champ champ, boolean headShot) {
		return;
	}

	public void abilityRanged(Champ champ, Projectile projectile) {
		return;
	}

	public void abilityPotion(ThrownPotion potion, Collection<LivingEntity> hits) {
		return;
	}

	public void bow(double force) {
		return;
	}

	public void onDrop(Material item) {
		return;
	}

	public void onSneak() {
		return;
	}

	public void ignite(double time) {

		Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), () -> {
			int totalTime = (PLAYER.getFireTicks() + (int) (time * 20));
			PLAYER.setFireTicks(totalTime);
		});

		return;
	}
	
	public void stun(double time) {
		takeEffect(new PotionEffect(PotionEffectType.JUMP,(int)(20 * time), -30));
		PLAYER.setWalkSpeed(0);
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
			PLAYER.setWalkSpeed(MOVE_SPEED);
		}, (long)(20 * time));

	}

	public void initialize() {

		currentHealth = MAX_HEALTH;
		PLAYER.setHealth(20);
		PLAYER.setLevel(0);
		PLAYER.setFlySpeed(0.1f);
		PLAYER.setWalkSpeed(MOVE_SPEED);
		PLAYER.getInventory().clear();
		PLAYER.getInventory().setContents(ITEMS);
		PLAYER.getInventory().setItemInOffHand(LEFT_HAND);
		PLAYER.getInventory().setArmorContents(CLOTHES);
		PLAYER.setVelocity(new Vector());
		PLAYER.setGameMode(GameMode.SURVIVAL);
		PLAYER.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(64);
		PLAYER.setAllowFlight(false);
		clearEffects();
		extinguish();
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
			PLAYER.setFoodLevel(5);
		}, 10);
		//Main.sendEquipmentInvisiblePacket(PLAYER, false);
		return;
	}

	public void heal(double amount) {
		if (!PLAYER.isDead()) {
			currentHealth += amount;
			if (this.currentHealth > this.MAX_HEALTH) {
				this.currentHealth = MAX_HEALTH;
			}
			updatePlayerHealth();
		}
	}

	public boolean takeDamage(double amount) {
		boolean killed = false;
		this.currentHealth -= amount;
		if (this.currentHealth < 0) {
			this.currentHealth = 0;
			killed = true;
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(PLAYER.getLocation(), HURT_SOUND, 1f, HURT_PITCH);
		}
		updatePlayerHealth();
		WorldBorder wb = new WorldBorder();
		wb.setCenter(0, 0);
		wb.setSize(300000);
		wb.setWarningDistance(600000);
		wb.world = ((CraftWorld) PLAYER.getWorld()).getHandle();
		
		ClientboundInitializeBorderPacket packet = new ClientboundInitializeBorderPacket(wb);
		PlayerConnection conn = ((CraftPlayer) PLAYER).getHandle().b;
		conn.sendPacket(packet);
		Thread th = new Thread(() -> {
			try {
				Thread.sleep(150);
				wb.setCenter(0, 0);
				wb.setSize(300000);
				wb.setWarningDistance(0);
				wb.world = ((CraftWorld) PLAYER.getWorld()).getHandle();
				conn.sendPacket(
						new ClientboundInitializeBorderPacket(wb));
			} catch (InterruptedException e) {
			}
		});
		th.start();
		return killed;
	}

	public void takeEffect(PotionEffect effect) {
		PLAYER.addPotionEffect(effect);
		return;
	}

	public void clearEffects() {
		for (PotionEffect p : PLAYER.getActivePotionEffects())
			PLAYER.removePotionEffect(p.getType());
	}

	public void extinguish() {
		PLAYER.setFireTicks(0);
	}

	public void onHit() {
		PLAYER.playSound(PLAYER.getLocation(), HIT_SOUND, 3f, 6.0f);
		return;
	}

	public void onKill(Champ champ) {
		PLAYER.playSound(PLAYER.getLocation(), KILL_SOUND, 3f, 4.0f);
		return;
	}

	public void kill() {
		this.takeDamage(this.MAX_HEALTH + 5);
	}

	public void updatePlayerHealth() {
		double newPlayerHealth = 20 * (this.currentHealth / this.MAX_HEALTH);
		if (newPlayerHealth == 0) {
			onDeath();
			return;
		}
		this.PLAYER.setHealth(newPlayerHealth);
		return;
	}

	public void onDeath() {
		Bukkit.broadcastMessage(PLAYER.getName() + "(" + CHAMP_NAME + ") died!");
		initialize();
		PLAYER.setGameMode(GameMode.SPECTATOR);
		PLAYER.setFlySpeed(0);
		PLAYER.setWalkSpeed(0);
		PLAYER.teleport(PLAYER.getWorld().getSpawnLocation().add(new Location(PLAYER.getWorld(), 0.5, 0, 0.5)));
		Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), () -> {
			PLAYER.teleport(Main.gamemode.respawnLocation(PLAYER));
			initialize();
		}, (long) (Main.gamemode.getRespawnTime() * 20));

		if (this instanceof Generic) {
			this.champSelect();
		}
		return;
	}

	@Override
	public String toString() {
		return this.NAME + "(" + this.CHAMP_NAME + ")" + ": HEALTH=" + this.currentHealth;
	}
}
