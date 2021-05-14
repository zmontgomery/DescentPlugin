package descent.champions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import descent.Ray;

public class ShaneLee extends Champ {
	public static final double MAX_HEALTH = 275;
	public static final String CHAMP_NAME = "Shane Lee";
	public static final float MOVE_SPEED = 0.24f;
	public static final double NATURAL_REGEN = 8.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { null, new ItemStack(Material.FEATHER),
			new ItemStack(Material.SLIME_BALL), new ItemStack(Material.GOLDEN_HORSE_ARMOR),
			new ItemStack(Material.NETHERITE_AXE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.GOLDEN_BOOTS), null,
			new ItemStack(Material.GOLDEN_CHESTPLATE), null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_NETHERITE_BLOCK_BREAK;
	public static final short MAX_ENERGY = 100;

	// Damage
	public static final double PUNCH_DAMAGE = 53;
	public static final double SONIC_HIT_DAMAGE = 45;
	public static final double SONIC_KICK_DAMAGE = 70;
	public static final double SAFE_HEAL_AMOUNT = 20;
	public static final double SLAM_DAMAGE = 40;
	public static final double ROUNDHOUSE_DAMAGE = 100;
	// Cool downs
	public static final float PUNCH_COOLDOWN = 1.2f;
	public static final float SONIC_WAVE_COOLDOWN = 6.0f;
	public static final float SAFE_COOLDOWN = 4.0f;
	public static final float SLAM_COOLDOWN = 6.0f;
	public static final float ROUNDHOUSE_COOLDOWN = 20.0f;
	public static final long LIFE_STEAL_DURATION = 5;
	public static final long LIFE_STEAL_RUNOUT = 5;
	public static final long SLAM_RUNOUT = 4;
	public static final long SONIC_RUNOUT = 5;
	// Energy usage
	public static final short SONIC_WAVE_ENERGY = 20;
	public static final short SONIC_KICK_ENERGY = 20;
	public static final short SAFE_ENERGY = 20;
	public static final short LIFESTEAL_ENERGY = 20;
	public static final short SLAM_ENERGY = 20;
	public static final short SLOW_ENERGY = 20;
	public static final short ROUNDHOUSE_ENERGY = 20;

	private long timeAtLastPunch;
	private long timeAtLastSonicWave;
	private long timeAtLastSafe;
	private long timeAtLastSlam;
	private long timeAtLastRoundhouse;

	private float energy;
	private float lifeSteal;
	private Champ sonicMark;
	private List<Champ> slamMarks;

	public ShaneLee(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastPunch = 0;
		timeAtLastSonicWave = 0;
		timeAtLastSafe = 0;
		timeAtLastSlam = 0;
		timeAtLastRoundhouse = 0;

		this.energy = MAX_ENERGY;
		this.sonicMark = null;
		this.lifeSteal = 0.0f;
		this.slamMarks = new ArrayList<>();
		Thread timer = new Thread(() -> {
			try {
				Thread.sleep(LIFE_STEAL_RUNOUT * 1000);
			} catch (InterruptedException e) {
				// squash
			}
			if (PLAYER.getInventory().getItem(2).getType() == Material.MAGMA_CREAM) {
				PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
				timeAtLastSafe = System.currentTimeMillis();
			}
		});
		timer.start();
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;

		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.AIR
				&& (System.currentTimeMillis() - timeAtLastPunch > (1000 * PUNCH_COOLDOWN))) {
			PLAYER.playSound(defend.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_BREAK, 1f, 1f);
			champ.takeDamage(PUNCH_DAMAGE);
			this.heal(PUNCH_DAMAGE * lifeSteal);
			timeAtLastPunch = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.NETHERITE_AXE
				&& (System.currentTimeMillis() - timeAtLastRoundhouse > (1000 * ROUNDHOUSE_COOLDOWN))) {

			PLAYER.playSound(defend.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f);
			this.energy -= ROUNDHOUSE_ENERGY;
			champ.takeDamage(ROUNDHOUSE_DAMAGE);
			this.heal(ROUNDHOUSE_DAMAGE * lifeSteal);
			champ.PLAYER.setVelocity(new Vector(PLAYER.getLocation().getDirection().getX() * 5,
					PLAYER.getLocation().getDirection().getY() * 5 + 1,
					PLAYER.getLocation().getDirection().getZ() * 5));
			timeAtLastRoundhouse = System.currentTimeMillis();
		}
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.FEATHER
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSonicWave > (1000 * SONIC_WAVE_COOLDOWN))) {

			this.energy -= SONIC_WAVE_ENERGY;
			Arrow arrow = PLAYER.getWorld().spawnArrow(PLAYER.getEyeLocation(), PLAYER.getLocation().getDirection(), 2,
					0);
			arrow.setShooter(PLAYER);
			arrow.setBounce(false);
			arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			arrow.setGravity(false);
			arrow.setCritical(true);
			arrow.setCustomName("MARK");
			arrow.setTicksLived(2000);
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(SONIC_RUNOUT * 1000);
				} catch (InterruptedException e) {
					// squash
				}
				if (PLAYER.getInventory().getItem(1).getType() == Material.WHITE_DYE) {
					PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
					sonicMark = null;
					timeAtLastSafe = System.currentTimeMillis();
				}
			});
			timer.start();

			timeAtLastSonicWave = System.currentTimeMillis();
			
			
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.WHITE_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)) {
			PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
			this.energy -= SONIC_KICK_ENERGY;
			sonicMark.takeDamage(SONIC_KICK_DAMAGE);
			this.heal(SONIC_KICK_DAMAGE * lifeSteal);
			PLAYER.teleport(sonicMark.PLAYER);
			sonicMark = null;
			timeAtLastSonicWave = System.currentTimeMillis();
			
			
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN))) {
			this.energy -= SAFE_ENERGY;
			this.heal(SAFE_HEAL_AMOUNT);
			PLAYER.getInventory().setItem(2, new ItemStack(Material.MAGMA_CREAM));
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(LIFE_STEAL_RUNOUT * 1000);
				} catch (InterruptedException e) {
					// squash
				}
				if (PLAYER.getInventory().getItem(2).getType() == Material.MAGMA_CREAM) {
					PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
					timeAtLastSafe = System.currentTimeMillis();
				}
			});
			timer.start();
			timeAtLastSafe = System.currentTimeMillis();
			
			
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN))) {
			Ray.safeRayCast(PLAYER, 10);
			
			
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)) {
			lifeSteal = 0.5f;
			this.energy -= LIFESTEAL_ENERGY;
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(LIFE_STEAL_DURATION * 1000);
				} catch (InterruptedException e) {
					// squash
				}
				this.lifeSteal = 0;
			});
			timer.start();
			PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
			timeAtLastSafe = System.currentTimeMillis();
			
			
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_HORSE_ARMOR
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSlam > (1000 * SLAM_COOLDOWN))) {
			Collection<Entity> entities = PLAYER.getWorld().getNearbyEntities(PLAYER.getLocation(), 6, 6, 6);
			for (Entity e : entities) {
				if (e instanceof Player) {
					Player p = (Player) e;
					if (p != PLAYER) {
						Champ c = Champ.getChamp(p);
						slamMarks.add(c);
						c.takeDamage(SLAM_DAMAGE);
						this.heal(SLAM_DAMAGE * lifeSteal);
					}
				}
			}
			this.energy -= SLAM_ENERGY;
			PLAYER.playSound(PLAYER.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f);
			if (slamMarks.size() < 1) {
				timeAtLastSlam = System.currentTimeMillis();
				return;
			}
			PLAYER.getInventory().setItem(3, new ItemStack(Material.LEATHER_HORSE_ARMOR));
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(SLAM_RUNOUT * 1000);
				} catch (InterruptedException e) {
					// squash
				}
				if (PLAYER.getInventory().getItem(3).getType() == Material.LEATHER_HORSE_ARMOR) {
					PLAYER.getInventory().setItem(3, new ItemStack(Material.GOLDEN_HORSE_ARMOR));
					timeAtLastSlam = System.currentTimeMillis();
				}
			});
			timer.start();
			timeAtLastSlam = System.currentTimeMillis();
			
			
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.LEATHER_HORSE_ARMOR
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)) {
			for (Champ c : slamMarks) {
				c.takeEffect(new PotionEffect(PotionEffectType.SLOW, 100, 4));
			}
			this.energy -= SLOW_ENERGY;
			slamMarks.clear();
			PLAYER.getInventory().setItem(3, new ItemStack(Material.GOLDEN_HORSE_ARMOR));
			timeAtLastSlam = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		if (projectile instanceof Arrow) {
			Arrow arrow = (Arrow) projectile;
			if (arrow.getCustomName().equals("MARK")) {
				PLAYER.getInventory().setItem(1, new ItemStack(Material.WHITE_DYE));
				this.sonicMark = champ;
				arrow.remove();
				champ.takeDamage(SONIC_HIT_DAMAGE);
				Thread timer = new Thread(() -> {
					try {
						Thread.sleep(SLAM_RUNOUT * 1000);
					} catch (InterruptedException e) {
						// squash
					}
					if (PLAYER.getInventory().getItem(1).getType() == Material.WHITE_DYE) {
						PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
						timeAtLastSlam = System.currentTimeMillis();
					}
				});
				timer.start();
				timeAtLastSonicWave = System.currentTimeMillis();
			}
			
			
		} else if (projectile == null) {
			this.energy -= SAFE_ENERGY;
			PLAYER.teleport(champ.PLAYER);
			champ.heal(SAFE_HEAL_AMOUNT);
			this.heal(SAFE_HEAL_AMOUNT);
			PLAYER.getInventory().setItem(2, new ItemStack(Material.MAGMA_CREAM));
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(LIFE_STEAL_RUNOUT * 1000);
				} catch (InterruptedException e) {
					// squash
				}
				if (PLAYER.getInventory().getItem(2).getType() == Material.MAGMA_CREAM) {
					PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
					timeAtLastSafe = System.currentTimeMillis();
				}
			});
			timer.start();
			timeAtLastSafe = System.currentTimeMillis();
		}
	}
	
	public void regenEnergy(short energy) {
		this.energy += energy;
	}

	@Override
	public String toString() {
		return super.toString() + " MARKED=" + this.sonicMark + " ENERGY=" + this.energy;
	}

}
