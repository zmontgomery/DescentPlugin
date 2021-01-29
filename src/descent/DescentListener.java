package descent;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.HashMap;

public class DescentListener implements Listener{
	public long currentTime = 0;
	public long timeOfSwing = 0;
	public HashMap<Player, Long> playerCooldowns = new HashMap<Player, Long>();
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
		event.setJoinMessage("Welcome, " + event.getPlayer().getName() + ", to the Descent Project!");
		playerCooldowns.put(event.getPlayer(), event.getPlayer().getWorld().getTime());
    }
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		event.setQuitMessage("Goodbye, " + event.getPlayer().getName() + "...");
		playerCooldowns.remove(event.getPlayer());
    }
	@EventHandler
	public void onSwordSwing(PlayerInteractEvent event) {
		
		if (event.getItem() != null && (event.getAction() == Action.LEFT_CLICK_AIR || 
		event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getItem().getType() == Material.ARROW) {
			
			currentTime = event.getPlayer().getWorld().getTime();
			timeOfSwing = playerCooldowns.get(event.getPlayer());
			
			if(currentTime - timeOfSwing > 10) {
				//ABILITY CODE GOES HERE
				Location pl = new Location(event.getPlayer().getWorld(), event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY() + 1.6, event.getPlayer().getLocation().getZ());
				event.getPlayer().getWorld().spawnArrow(pl, event.getPlayer().getLocation().getDirection(), 3, 3);
				playerCooldowns.replace(event.getPlayer(), currentTime);
				event.getPlayer().setInvisible(true);
				event.getPlayer().setWalkSpeed(1);
				event.getPlayer().setArrowsInBody(1000);
			}
		}
	}
//BOW
//	@EventHandler
//	public void onRightClick(PlayerInteractEvent event)
//    {
//		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem().getType() == Material.BOW) {
//			Bukkit.broadcastMessage("True");
//		
//		}
//    }
}