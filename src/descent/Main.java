package descent;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
	static float timeElapsed = 0;
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(new DescentListener(), this);
    	this.getCommand("hello").setExecutor(new CommandHello());
    	BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
            	timeElapsed += 0.5;
            	String s = String.valueOf(timeElapsed);
            	Bukkit.broadcastMessage(s);
            }
        }, 0L, 10L);
    }
    @Override
    public void onDisable() {

    }
    
}
