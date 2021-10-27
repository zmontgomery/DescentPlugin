package descent.champions;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import descent.Main;

public class Pyromancer extends Champ {

	public static final double MAX_HEALTH = 150;
	public static final String CHAMP_NAME = "Pyromancer";
	public static final float MOVE_SPEED = 0.28f;
	public static final double NATURAL_REGEN = 6.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.BLAZE_POWDER) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.LEATHER_BOOTS), null,
			new ItemStack(Material.LEATHER_CHESTPLATE), null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_DROWNED_STEP;
	public static final float HURT_PITCH = 3.0f;
	
	public static final Sound FIRE_SOUND = Sound.BLOCK_FIRE_EXTINGUISH;
	public static final Sound JUMP_SOUND = Sound.ENTITY_LIGHTNING_BOLT_THUNDER;

	public static final double FIRE_DAMAGE = 19;
	// Cool downs
	public static final float FIRE_COOLDOWN = 0.81f;
	public static final float JUMP_COOLDOWN = 6.9f;

	public static final float JUMP_RUNOUT = 2.7f;

	private long timeAtLastFire;
	private long timeAtLastJump;

	public Pyromancer(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND, HURT_PITCH);
		timeAtLastFire = 0;
		timeAtLastJump = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.BLAZE_POWDER
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastFire > (1000 * FIRE_COOLDOWN))) {

			Location startLocation = new Location(PLAYER.getWorld(), PLAYER.getLocation().getX(),
					PLAYER.getLocation().getY() + PLAYER.getEyeHeight(), PLAYER.getLocation().getZ());
			Vector dir = new Vector(PLAYER.getLocation().getDirection().getX(),
					PLAYER.getLocation().getDirection().getY() + 0.15, PLAYER.getLocation().getDirection().getZ());

			Thread ray = new Thread(new FireRay(startLocation, dir, 150));
			ray.start();
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), FIRE_SOUND, 3.0f, 1.5f);
			}
			timeAtLastFire = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityHitscan(Champ champ, boolean headShot) {
		if (champ.takeDamage(FIRE_DAMAGE)) {
			onKill(champ);
		}
		champ.ignite(2.5);
		onHit();
	}

	@Override
	public void onSneak() {
		if (System.currentTimeMillis() - timeAtLastJump > (1000 * JUMP_COOLDOWN)) {
			takeEffect(new PotionEffect(PotionEffectType.JUMP, (int) (JUMP_RUNOUT * 20), 7));
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep((long) (JUMP_RUNOUT * 1000));
				} catch (InterruptedException e) {
					// squash
				}
				timeAtLastJump = System.currentTimeMillis();
			});
			timer.start();
			PLAYER.playSound(PLAYER.getLocation(), JUMP_SOUND, 2.0f, 3f);
			timeAtLastJump = System.currentTimeMillis();
		}
	}

	public class FireRay implements Runnable {

		Location startLocation;
		Vector dir;
		double distance;
		int wallsHit;
		public Collection<Entity> entities;

		private FireRay(Location startLocation, Vector dir, double distance, int wallsHit) {
			this.startLocation = startLocation;
			this.dir = dir;
			this.distance = distance;
			this.wallsHit = wallsHit;
		}

		private FireRay(Location startLocation, Vector dir, double distance) {
			this(startLocation, dir, distance, 0);
		}

		@Override
		public void run() {
			// STATIC
			World world = PLAYER.getWorld();

			double x = dir.getX();
			double y = dir.getY();
			double z = dir.getZ();

			double grav = 0;

			Location bulletStartLocation = new Location(world, startLocation.getX(), startLocation.getY(),
					startLocation.getZ());
			boolean did = false;
			short j = 0;
			for (double i = 0; i < distance; i += 0.1) {
				Location bulletLocation = new Location(world, bulletStartLocation.getX() + (i * x),
						bulletStartLocation.getY() + (i * y) - grav, bulletStartLocation.getZ() + (i * z));
				grav = 0.026 * (i * i);

				if (j % 25 == 0) {
					did = true;
					Runnable taskOne = new Runnable() {

						@Override
						public void run() {
							synchronized (this) {
								entities = world.getNearbyEntities(bulletLocation, 0.8, 0.8, 0.8);
								this.notify();
							}

							return;
						}

					};
					synchronized (taskOne) {
						Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), taskOne);
						try {
							taskOne.wait();
						} catch (InterruptedException e) {
							// squash
						}
					}
				}
				if (PLAYER.getWorld().getBlockAt(bulletLocation).getType().isSolid() == false) {
					world.spawnParticle(Particle.FLAME, bulletLocation, 1, 0.08, 0.08, 0.08, 0.02);
					if (did) {
						did = false;
						for (Entity ent : entities) {
							if (ent instanceof Player) {
								Player hit = (Player) ent;
								if (hit != PLAYER && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName().equals(Champ.BOARD.getEntryTeam(hit.getName()).getName())) {
									Runnable taskTwo = new Runnable() {
										@Override
										public void run() {
											Champ pldefend = Champ.getChamp(hit);
											abilityHitscan(pldefend, false);
											return;
										}

									};
									Bukkit.getScheduler().runTask(Main.getPlugin(Main.class), taskTwo);
									return;
								}
							}
						}
					}
					wallsHit = 0;

				} else {
					wallsHit++;
					if (wallsHit > 4) {
						return;
					}
					Vector newDir = new Vector();
					newDir.setX(dir.getX());
					newDir.setZ(dir.getZ());
					newDir.setY(0.3);
					Location next = new Location(world, bulletLocation.getX(), bulletLocation.getY() + 0.3,
							bulletLocation.getZ());
					Thread ray = new Thread(new FireRay(next, newDir, distance - i, wallsHit));
					ray.start();
					return;
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// squash
				}
				j++;
			}
			return;
		}

	}
}
