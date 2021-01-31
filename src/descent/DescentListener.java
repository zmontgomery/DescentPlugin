package descent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DescentListener implements Listener {
	@EventHandler
	public static void playerDamageEvent(EntityDamageByEntityEvent e) {
		
		e.setCancelled(true);
		
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
			
			Player plattack = (Player)e.getDamager();
			Player pldefend = (Player)e.getEntity();
			
			DamageSystem.damagePlayer(plattack, pldefend);
			
		}
		if(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player) {
			
			Bukkit.broadcastMessage("LL xD");
			
		}
	}
	@EventHandler
	public static void playerInteractEvent(PlayerInteractEvent e) {
		
		if(e.getPlayer().getInventory().getItemInMainHand().getType() == Material.ARROW && (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)) {
			
			e.getPlayer().getWorld().spawnArrow(new Location(e.getPlayer().getWorld(), e.getPlayer().getLocation().getX(), e.getPlayer().getLocation().getY() + e.getPlayer().getEyeHeight(), e.getPlayer().getLocation().getZ()), e.getPlayer().getLocation().getDirection(), 3, 0).setCustomName("knife");
			
		}
		
	}
	@EventHandler
	public static void playerJoinEvent(PlayerJoinEvent e) {
		
		ChampList.playerChamp.remove(e.getPlayer());
		e.getPlayer().getInventory().clear();
		e.getPlayer().setLevel(0);
		e.getPlayer().setHealth(20);
		
	}

}