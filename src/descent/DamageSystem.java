package descent;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class DamageSystem {
	
	public static void damagePlayerMelee(Player plattack, Player pldefend) {
		
		if(plattack.getInventory().getItemInMainHand().getType() == Material.IRON_SWORD) {
			
			if(System.currentTimeMillis() - ChampCooldowns.swordSwingCooldown.get(plattack) > (1000 * ChampList.swordSwingCooldown)) {
			
				int damage = ChampList.sword.baseDamage;
				
				ChampCooldowns.swordSwingCooldown.replace(plattack, System.currentTimeMillis());
				
				plattack.playSound(pldefend.getLocation(), Sound.BLOCK_STONE_BREAK, 1f, 1f);
			
				if(pldefend.getLevel() <= damage) {
				
					playerKill(plattack, pldefend);
				
				} else {
				
					pldefend.setLevel(pldefend.getLevel() - damage);
					ChampConstructor cc = ChampList.playerChamp.get(pldefend);
					pldefend.setHealth(20 * ((double)pldefend.getLevel()/cc.maxHealth));
				
				}
			}
		}
	}
	
	public static void damagePlayerProjectile(Player plattack, Player pldefend, Projectile p) {
		
		if(plattack.getInventory().getItemInMainHand().getType() == Material.ARROW) {
			
			int damage = ChampList.knife.baseDamage;
			
			p.remove();
			
			if(pldefend.getLevel() <= damage) {
				
				playerKill(plattack, pldefend);
				
			} else {
				
				pldefend.setLevel(pldefend.getLevel() - damage);
				ChampConstructor cc = ChampList.playerChamp.get(pldefend);
				pldefend.setHealth(20 * ((double)pldefend.getLevel()/cc.maxHealth));
				
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
