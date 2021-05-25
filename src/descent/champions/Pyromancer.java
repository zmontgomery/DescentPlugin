package descent.champions;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import descent.Main;

public class Pyromancer extends Champ {

	private class FireRay extends BukkitRunnable {

		Location startLocation;
		Vector dir;
		double distance;
		int wallsHit;

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
			Location bulletLocation;

			for (double i = 0; i < distance; i += 0.5) {
				bulletLocation = new Location(world, bulletStartLocation.getX() + (i * x),
						bulletStartLocation.getY() + (i * y) - grav, bulletStartLocation.getZ() + (i * z));
				grav = 0.026 * (i * i);
				Collection<Entity> entities = world.getNearbyEntities(bulletLocation, 0.6, 0.6, 0.6);

				if (PLAYER.getWorld().getBlockAt(bulletLocation).getType().isSolid() == false) {
					world.spawnParticle(Particle.FLAME, bulletLocation, 1, 0.08, 0.08, 0.08, 0.02);
					for (Entity ent : entities) {
						if (ent instanceof Player) {
							Player hit = (Player) ent;
							if (hit != PLAYER) {
								Champ pldefend = Champ.getChamp(hit);
								abilityHitscan(pldefend, false);
								return;
							}
						}
					}
					wallsHit = 0;

				} else {
					wallsHit++;
					if(wallsHit > 4) {
						return;
					}
					Vector newDir = new Vector();
					newDir.setX(dir.getX());
					newDir.setZ(dir.getZ());
					newDir.setY(0.3);
					Location next = new Location(world, bulletLocation.getX(), bulletLocation.getY() + 0.3,
							bulletLocation.getZ());
					BukkitRunnable ray = new FireRay(next, newDir, distance - i, wallsHit);
					ray.runTaskLater(Main.getPlugin(Main.class), 1);
					return;
				}
			}
			return;
		}

	}

	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Pyromancer";
	public static final float MOVE_SPEED = 0.29f;
	public static final double NATURAL_REGEN = 5.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.BLAZE_POWDER) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.LEATHER_BOOTS), null,
			new ItemStack(Material.LEATHER_CHESTPLATE), null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_DROWNED_STEP;
	
	public static final Sound FIRE_SOUND = Sound.BLOCK_FIRE_EXTINGUISH;

	public static final double FIRE_DAMAGE = 50;
	// Cool downs
	public static final float FIRE_COOLDOWN = 0.8f;

	private long timeAtLastFire;

	public Pyromancer(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastFire = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.BLAZE_POWDER
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastFire > (1000 * FIRE_COOLDOWN))) {

			Location startLocation = new Location(PLAYER.getWorld(), PLAYER.getLocation().getX(),
					PLAYER.getLocation().getY() + PLAYER.getEyeHeight(), PLAYER.getLocation().getZ());
			Vector dir = new Vector(PLAYER.getLocation().getDirection().getX(), PLAYER.getLocation().getDirection().getY() + 0.15, PLAYER.getLocation().getDirection().getZ());
			
			BukkitRunnable ray = new FireRay(startLocation, dir, 150);
			ray.runTaskLater(Main.getPlugin(Main.class), 1);
			PLAYER.playSound(PLAYER.getLocation(), FIRE_SOUND, 3.0f, 1.5f);
			timeAtLastFire = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityHitscan(Champ champ, boolean headShot) {
		champ.takeDamage(FIRE_DAMAGE);
		champ.ignite(2.5);
		onHit();
	}
}
