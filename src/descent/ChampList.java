package descent;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ChampList {
	
	public static HashMap<Player, ChampConstructor> playerChamp = new HashMap<Player, ChampConstructor>();
	
	//CHAMPION STATS
		//KNIFE
	final static String knifeName = "Knife";
	final static int knifeMaxHealth = 200;
	final static float knifeMoveSpeed = 0.24f;
	final static int knifeBaseDamage = 24;
	
	final static ItemStack[] knifeItems = new ItemStack[]{new ItemStack(Material.ARROW)};
	final static ItemStack[] knifeArmor = new ItemStack[]{null, null, null, new ItemStack(Material.LEATHER_HELMET)};
	
		//SWORD
	final static String swordName = "Sword";
	final static int swordMaxHealth = 225;
	final static float swordMoveSpeed = 0.24f;
	final static int swordBaseDamage = 40;
	
	final static ItemStack[] swordItems = new ItemStack[]{new ItemStack(Material.IRON_SWORD)};
	final static ItemStack[] swordArmor = new ItemStack[]{null, null, new ItemStack(Material.IRON_CHESTPLATE), null};
	
	//CHAMPION INITIALIZATION
	
	public static ChampConstructor knife = new ChampConstructor(knifeName, knifeMaxHealth, knifeMoveSpeed, knifeBaseDamage, knifeItems, knifeArmor);
	public static ChampConstructor sword = new ChampConstructor(swordName, swordMaxHealth, swordMoveSpeed, swordBaseDamage, swordItems, swordArmor);
	
	public static void setChamp(Player pl, ChampConstructor cc) {
		
		pl.sendMessage(cc.name);
		pl.setHealth(20);
		pl.setFoodLevel(5);
		pl.setExp(0);
		pl.setLevel(cc.maxHealth);
		pl.setWalkSpeed(cc.moveSpeed);
		pl.getInventory().clear();
		pl.getInventory().setContents(cc.items);
		pl.getInventory().setArmorContents(cc.armor);
		
		if(playerChamp.containsKey(pl)) {
			
			playerChamp.replace(pl, cc);
			
		} else {
			
			playerChamp.put(pl, cc);
			
		}
	}
}
