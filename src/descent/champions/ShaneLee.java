package descent.champions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import descent.threads.LeeEnergyRegen;
import net.minecraft.server.v1_16_R3.MobEffect;
import net.minecraft.server.v1_16_R3.MobEffectList;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class ShaneLee extends Champ {
	public static final double MAX_HEALTH = 275;
	public static final String CHAMP_NAME = "Shane Lee";
	public static final float MOVE_SPEED = 0.25f;
	public static final double NATURAL_REGEN = 8.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { null, new ItemStack(Material.FEATHER),
			new ItemStack(Material.SLIME_BALL), new ItemStack(Material.GOLDEN_HORSE_ARMOR),
			new ItemStack(Material.NETHERITE_AXE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.GOLDEN_BOOTS), null,
			new ItemStack(Material.GOLDEN_CHESTPLATE), null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_NETHERITE_BLOCK_BREAK;

	public static final short MAX_ENERGY = 200;

	// Damage
	public static final double PUNCH_DAMAGE = 44;
	public static final double SONIC_HIT_DAMAGE = 45;
	public static final double SONIC_KICK_DAMAGE = 50;
	public static final double SAFE_HEAL_AMOUNT = 25;
	public static final double SLAM_DAMAGE = 40;
	public static final double ROUNDHOUSE_DAMAGE = 80;
	// Cool downs
	public static final float PUNCH_COOLDOWN = 1.1f;
	public static final float SONIC_WAVE_COOLDOWN = 7.0f;
	public static final float SAFE_COOLDOWN = 5.0f;
	public static final float SLAM_COOLDOWN = 7.0f;
	public static final float ROUNDHOUSE_COOLDOWN = 20.0f;
	public static final long LIFE_STEAL_DURATION = 5;
	public static final long LIFE_STEAL_RUNOUT = 4;
	public static final long SLAM_RUNOUT = 3;
	public static final long SONIC_RUNOUT = 4;
	// Energy usage
	public static final short SONIC_WAVE_ENERGY = 25;
	public static final short SONIC_KICK_ENERGY = 45;
	public static final short SAFE_ENERGY = 25;
	public static final short LIFESTEAL_ENERGY = 45;
	public static final short SLAM_ENERGY = 55;
	public static final short SLOW_ENERGY = 45;
	public static final short ROUNDHOUSE_ENERGY = 70;
	public static final short ENERGY_ON_KILL = 100;
	
	public static final short ROUNDHOUSE_VELOCITY = 4;
	public static final float LIFESTEAL_AMOUNT = 0.6f;

	private long timeAtLastPunch;
	private long timeAtLastSonicWave;
	private long timeAtLastSafe;
	private long timeAtLastSlam;
	private long timeAtLastRoundhouse;

	private float energy;
	private float lifeSteal;
	private Champ sonicMark;
	private List<Champ> slamMarks;
	private Thread sonicTimer;
	private Thread safeTimer;
	private Thread slamTimer;

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
		this.safeTimer = null;
		this.slamTimer = null;
		Thread regen = new Thread(new LeeEnergyRegen(player, this));
		regen.start();
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
		initialize();
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;

		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.AIR
				&& (System.currentTimeMillis() - timeAtLastPunch > (1000 * PUNCH_COOLDOWN))) {
			boolean killed = champ.takeDamage(PUNCH_DAMAGE);
			if (killed) {
				regenEnergy(ENERGY_ON_KILL);
			}
			this.heal(PUNCH_DAMAGE * lifeSteal);
			this.regenEnergy(10);
			timeAtLastPunch = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.NETHERITE_AXE
				&& (System.currentTimeMillis() - timeAtLastRoundhouse > (1000 * ROUNDHOUSE_COOLDOWN))
				&& energy >= ROUNDHOUSE_ENERGY) {
			this.heal(ROUNDHOUSE_DAMAGE * lifeSteal);
			champ.PLAYER.setVelocity(new Vector(PLAYER.getLocation().getDirection().getX() * ROUNDHOUSE_VELOCITY,
					PLAYER.getLocation().getDirection().getY() * ROUNDHOUSE_VELOCITY + 1,
					PLAYER.getLocation().getDirection().getZ() * ROUNDHOUSE_VELOCITY));
			timeAtLastRoundhouse = System.currentTimeMillis();
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(defend.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 2f, 0.5f);
			}
			useEnergy(ROUNDHOUSE_ENERGY);
			boolean killed = champ.takeDamage(ROUNDHOUSE_DAMAGE);
			if (killed) {
				regenEnergy(ENERGY_ON_KILL);
			}
			
		}
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.FEATHER
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSonicWave > (1000 * SONIC_WAVE_COOLDOWN))
				&& energy >= SONIC_WAVE_ENERGY) {

			useEnergy(SONIC_WAVE_ENERGY);
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
					return;
				}
				if (PLAYER.getInventory().getItem(1).getType() == Material.WHITE_DYE) {
					PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
					this.sonicMark = null;
					timeAtLastSonicWave = System.currentTimeMillis();
				}
			});
			sonicTimer = timer;
			timer.start();

			timeAtLastSonicWave = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.WHITE_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& energy >= SONIC_KICK_ENERGY) {
			sonicTimer.interrupt();
			PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
			useEnergy(SONIC_KICK_ENERGY);
			boolean killed = sonicMark.takeDamage(SONIC_KICK_DAMAGE);
			if (killed) {
				regenEnergy(ENERGY_ON_KILL);
			}
			this.heal(SONIC_KICK_DAMAGE * lifeSteal);
			PLAYER.teleport(sonicMark.PLAYER);
			this.sonicMark = null;
			timeAtLastSonicWave = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN)) && energy >= SAFE_ENERGY) {
			useEnergy(SAFE_ENERGY);
			this.heal(SAFE_HEAL_AMOUNT);
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(ShaneLee.LIFE_STEAL_RUNOUT * 1000);
				} catch (InterruptedException e) {
					return;
				}
				if (PLAYER.getInventory().getItem(2).getType() == Material.MAGMA_CREAM) {
					PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
					timeAtLastSafe = System.currentTimeMillis() - (long) (1000 * SAFE_COOLDOWN);
				}
			});
			safeTimer = timer;
			timer.start();
			timeAtLastSafe = System.currentTimeMillis();
			PLAYER.getInventory().setItem(2, new ItemStack(Material.MAGMA_CREAM));

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN))) {
			safeRayCast(PLAYER, 10);

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.MAGMA_CREAM
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& energy >= LIFESTEAL_ENERGY) {
			safeTimer.interrupt();
			lifeSteal = LIFESTEAL_AMOUNT;
			useEnergy(LIFESTEAL_ENERGY);
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
				&& (System.currentTimeMillis() - timeAtLastSlam > (1000 * SLAM_COOLDOWN)) && energy >= SLAM_ENERGY) {
			Collection<Entity> entities = PLAYER.getWorld().getNearbyEntities(PLAYER.getLocation(), 6, 6, 6);
			for (Entity e : entities) {
				if (e instanceof Player) {
					Player p = (Player) e;
					if (p != PLAYER) {
						Champ c = Champ.getChamp(p);
						slamMarks.add(c);
						boolean killed = c.takeDamage(SLAM_DAMAGE);
						if (killed) {
							regenEnergy(ENERGY_ON_KILL);
						}
						this.heal(SLAM_DAMAGE * lifeSteal);
					}
				}
			}
			useEnergy(SLAM_ENERGY);
			
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f);
			}
			if (slamMarks.size() < 1) {
				timeAtLastSlam = System.currentTimeMillis();
				return;
			}
			PLAYER.getInventory().setItem(3, new ItemStack(Material.LEATHER_HORSE_ARMOR));
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(SLAM_RUNOUT * 1000);
				} catch (InterruptedException e) {
					return;
				}
				if (PLAYER.getInventory().getItem(3).getType() == Material.LEATHER_HORSE_ARMOR) {
					PLAYER.getInventory().setItem(3, new ItemStack(Material.GOLDEN_HORSE_ARMOR));
					timeAtLastSlam = System.currentTimeMillis();
				}
			});
			slamTimer = timer;
			timer.start();
			timeAtLastSlam = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.LEATHER_HORSE_ARMOR
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK) && energy >= SLOW_ENERGY) {
			for (Champ c : slamMarks) {
				c.takeEffect(new PotionEffect(PotionEffectType.SLOW, 100, 4));
			}
			slamTimer.interrupt();
			useEnergy(SLOW_ENERGY);
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
				
				PacketPlayOutEntityEffect packet = new PacketPlayOutEntityEffect(champ.PLAYER.getEntityId(), new MobEffect(new MobEffect(MobEffectList.fromId(24), 10, 0)));
				
				PlayerConnection conn = ((CraftPlayer) PLAYER).getHandle().playerConnection;
				conn.sendPacket(packet);
				
				PLAYER.getInventory().setItem(1, new ItemStack(Material.WHITE_DYE));
				this.sonicMark = champ;
				arrow.remove();
				boolean killed = champ.takeDamage(SONIC_HIT_DAMAGE);
				if (killed) {
					regenEnergy(ENERGY_ON_KILL);
				}
				Thread timer = new Thread(() -> {
					try {
						Thread.sleep(SONIC_RUNOUT * 1000);
					} catch (InterruptedException e) {
						// squash
					}
					if (PLAYER.getInventory().getItem(1).getType() == Material.WHITE_DYE) {
						PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
						timeAtLastSonicWave = System.currentTimeMillis();
					}
				});
				timer.start();
				timeAtLastSonicWave = System.currentTimeMillis();
			}

		} else if (projectile == null) {
			if (energy >= SAFE_ENERGY) {
				useEnergy(SAFE_ENERGY);
				PLAYER.teleport(champ.PLAYER);
				champ.heal(SAFE_HEAL_AMOUNT);
				this.heal(SAFE_HEAL_AMOUNT);
				PLAYER.getInventory().setItem(2, new ItemStack(Material.MAGMA_CREAM));
				Thread timer = new Thread(() -> {
					try {
						Thread.sleep(ShaneLee.LIFE_STEAL_RUNOUT * 1000);
					} catch (InterruptedException e) {
						return;
					}
					if (PLAYER.getInventory().getItem(2).getType() == Material.MAGMA_CREAM) {
						PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
						timeAtLastSafe = System.currentTimeMillis() + (long) (1000 * SAFE_COOLDOWN);
					}
				});
				safeTimer = timer;
				timer.start();
				timeAtLastSafe = System.currentTimeMillis();
			}
		}
	}

	public void regenEnergy(int energy) {
		this.energy += energy;
		if (this.energy > ShaneLee.MAX_ENERGY) {
			this.energy = ShaneLee.MAX_ENERGY;
		}
		updateEnergy();
	}

	public void useEnergy(int energy) {
		this.energy -= energy;
		updateEnergy();
	}

	public void updateEnergy() {
		PLAYER.setLevel((int) energy);
	}

	@Override
	public void initialize() {
		super.initialize();
		regenEnergy(ShaneLee.MAX_ENERGY);
		return;
	}

	private static void safeRayCast(Player shooter, int distance) {

		World w = shooter.getWorld();
		Location l = new Location(w, shooter.getLocation().getX(),
				shooter.getLocation().getY() + shooter.getEyeHeight(), shooter.getLocation().getZ());
		Vector v = shooter.getLocation().getDirection();

		double x = v.getX();
		double y = v.getY();
		double z = v.getZ();

		for (double i = 0; i < distance; i = i + 0.1) {

			Location bulletLocation = new Location(w, l.getX() + (i * x), l.getY() + (i * y), l.getZ() + (i * z));

			Collection<Entity> entities = w.getNearbyEntities(bulletLocation, 0.05, 0.05, 0.05);

			if (i > 1) {

				if (shooter.getWorld().getBlockAt(bulletLocation).getType().isSolid() == false) {
					for (Entity ent : entities) {
						if (ent instanceof Player) {
							Player hit = (Player) ent;
							shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
							Champ plattack = Champ.getChamp(shooter);
							Champ pldefend = Champ.getChamp(hit);
							plattack.abilityRanged(pldefend, null);
							return;
						}
					}

				} else {

					return;

				}
			}
		}
	}

	@Override
	public String toString() {
		return super.toString() + " MARKED=" + this.sonicMark + " ENERGY=" + this.energy;
	}

}
