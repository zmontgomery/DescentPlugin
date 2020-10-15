package descent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DescentListener implements Listener{
	public long timestart = 0;
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
		event.setJoinMessage("Welcome, " + event.getPlayer().getName() + ", to the Descent Project!");
    }
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
    {
		event.setQuitMessage("Goodbye, " + event.getPlayer().getName() + "...");
    }
	@EventHandler
	public void onSwordSwing(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) && event.getItem().getType() == Material.IRON_SWORD) {
			if(event.getPlayer().getWorld().getTime() - 60 > timestart) {
				Bukkit.broadcastMessage("ACTIVATE!");
			}
		    timestart = event.getPlayer().getWorld().getTime();
		}
	}
//	@EventHandler
//	public void onRightClick(PlayerInteractEvent event)
//    {
//		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem().getType() == Material.BOW) {
//			Bukkit.broadcastMessage("True");
//		
//		}
//    }
}