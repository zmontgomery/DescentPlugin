package descent;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class ChampCooldowns {

	//CHAMPION COOLDOWNS
		//KNIFE
	public static HashMap<Player, Long> knifeSwingCooldown = new HashMap<Player, Long>();
	
		//SWORD
	public static HashMap<Player, Long> swordSwingCooldown = new HashMap<Player, Long>();
		
		//AXE
	public static HashMap<Player, Long> axeSwingCooldown = new HashMap<Player, Long>();
	public static HashMap<Player, Long> axeLeapCooldown = new HashMap<Player, Long>();
	
		//GUN
	public static HashMap<Player, Long> gunShootCooldown = new HashMap<Player, Long>();
	public static HashMap<Player, Long> gunHealCooldown = new HashMap<Player, Long>();
	
}
