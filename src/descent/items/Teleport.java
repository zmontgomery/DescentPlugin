package descent.items;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import descent.champions.Champ;

public class Teleport extends Item{
	public static final String NAME = "Teleport";
	public static final short USES = 1;
	public static final int DISTANCE = 12;
	public static final float TELEPORT_COOLDOWN = 10;
	
	private long timeAtLastTeleport;

	public Teleport(Champ owner, ItemStack object) {
		super(NAME, owner, object, USES);
		timeAtLastTeleport = 0;
	}

	@Override
	public void use() {
		if(System.currentTimeMillis() - timeAtLastTeleport > (1000 * TELEPORT_COOLDOWN)) {
			teleportRayCast(DISTANCE);
			timeAtLastTeleport = System.currentTimeMillis();
		}
	}
	
	
	private void teleportRayCast(double distance) {
		World world = OWNER.PLAYER.getWorld();
		Location eye = OWNER.PLAYER.getEyeLocation();
		Vector direction = OWNER.PLAYER.getLocation().getDirection();

		double x = direction.getX();
		double y = direction.getY();
		double z = direction.getZ();

		Location endLocation = eye;
		Location oldEnd = null;
		int j = 0;
		for (double i = 0; i < distance; i = i + .2) {
			if (j % 5 == 0) {
				oldEnd = new Location(world, eye.getX() + (i * x), eye.getY() + (i * y), eye.getZ() + (i * z), eye.getYaw(),
						eye.getPitch());
			}
			endLocation = new Location(world, eye.getX() + (i * x), eye.getY() + (i * y), eye.getZ() + (i * z), eye.getYaw(),
					eye.getPitch());

			// Particle
			if (isValid(endLocation)) {
				world.spawnParticle(Particle.CRIT, eye, 1, 0, 0, 0, 0);
			} else {
				if (oldEnd != null) {
					oldEnd.setY(oldEnd.getY() - OWNER.PLAYER.getEyeHeight() + .22);
					OWNER.PLAYER.teleport(oldEnd);
				}
				return;
			}
			j++;
		}
		endLocation = new Location(world, endLocation.getX(), endLocation.getY() - OWNER.PLAYER.getEyeHeight() + .22, endLocation.getZ(), eye.getYaw(),
				eye.getPitch());
		OWNER.PLAYER.teleport(endLocation);
	}
	
	private boolean isValid(Location loc) {
		double height = OWNER.PLAYER.getEyeHeight();
		World world = OWNER.PLAYER.getWorld();
		float safeAura = 0.32f;
		Location[] spots = new Location[7];
		spots[0] = new Location(world, loc.getX() + safeAura, loc.getY(), loc.getZ());
		spots[1] = new Location(world, loc.getX() - safeAura, loc.getY(), loc.getZ());
		spots[2] = new Location(world, loc.getX(), loc.getY() + safeAura, loc.getZ());
		spots[3] = new Location(world, loc.getX(), loc.getY() - height + .1, loc.getZ());
		spots[4] = new Location(world, loc.getX(), loc.getY(), loc.getZ() + safeAura);
		spots[5] = new Location(world, loc.getX(), loc.getY(), loc.getZ() - safeAura);
		spots[6] = loc;
		for(Location spot : spots) {
			Material block = world.getBlockAt(spot).getType();
			if (block != Material.AIR && block != Material.WATER && block != Material.WALL_TORCH && block != Material.TORCH) {
				return false;
			} 
		}
		return true;
	}

}
