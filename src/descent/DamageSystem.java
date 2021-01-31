package descent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DamageSystem {
	
	public static void damagePlayer(Player plattack, Player pldefend) {
		
		if(plattack.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
			
			int damage = ChampList.sword.baseDamage;
			
			if(pldefend.getLevel() <= damage) {
				
				playerKill(plattack, pldefend);
				
			} else {
				
				pldefend.setLevel(pldefend.getLevel() - damage);
				ChampConstructor cc = ChampList.playerChamp.get(pldefend);
				pldefend.setHealth(20 * ((double)pldefend.getLevel()/cc.maxHealth));
				Bukkit.broadcastMessage(20 * ((double)pldefend.getLevel()/cc.maxHealth) + "");
				
			}
		}
		
		if(plattack.getInventory().getItemInMainHand().getType() == Material.ARROW) {
			
			int damage = ChampList.knife.baseDamage;
			
			if(pldefend.getLevel() <= damage) {
				
				playerKill(plattack, pldefend);
				
			} else {
				
				pldefend.setLevel(pldefend.getLevel() - damage);
				ChampConstructor cc = ChampList.playerChamp.get(pldefend);
				pldefend.setHealth(20 * ((double)pldefend.getLevel()/cc.maxHealth));
				Bukkit.broadcastMessage(20 * ((double)pldefend.getLevel()/cc.maxHealth) + "");
				
			}
		}
	}
	public static void playerKill(Player plattack, Player pldefend) {
		
		pldefend.setHealth(0);
		Bukkit.broadcastMessage(plattack.getName() + " has eliminated " + pldefend.getName() + "!");
		ChampConstructor cc = ChampList.playerChamp.get(pldefend);
		pldefend.setLevel(cc.maxHealth);
		
	}
}
