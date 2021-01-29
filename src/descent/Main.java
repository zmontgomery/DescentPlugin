package descent;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
	static float timeElapsed = 0;
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(new DescentListener(), this);
    	this.getCommand("hello").setExecutor(new CommandHello());
        
    }
    @Override
    public void onDisable() {

    }
}
