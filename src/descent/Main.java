package descent;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(new DescentListener(), this);
    	this.getCommand("hello").setExecutor(new CommandHello());
    }
    @Override
    public void onDisable() {

    }
    
}
