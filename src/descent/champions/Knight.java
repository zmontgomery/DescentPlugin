package descent.champions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Knight extends Champ {
	public static final double MAX_HEALTH = 300;
	public static final String CHAMP_NAME = "Knight";
	public static final float MOVE_SPEED = 0.23f;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.IRON_SWORD) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.IRON_BOOTS),
			new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE),
			new ItemStack(Material.IRON_HELMET) };
	public static final ItemStack LEFT_HAND = new ItemStack(Material.SHIELD);

	public static final short MAX_SHIELD_HEALTH = 300;
	private short currentShieldHealth;

	// Damage
	public static final int SWORD_DAMAGE = 45;
	// Cool downs
	public static final float MELEE_COOLDOWN = 0.4f;
	private long timeAtLastSwing;

	public Knight(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
		this.currentShieldHealth = Knight.MAX_SHIELD_HEALTH;
		timeAtLastSwing = System.currentTimeMillis() - (int)(1000 * MELEE_COOLDOWN);
	}

	public int getCurrentShieldHealth() {
		return this.currentShieldHealth;
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD
				&& (System.currentTimeMillis() - timeAtLastSwing > (1000 * MELEE_COOLDOWN))) {
			PLAYER.playSound(defend.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
			PLAYER.playSound(defend.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			champ.takeDamage(SWORD_DAMAGE);
			timeAtLastSwing = System.currentTimeMillis();
		}
	}

	@Override
	public void takeDamage(int amount) {
		if (this.currentShieldHealth > 0 && PLAYER.isBlocking()) {
			this.currentShieldHealth -= amount;
			if (this.currentShieldHealth < 0) {
				this.currentShieldHealth = 0;
			}
		} else {
			this.currentHealth -= amount;
			if (this.currentHealth < 0) {
				this.currentHealth = 0;
			}
			updatePlayerHealth();
		}

	}

	@Override
	public void initialize() {
		currentHealth = MAX_HEALTH;
		currentShieldHealth = MAX_SHIELD_HEALTH;
		PLAYER.setHealth(20);
		PLAYER.setFoodLevel(5);
		PLAYER.setWalkSpeed(MOVE_SPEED);
		PLAYER.getInventory().clear();
		PLAYER.getInventory().setContents(ITEMS);
		PLAYER.getInventory().setArmorContents(CLOTHES);
		PLAYER.getInventory().setItemInOffHand(LEFT_HAND);
		return;
	}

	@Override
	public String toString() {
		return super.toString() + " SHIELD=" + this.currentShieldHealth;
	}
}
