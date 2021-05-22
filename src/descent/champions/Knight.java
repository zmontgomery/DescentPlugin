package descent.champions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import descent.threads.KnightShieldRegen;
import descent.threads.Stampede;

public class Knight extends Champ {
	public static final double MAX_HEALTH = 300;
	public static final String CHAMP_NAME = "Knight";
	public static final float MOVE_SPEED = 0.24f;
	public static final double NATURAL_REGEN = 2.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.IRON_SWORD) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.IRON_BOOTS),
			new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE),
			new ItemStack(Material.IRON_HELMET) };
	public static final ItemStack LEFT_HAND = new ItemStack(Material.SHIELD);
	public static final Sound HURT_SOUND = null;

	public static final short MAX_SHIELD_HEALTH = 200;

	// Damage
	public static final int SWORD_DAMAGE = 45;
	// Cool downs
	public static final float MELEE_COOLDOWN = 0.4f;
	public static final float SHIELD_COOLDOWN = 5.0f;

	private long timeAtLastSwing;

	private short currentShieldHealth;

	public Knight(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		Thread regen = new Thread(new KnightShieldRegen(player, this));
		regen.start();
		Thread stampede = new Thread(new Stampede(player, this));
		stampede.start();
		timeAtLastSwing = 0;
		initialize();
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
			champ.takeDamage(SWORD_DAMAGE);
			timeAtLastSwing = System.currentTimeMillis();
		}
	}

	@Override
	public boolean takeDamage(double amount) {
		boolean killed = false;
		if (this.currentShieldHealth > 0 && PLAYER.isBlocking()) {
			this.currentShieldHealth -= amount;
			if (this.currentShieldHealth < 0) {
				this.currentShieldHealth = 0;
				PLAYER.getInventory().setItemInOffHand(null);
				Thread timer = new Thread(() -> {
					try {
						Thread.sleep((long) (SHIELD_COOLDOWN * 1000));
					} catch (InterruptedException e) {
						// squash
					}
					if(Champ.getChamp(PLAYER) instanceof Knight) {
						PLAYER.getInventory().setItemInOffHand(Knight.LEFT_HAND);
					}
				});
				timer.start();
			}
			updateShield();
		} else {
			this.currentHealth -= amount;
			if (this.currentHealth < 0) {
				this.currentHealth = 0;
				killed = true;
			}
			updatePlayerHealth();
		}
		return killed;
	}

	public void regenShield(int amount) {
		if(!PLAYER.isBlocking()) {
			currentShieldHealth += amount;
			if (currentShieldHealth > Knight.MAX_SHIELD_HEALTH) {
				currentShieldHealth = Knight.MAX_SHIELD_HEALTH;
			}
			updateShield();
		}
		return;
	}
	
	public void updateShield() {
		PLAYER.setLevel((int) currentShieldHealth);
	}

	@Override
	public void initialize() {
		super.initialize();
		regenShield(Knight.MAX_SHIELD_HEALTH);
		return;
	}

	@Override
	public String toString() {
		return super.toString() + " SHIELD=" + this.currentShieldHealth;
	}
}
