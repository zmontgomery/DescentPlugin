package descent;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(new DescentListener(), this);
    	this.getCommand("resetnexus").setExecutor(new CommandResetNexus());
  
    }
    
    @Override
    public void onDisable() {

    }
    
//    public static void respawnHunger(Player pl) {
//		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(Main.class), new Runnable() {
//		    @Override
//		    public void run() {
//		    	pl.setFoodLevel(5);
//		    }
//		}, 20L); //20 Tick (1 Second) delay before run() is called
//    }
    
}
