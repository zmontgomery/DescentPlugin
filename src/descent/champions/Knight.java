package descent.champions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import descent.threads.KnightShieldRegen;

public class Knight extends Champ {
	public static final double MAX_HEALTH = 300;
	public static final String CHAMP_NAME = "Knight";
	public static final float MOVE_SPEED = 0.22f;
	public static final double NATURAL_REGEN = 2.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.IRON_SWORD) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.IRON_BOOTS),
			new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE),
			new ItemStack(Material.IRON_HELMET) };
	public static final ItemStack LEFT_HAND = new ItemStack(Material.SHIELD);
	public static final Sound HURT_SOUND = Sound.BLOCK_LANTERN_BREAK;

	public static final Sound SWORD_SOUND = Sound.ENTITY_DOLPHIN_SPLASH;
	public static final Sound SHIELD_HIT_SOUND = Sound.ITEM_SHIELD_BLOCK;
	public static final Sound SHIELD_BREAK_SOUND = Sound.ITEM_SHIELD_BREAK;
	public static final Sound SPEED_UP_SOUND = Sound.ENTITY_HORSE_GALLOP;
	public static final Sound NEW_SHIELD_SOUND = Sound.ITEM_ARMOR_EQUIP_NETHERITE;

	public static final short MAX_SHIELD_HEALTH = 150;

	// Damage
	public static final int SWORD_DAMAGE = 30;
	// Cool downs
	public static final float MELEE_COOLDOWN = 0.41f;
	public static final float SHIELD_COOLDOWN = 4.0f;

	public static final int DAMAGE_PER_INTERVAL = 7;
	public static final float STAMPEDE_RUNOUT = 0.32f;
	public static final float STAMPEDE_INTERVAL = 2.5f;
	public static final int MAX_INTERVALS = 4;
	public static final float INCREASE_AMOUNT = 0.041f;

	private long timeAtLastSwing;
	private long timeAtLastIncrease;

	private short currentShieldHealth;
	private int currentInterval;
	private Thread runoutTimer;

	public Knight(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		Thread regen = new Thread(new KnightShieldRegen(player, this));
		regen.start();

		runoutTimer = new Thread();
		currentInterval = 0;
		timeAtLastIncrease = 0;
		timeAtLastSwing = 0;
		initialize();
	}

	public int getCurrentShieldHealth() {
		return this.currentShieldHealth;
	}

	@Override
	public void abilityMelee(Champ champ) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD
				&& (System.currentTimeMillis() - timeAtLastSwing > (1000 * MELEE_COOLDOWN))
				&& !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
						.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			if (champ.takeDamage(SWORD_DAMAGE + (DAMAGE_PER_INTERVAL * currentInterval))) {
				onKill(champ);
			}
			onHit();
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), SWORD_SOUND, 1f, 3f);
			}

			PLAYER.setWalkSpeed(Knight.MOVE_SPEED);
			currentInterval = 0;
			timeAtLastSwing = System.currentTimeMillis();
		}
	}

	@Override
	public boolean takeDamage(double amount) {
		boolean killed = false;
		if (this.currentShieldHealth > 0 && PLAYER.isBlocking()) {
			this.currentShieldHealth -= amount;
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(player.getLocation(), SHIELD_HIT_SOUND, 1f, 0.6f);
			}
			if (this.currentShieldHealth <= 0) {
				this.currentShieldHealth = 0;
				PLAYER.getInventory().setItemInOffHand(null);
				Thread timer = new Thread(() -> {
					try {
						Thread.sleep((long) (SHIELD_COOLDOWN * 1000));
					} catch (InterruptedException e) {
						// squash
					}
					if (Champ.getChamp(PLAYER) instanceof Knight) {
						PLAYER.getInventory().setItemInOffHand(Knight.LEFT_HAND);
						PLAYER.playSound(PLAYER.getLocation(), NEW_SHIELD_SOUND, 1f, 2f);
					}
				});
				timer.start();
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.playSound(player.getLocation(), SHIELD_BREAK_SOUND, 1.5f, 0.6f);
				}
			}
			updateShield();
		} else {
			killed = super.takeDamage(amount);
		}
		return killed;
	}

	public void regenShield(int amount) {
		if (!PLAYER.isBlocking()) {
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

	// ONLY RUNS WHEN A KNIGHT MOVES
	public void stampede() {
		if (PLAYER.isBlocking()) {
			return;
		}
		runoutTimer.interrupt();
		runoutTimer = new Thread(() -> {
			try {
				Thread.sleep((long) (STAMPEDE_RUNOUT * 1000));
			} catch (InterruptedException e) {
				return;
			}
			PLAYER.setWalkSpeed(Knight.MOVE_SPEED);
			currentInterval = 0;
		});

		if (System.currentTimeMillis() - timeAtLastIncrease > (1000 * STAMPEDE_INTERVAL)
				&& currentInterval <= MAX_INTERVALS) {
			PLAYER.setWalkSpeed(PLAYER.getWalkSpeed() + INCREASE_AMOUNT);
			PLAYER.playSound(PLAYER.getLocation(), SPEED_UP_SOUND, 1f,
					1.0f * ((float) currentInterval / (float) MAX_INTERVALS));
			currentInterval++;
			timeAtLastIncrease = System.currentTimeMillis();
		}
		runoutTimer.start();
		return;
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
