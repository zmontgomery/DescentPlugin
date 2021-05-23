package descent.champions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import descent.Main;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.WorldBorder;

public abstract class Champ {

	private static Map<Player, Champ> players = new HashMap<>();
	public static final Random RNG = new Random();

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
	protected double currentHealth;

	protected Champ(Player player, String champName, float moveSpeed, double naturalRegen, double maxHealth, ItemStack[] items,
			ItemStack[] clothes, ItemStack leftHand, Sound hurtSound) {
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
		Inventory championSelect = Bukkit.createInventory(PLAYER, 9, "Champion Selection");
		
		championSelect.addItem(new ItemStack(Material.WOODEN_SWORD));
		championSelect.addItem(new ItemStack(Material.SHIELD));
		championSelect.addItem(new ItemStack(Material.GOLDEN_AXE));
		championSelect.addItem(new ItemStack(Material.NETHERITE_HOE));
		championSelect.addItem(new ItemStack(Material.BOW));
		championSelect.addItem(new ItemStack(Material.GOLDEN_SWORD));
		championSelect.addItem(new ItemStack(Material.POTION));
		championSelect.addItem(new ItemStack(Material.GOLDEN_CHESTPLATE));
		
		PLAYER.openInventory(championSelect);
	}
	
	public void teamSelect() {
		// PLAYER.alsodoSomethinglol();
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

	public void bow(double force) {
		return;
	}
	public void detectElim() {
		return;
	}

	public void initialize() {
		currentHealth = MAX_HEALTH;
		PLAYER.setHealth(20);
		PLAYER.setFoodLevel(5);
		PLAYER.setLevel(0);
		PLAYER.setWalkSpeed(MOVE_SPEED);
		PLAYER.getInventory().clear();
		PLAYER.getInventory().setContents(ITEMS);
		PLAYER.getInventory().setArmorContents(CLOTHES);
		PLAYER.getInventory().setItemInOffHand(LEFT_HAND);
		Main.gamemode.respawn(PLAYER);
		return;
	}

	public void heal(double amount) {
		if(!PLAYER.isDead()) {
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
		PLAYER.playSound(PLAYER.getLocation(), HURT_SOUND, 1f, 1f);
		updatePlayerHealth();
		WorldBorder wb = new WorldBorder();
		wb.setCenter(0, 0);
		wb.setSize(300000);
		wb.setWarningDistance(600000);
		wb.world = ((CraftWorld) PLAYER.getWorld()).getHandle();
		PacketPlayOutWorldBorder packet = new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE);
		PlayerConnection conn = ((CraftPlayer) PLAYER).getHandle().playerConnection;
		conn.sendPacket(packet);
		Thread th = new Thread(() -> {
			try {
				Thread.sleep(150);
				wb.setCenter(0, 0);
				wb.setSize(300000);
				wb.setWarningDistance(0);
				wb.world = ((CraftWorld) PLAYER.getWorld()).getHandle();
				conn.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
			} catch (InterruptedException e) {}
		});
		th.start();
		return killed;
	}
	
	public void takeEffect(PotionEffect effect) {
		PLAYER.addPotionEffect(effect);
		return;
	}
	
	public void kill() {
		this.takeDamage(this.MAX_HEALTH + 5);
	}

	public void updatePlayerHealth() {
		this.PLAYER.setHealth(20 * (this.currentHealth / this.MAX_HEALTH));
	}

	@Override
	public String toString() {
		return this.NAME + "(" + this.CHAMP_NAME + ")" + ": HEALTH=" + this.currentHealth;
	}
}
