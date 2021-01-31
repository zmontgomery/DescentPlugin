package descent;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
    	
    	getServer().getPluginManager().registerEvents(new DescentListener(), this);
    	getCommand("champion").setExecutor(new CommandChamp());
    	
    }
    @Override
    public void onDisable() {
    	
    }   
}
