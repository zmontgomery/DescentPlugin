package descentmodel;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.Vector;

public class Ray {

	public static void playerRayCast(Player shooter, int distance) {
		boolean headShot = false;
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getMainScoreboard();
		
		World w = shooter.getWorld();
		Location l = new Location(w, shooter.getLocation().getX(), shooter.getLocation().getY() + shooter.getEyeHeight(), shooter.getLocation().getZ());
		Vector v = shooter.getLocation().getDirection();
		
		double x = v.getX();
		double y = v.getY();
		double z = v.getZ();
		
		for(double i = 0; i < distance; i = i + 0.1) {
			
			Location bulletLocation = new Location(w, l.getX() + (i * x), l.getY() + (i * y), l.getZ() + (i * z));
			
			Collection<Entity> entities = w.getNearbyEntities(bulletLocation, 0.05, 0.05, 0.05);
			
			if(i > 1) {
				
				if(shooter.getWorld().getBlockAt(bulletLocation).getType().isSolid() == false) {
					
					w.spawnParticle(Particle.CRIT, bulletLocation.getX(), bulletLocation.getY(), bulletLocation.getZ(), 1, 0, 0, 0, 0);
				
					for(Entity ent : entities) {
						
						if(ent instanceof Player) {
								
							Player hit = (Player)ent;
							
							if(b.getEntryTeam(hit.getPlayer().getName()).getName() != "spec") {
								if(b.getEntryTeam(shooter.getName()).getName() != b.getEntryTeam(hit.getPlayer().getName()).getName()) {
									
									if(Math.abs(hit.getEyeLocation().getY() - bulletLocation.getY()) < 0.24) {
										headShot = true;
										shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 2f);
									} else {
										shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 1f);
									}
								
									hit.playSound(hit.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
									
									Champ plattack = Champ.getChamp(shooter);
									Champ pldefend = Champ.getChamp(hit);
									plattack.abilityHitscan(pldefend, headShot);
									
									return;
									
								}
							}
						}						
					}
					
				} else {
					
					return;
					
				}
			}
		}	
	}	
}
