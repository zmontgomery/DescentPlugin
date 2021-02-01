package descent;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Ray {

	public static Player playerRayCast(Player shooter, int distance) {
		
		World w = shooter.getWorld();
		Location l = new Location(w, shooter.getLocation().getX(), shooter.getLocation().getY() + shooter.getEyeHeight(), shooter.getLocation().getZ());
		Vector v = shooter.getLocation().getDirection();
		
		double x = v.getX();
		double y = v.getY();
		double z = v.getZ();
		
		for(double i = 0; i < distance; i = i + 0.1) {
			
			Location location = new Location(w, l.getX() + (i * x), l.getY() + (i * y), l.getZ() + (i * z));
			
			Collection<Entity> entities = w.getNearbyEntities(location, 0.2, 0.2, 0.2);
			
			if(i > 1) {
				
				w.spawnParticle(Particle.CRIT, location.getX(), location.getY(), location.getZ(), 1, 0, 0, 0, 0);
				
				if(entities.size() > 0) {
				
					Entity[] entityArray = entities.toArray(new Entity[entities.size()]);
				
					for(int s = 0; s < entityArray.length; s++) {
						
						if(entityArray[s] instanceof Player) {
							
							return (Player)entityArray[s];
							
						}
						
					}
			
				}
			}
			
		}
		
		return null;
	
	}

}
