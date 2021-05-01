package descent.champions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

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

	public void initialize() {
		currentHealth = MAX_HEALTH;
		PLAYER.setHealth(20);
		PLAYER.setFoodLevel(5);
		PLAYER.setWalkSpeed(MOVE_SPEED);
		PLAYER.getInventory().clear();
		PLAYER.getInventory().setContents(ITEMS);
		PLAYER.getInventory().setArmorContents(CLOTHES);
		PLAYER.getInventory().setItemInOffHand(LEFT_HAND);
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

	public void takeDamage(double amount) {
		this.currentHealth -= amount;
		if (this.currentHealth < 0) {
			this.currentHealth = 0;
		}
		PLAYER.playSound(PLAYER.getLocation(), HURT_SOUND, 1f, 1f);
		updatePlayerHealth();
	}

	public void updatePlayerHealth() {
		this.PLAYER.setHealth(20 * (this.currentHealth / this.MAX_HEALTH));
	}

	@Override
	public String toString() {
		return this.NAME + "(" + this.CHAMP_NAME + ")" + ": HEALTH=" + this.currentHealth;
	}
}
