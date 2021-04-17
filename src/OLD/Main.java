package OLD;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	public static int controlX;
	public static int controlY;
	public static int controlZ;
	
	public static int blueX;
	public static int blueY;
	public static int blueZ;
	
	public static int redX;
	public static int redY;
	public static int redZ;
	
	public static int blueDoorX1;
	public static int blueDoorY1;
	public static int blueDoorZ1;
	
	public static int blueDoorX2;
	public static int blueDoorY2;
	public static int blueDoorZ2;
	
	public static int redDoorX1;
	public static int redDoorY1;
	public static int redDoorZ1;
	
	public static int redDoorX2;
	public static int redDoorY2;
	public static int redDoorZ2;
	
    @Override
    public void onEnable() {
    	
    	getServer().getPluginManager().registerEvents(new DescentListener(), this);
    	
    	getCommand("champion").setExecutor(new CommandChamp());
    	getCommand("setteam").setExecutor(new CommandSetTeam());
    	
    	this.saveDefaultConfig();
    	
    	FileConfiguration config = this.getConfig();
    	controlX = config.getInt("descentconfig.controlpoint.x");
    	controlY = config.getInt("descentconfig.controlpoint.y");
    	controlZ = config.getInt("descentconfig.controlpoint.z");
    	
    	blueX = config.getInt("descentconfig.blue.x");
    	blueY = config.getInt("descentconfig.blue.y");
    	blueZ = config.getInt("descentconfig.blue.z");
    	
    	redX = config.getInt("descentconfig.red.x");
    	redY = config.getInt("descentconfig.red.y");
    	redZ = config.getInt("descentconfig.red.z");
    	
    	blueDoorX1 = config.getInt("descentconfig.bluedoor.x1");
    	blueDoorY1 = config.getInt("descentconfig.bluedoor.y1");
    	blueDoorZ1 = config.getInt("descentconfig.bluedoor.z1");
    	
    	blueDoorX2 = config.getInt("descentconfig.bluedoor.x2");
    	blueDoorY2 = config.getInt("descentconfig.bluedoor.y2");
    	blueDoorZ2 = config.getInt("descentconfig.bluedoor.z2");
    	
    	redDoorX1 = config.getInt("descentconfig.reddoor.x1");
    	redDoorY1 = config.getInt("descentconfig.reddoor.y1");
    	redDoorZ1 = config.getInt("descentconfig.reddoor.z1");
    	
    	redDoorX2 = config.getInt("descentconfig.reddoor.x2");
    	redDoorY2 = config.getInt("descentconfig.reddoor.y2");
    	redDoorZ2 = config.getInt("descentconfig.reddoor.z2");
    	
    	PlayerTeams.closeSpawnDoors();
    	
    	PlayerTeams.registerTeams();
    	
    }
    @Override
    public void onDisable() {
    	
    	PlayerTeams.closeSpawnDoors();
    	
    	PlayerTeams.unregisterTeams();
    	
    	for(Player pl : Bukkit.getOnlinePlayers()) {
			if(ChampCooldowns.knifeTrapLocation.containsKey(pl))
				pl.getWorld().getBlockAt(ChampCooldowns.knifeTrapLocation.get(pl)).setType(Material.AIR);
    	}
    }
    
}
