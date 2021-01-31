package descent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

public class DescentListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public static void playerDamageEvent(EntityDamageByEntityEvent event) {
		
		event.setCancelled(true);
		
		Player plattack;
		Player pldefend;
		Projectile projectile;

		if(event.getDamager() instanceof Player && event.getEntity() instanceof Player) {

			
			plattack = (Player)event.getDamager();
			pldefend = (Player)event.getEntity();
			
			DamageSystem.damagePlayerMelee(plattack, pldefend);
			
		}
		if(event.getDamager() instanceof Projectile && event.getEntity() instanceof Player) {
			
			projectile = (Projectile) event.getDamager();
			plattack = Bukkit.getPlayerExact(projectile.getCustomName());
			pldefend = (Player)event.getEntity();
			
			DamageSystem.damagePlayerProjectile(plattack, pldefend, projectile);
			
		}
	}
	@EventHandler
	public static void playerInteractEvent(PlayerInteractEvent event) {
		
		if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.ARROW && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
			
			Arrow knife = event.getPlayer().getWorld().spawnArrow(new Location(event.getPlayer().getWorld(), event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + event.getPlayer().getEyeHeight(), event.getPlayer().getLocation().getZ()), event.getPlayer().getLocation().getDirection(), 3, 0);
			knife.setCustomName(event.getPlayer().getName());
			knife.setBounce(false);
			
		}
		if(event.getPlayer().getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if(System.currentTimeMillis() - ChampCooldowns.axeLeapCooldown.get(event.getPlayer()) > (1000 * ChampList.axeLeapCooldown)) {
				event.getPlayer().setVelocity(new Vector(event.getPlayer().getVelocity().getX() + event.getPlayer().getLocation().getDirection().getX()*1.3, event.getPlayer().getVelocity().getY() + event.getPlayer().getLocation().getDirection().getY()*1.3, event.getPlayer().getVelocity().getZ() + event.getPlayer().getLocation().getDirection().getZ()*1.3));
				ChampCooldowns.axeLeapCooldown.replace(event.getPlayer(), System.currentTimeMillis());
			}
		}
	}
	@EventHandler
	public static void playerJoinEvent(PlayerJoinEvent event) {
		
		ChampList.playerChamp.remove(event.getPlayer());
		event.getPlayer().getInventory().clear();
		event.getPlayer().setLevel(0);
		event.getPlayer().setHealth(20);
		event.getPlayer().setFoodLevel(5);
		
		ChampCooldowns.knifeSwingCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.swordSwingCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.axeSwingCooldown.put(event.getPlayer(), (long)0);
		ChampCooldowns.axeLeapCooldown.put(event.getPlayer(), (long)0);
		
	}
	@EventHandler
	public static void playerQuitEvent(PlayerQuitEvent event) {
		
		ChampCooldowns.knifeSwingCooldown.remove(event.getPlayer());
		ChampCooldowns.swordSwingCooldown.remove(event.getPlayer());
		ChampCooldowns.axeSwingCooldown.remove(event.getPlayer());
		ChampCooldowns.axeLeapCooldown.remove(event.getPlayer());
		
	}
	@EventHandler
	public static void playerDeathEvent(PlayerDeathEvent event) {
		
		event.setDeathMessage("");
		
	}
	@EventHandler
	public static void playerRespawnEvent(PlayerRespawnEvent event) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), new Runnable() {
		    @Override
		    public void run() {
		        event.getPlayer().setFoodLevel(5);
		    }
		}, 1L);	
	}
	@EventHandler
	public static void damageEvent(EntityDamageEvent event) {
		
		event.setCancelled(true);

	}
}