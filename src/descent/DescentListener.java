package descent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DescentListener implements Listener{
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
		event.setJoinMessage("Welcome, " + event.getPlayer().getName() + ", to the Descent Project!");
    }
}