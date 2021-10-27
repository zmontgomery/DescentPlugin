package descent.champions;


import java.util.Collection;
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
import descent.threads.FighterEnergyRegen;
import net.minecraft.server.v1_16_R3.MobEffect;
import net.minecraft.server.v1_16_R3.MobEffectList;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityEffect;
import net.minecraft.server.v1_16_R3.PlayerConnection;

public class Fighter extends Champ {
	public static final double MAX_HEALTH = 275;
	public static final String CHAMP_NAME = "Fighter";
	public static final float MOVE_SPEED = 0.257f;
	public static final double NATURAL_REGEN = 8.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { null, new ItemStack(Material.FEATHER),
			new ItemStack(Material.SLIME_BALL), new ItemStack(Material.NETHERITE_AXE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.GOLDEN_BOOTS), null,
			new ItemStack(Material.GOLDEN_CHESTPLATE), null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_NETHERITE_BLOCK_BREAK;
	public static final float HURT_PITCH = 3.0f;

	public static final Sound SONIC_SHOOT_SOUND = Sound.BLOCK_SHROOMLIGHT_BREAK;
	public static final Sound SONIC_LEAP_SOUND = Sound.BLOCK_PUMPKIN_CARVE;
	public static final Sound PUNCH_SOUND = Sound.ENTITY_PLAYER_ATTACK_CRIT;
	public static final Sound KICK_SOUND = Sound.ENTITY_DRAGON_FIREBALL_EXPLODE;
	public static final Sound SAFE_SOUND = Sound.BLOCK_ENCHANTMENT_TABLE_USE;

	public static final short MAX_ENERGY = 200;

	// Damage
	public static final double PUNCH_DAMAGE = 37;
	public static final double SONIC_HIT_DAMAGE = 35;
	public static final double SONIC_KICK_DAMAGE = 45;
	public static final double SAFE_HEAL_AMOUNT = 20;
	public static final double ROUNDHOUSE_DAMAGE = 70;
	// Cool downs
	public static final float PUNCH_COOLDOWN = 1.1f;
	public static final float SONIC_WAVE_COOLDOWN = 7.0f;
	public static final float SAFE_COOLDOWN = 5.0f;
	public static final float ROUNDHOUSE_COOLDOWN = 20.0f;
	public static final long LIFE_STEAL_DURATION = 5;
	public static final long LIFE_STEAL_RUNOUT = 4;
	public static final long SONIC_RUNOUT = 4;
	// Energy usage
	public static final short SONIC_WAVE_ENERGY = 40;
	public static final short SONIC_KICK_ENERGY = 70;
	public static final short SAFE_ENERGY = 40;
	public static final short LIFESTEAL_ENERGY = 60;
	public static final short ROUNDHOUSE_ENERGY = 85;
	public static final short ENERGY_ON_KILL = 40;

	public static final short ROUNDHOUSE_VELOCITY = 3;
	public static final float LIFESTEAL_AMOUNT = 0.5f;
	public static final int SAFE_DISTANCE = 10;
	public static final float SPEED_TIME = 4.0f;

	private long timeAtLastPunch;
	private long timeAtLastSonicWave;
	private long timeAtLastSafe;
	private long timeAtLastRoundhouse;

	private float energy;
	private float lifeSteal;
	private Champ sonicMark;
	private Thread sonicTimer;
	private Thread safeTimer;

	public Fighter(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND,
				HURT_PITCH);
		timeAtLastPunch = 0;
		timeAtLastSonicWave = 0;
		timeAtLastSafe = 0;
		timeAtLastRoundhouse = 0;

		this.energy = MAX_ENERGY;
		this.sonicMark = null;
		this.lifeSteal = 0.0f;
		this.safeTimer = null;
		Thread regen = new Thread(new FighterEnergyRegen(player, this));
		regen.start();
		initialize();
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;

		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.AIR
				&& (System.currentTimeMillis() - timeAtLastPunch > (1000 * PUNCH_COOLDOWN))
				&& !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
						.equals(Champ.BOARD.getEntryTeam(defend.getName()).getName())) {
			boolean killed = champ.takeDamage(PUNCH_DAMAGE);
			onHit();
			if (killed) {
				onKill(champ);
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), PUNCH_SOUND, 1f, 0.4f);
			}
			this.heal(PUNCH_DAMAGE * lifeSteal);
			this.regenEnergy(10);
			timeAtLastPunch = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.NETHERITE_AXE
				&& (System.currentTimeMillis() - timeAtLastRoundhouse > (1000 * ROUNDHOUSE_COOLDOWN))
				&& energy >= ROUNDHOUSE_ENERGY && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
						.equals(Champ.BOARD.getEntryTeam(defend.getName()).getName())) {
			this.heal(ROUNDHOUSE_DAMAGE * lifeSteal);
			champ.PLAYER.setVelocity(new Vector(PLAYER.getLocation().getDirection().getX() * ROUNDHOUSE_VELOCITY,
					PLAYER.getLocation().getDirection().getY() * ROUNDHOUSE_VELOCITY + 1,
					PLAYER.getLocation().getDirection().getZ() * ROUNDHOUSE_VELOCITY));
			timeAtLastRoundhouse = System.currentTimeMillis();
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(defend.getLocation(), KICK_SOUND, 2f, 0.5f);
			}
			useEnergy(ROUNDHOUSE_ENERGY);
			boolean killed = champ.takeDamage(ROUNDHOUSE_DAMAGE);
			onHit();
			if (killed) {
				onKill(champ);
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
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), SONIC_SHOOT_SOUND, 1f, 0.4f);
			}
			sonicTimer = timer;
			timer.start();

			timeAtLastSonicWave = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.WHITE_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& energy >= SONIC_KICK_ENERGY) {
			sonicTimer.interrupt();
			PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
			useEnergy(SONIC_KICK_ENERGY);
			Vector direction = PLAYER.getLocation().getDirection();
			PLAYER.teleport(sonicMark.PLAYER);
			PLAYER.getLocation().setDirection(direction);
			boolean killed = sonicMark.takeDamage(SONIC_KICK_DAMAGE);
			onHit();
			this.heal(SONIC_KICK_DAMAGE * lifeSteal);
			if (killed) {
				onKill(sonicMark);
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), SONIC_LEAP_SOUND, 1f, 0.7f);
			}
			this.sonicMark = null;
			timeAtLastSonicWave = System.currentTimeMillis();

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN)) && energy >= SAFE_ENERGY) {
			useEnergy(SAFE_ENERGY);
			this.heal(SAFE_HEAL_AMOUNT);
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep(Fighter.LIFE_STEAL_RUNOUT * 1000);
				} catch (InterruptedException e) {
					return;
				}
				if (PLAYER.getInventory().getItem(2).getType() == Material.MAGMA_CREAM) {
					PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
					timeAtLastSafe = System.currentTimeMillis() - (long) (1000 * SAFE_COOLDOWN);
				}
			});
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), SAFE_SOUND, 1f, 1.5f);
			}
			safeTimer = timer;
			timer.start();
			timeAtLastSafe = System.currentTimeMillis();
			PLAYER.getInventory().setItem(2, new ItemStack(Material.MAGMA_CREAM));

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN))) {
			safeRayCast();

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
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), SAFE_SOUND, 1f, 0.5f);
			}
			timer.start();
			PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
			timeAtLastSafe = System.currentTimeMillis();

		}
	}

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		if (projectile instanceof Arrow) {
			Arrow arrow = (Arrow) projectile;
			if (arrow.getCustomName().equals("MARK") && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
					.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {

				PacketPlayOutEntityEffect packet = new PacketPlayOutEntityEffect(champ.PLAYER.getEntityId(),
						new MobEffect(new MobEffect(MobEffectList.fromId(24), 10, 0)));

				PlayerConnection conn = ((CraftPlayer) PLAYER).getHandle().playerConnection;
				conn.sendPacket(packet);

				PLAYER.getInventory().setItem(1, new ItemStack(Material.WHITE_DYE));
				this.sonicMark = champ;
				arrow.remove();
				boolean killed = champ.takeDamage(SONIC_HIT_DAMAGE);
				onHit();
				if (killed) {
					onKill(champ);
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

		} else if (projectile == null && Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
				.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			if (energy >= SAFE_ENERGY) {
				useEnergy(SAFE_ENERGY);
				PLAYER.teleport(champ.PLAYER);
				champ.heal(SAFE_HEAL_AMOUNT);
				this.heal(SAFE_HEAL_AMOUNT);
				PLAYER.getInventory().setItem(2, new ItemStack(Material.MAGMA_CREAM));
				Thread timer = new Thread(() -> {
					try {
						Thread.sleep(Fighter.LIFE_STEAL_RUNOUT * 1000);
					} catch (InterruptedException e) {
						return;
					}
					if (PLAYER.getInventory().getItem(2).getType() == Material.MAGMA_CREAM) {
						PLAYER.getInventory().setItem(2, new ItemStack(Material.SLIME_BALL));
						timeAtLastSafe = System.currentTimeMillis() + (long) (1000 * SAFE_COOLDOWN);
					}
				});
				for (Player player : Bukkit.getOnlinePlayers()) {
					player.playSound(PLAYER.getLocation(), SAFE_SOUND, 1f, 1.5f);
				}
				safeTimer = timer;
				timer.start();
				timeAtLastSafe = System.currentTimeMillis();
			}
		}
	}

	public void regenEnergy(int energy) {
		this.energy += energy;
		if (this.energy > Fighter.MAX_ENERGY) {
			this.energy = Fighter.MAX_ENERGY;
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
		regenEnergy(Fighter.MAX_ENERGY);
		return;
	}

	@Override
	public void onKill(Champ champ) {
		super.onKill(champ);
		regenEnergy(ENERGY_ON_KILL);
		if (champ == sonicMark) {
			sonicTimer.interrupt();
			PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
			this.sonicMark = null;
		}
		PotionEffect effect = new PotionEffect(PotionEffectType.SPEED, (int) (20 * SPEED_TIME), 1);
		takeEffect(effect);
	}

	private void safeRayCast() {

		World w = PLAYER.getWorld();
		Location l = new Location(w, PLAYER.getLocation().getX(), PLAYER.getLocation().getY() + PLAYER.getEyeHeight(),
				PLAYER.getLocation().getZ());
		Vector v = PLAYER.getLocation().getDirection();

		double x = v.getX();
		double y = v.getY();
		double z = v.getZ();

		for (double i = 0; i < SAFE_DISTANCE; i = i + 0.1) {

			Location bulletLocation = new Location(w, l.getX() + (i * x), l.getY() + (i * y), l.getZ() + (i * z));

			Collection<Entity> entities = w.getNearbyEntities(bulletLocation, 0.05, 0.05, 0.05);

			if (i > 1) {

				if (PLAYER.getWorld().getBlockAt(bulletLocation).getType().isSolid() == false) {
					for (Entity ent : entities) {
						if (ent instanceof Player) {
							Player hit = (Player) ent;
							if (Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
									.equals(Champ.BOARD.getEntryTeam(hit.getName()).getName())) {
								PLAYER.playSound(PLAYER.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
								Champ pldefend = Champ.getChamp(hit);
								this.abilityRanged(pldefend, null);
							}
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
