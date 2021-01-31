package descent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DescentListener implements Listener {
	@EventHandler
	public static void playerDamageEvent(EntityDamageByEntityEvent e) {
		
		e.setCancelled(true);
		
		Player plattack;
		Player pldefend;
		Projectile projectile;
		
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			
			plattack = (Player)e.getDamager();
			pldefend = (Player)e.getEntity();
			
			DamageSystem.damagePlayerMelee(plattack, pldefend);
			
		}
		if(e.getDamager() instanceof Projectile && e.getEntity() instanceof Player) {
			
			projectile = (Projectile) e.getDamager();
			plattack = Bukkit.getPlayerExact(projectile.getCustomName());
			pldefend = (Player)e.getEntity();
			
			DamageSystem.damagePlayerProjectile(plattack, pldefend, projectile);
			
		}
	}
	@EventHandler
	public static void damageEvent(EntityDamageEvent e) {
		
		e.setCancelled(true);

	}
	@EventHandler
	public static void playerInteractEvent(PlayerInteractEvent e) {
		
		if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.ARROW && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
			
			Arrow knife = e.getPlayer().getWorld().spawnArrow(new Location(e.getPlayer().getWorld(), e.getPlayer().getLocation().getX(), e.getPlayer().getLocation().getY() + e.getPlayer().getEyeHeight(), e.getPlayer().getLocation().getZ()), e.getPlayer().getLocation().getDirection(), 3, 0);
			knife.setCustomName(e.getPlayer().getName());
			knife.setBounce(false);
			
		}
		
	}
	@EventHandler
	public static void playerJoinEvent(PlayerJoinEvent e) {
		
		ChampList.playerChamp.remove(e.getPlayer());
		e.getPlayer().getInventory().clear();
		e.getPlayer().setLevel(0);
		e.getPlayer().setHealth(20);
		e.getPlayer().setFoodLevel(5);
		
		ChampCooldowns.knifeSwingCooldown.put(e.getPlayer(), (long)0);
		ChampCooldowns.swordSwingCooldown.put(e.getPlayer(), (long)0);
		
	}
	@EventHandler
	public static void playerQuitEvent(PlayerQuitEvent e) {
		
		ChampCooldowns.knifeSwingCooldown.remove(e.getPlayer());
		ChampCooldowns.swordSwingCooldown.remove(e.getPlayer());
		
	}
	@EventHandler
	public static void playerDeathEvent(PlayerDeathEvent e) {
		
		e.setDeathMessage("");
		
	}
	@EventHandler
	public static void playerRespawnEvent(PlayerRespawnEvent e) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
		    @Override
		    public void run() {
		        e.getPlayer().setFoodLevel(5);
		    }
		}, 1L);	
	}
}