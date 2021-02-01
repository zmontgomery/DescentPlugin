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
	final static float knifeMoveSpeed = 0.29f;
	final static int knifeBaseDamage = 20;
	
	public final static double knifeSwingCooldown = 0.25;
	
	final static ItemStack[] knifeItems = new ItemStack[]{new ItemStack(Material.ARROW)};
	final static ItemStack[] knifeArmor = new ItemStack[]{null, null, null, new ItemStack(Material.LEATHER_HELMET)};
	final static ItemStack knifeOffHand = null;
	
		//SWORD
	final static String swordName = "Sword";
	final static int swordMaxHealth = 300;
	final static float swordMoveSpeed = 0.23f;
	final static int swordBaseDamage = 40;
	final static double shieldDamageReduction = 0.8;
	
	public final static double swordSwingCooldown = 0.5;
	
	final static ItemStack[] swordItems = new ItemStack[]{new ItemStack(Material.IRON_SWORD)};
	final static ItemStack[] swordArmor = new ItemStack[]{null, null, new ItemStack(Material.IRON_CHESTPLATE), null};
	final static ItemStack swordOffHand = new ItemStack(Material.SHIELD);
	
		//AXE
	final static String axeName = "Axe";
	final static int axeMaxHealth = 225;
	final static float axeMoveSpeed = 0.26f;
	final static int axeBaseDamage = 50;
	
	public final static double axeSwingCooldown = 0.8;
	public final static double axeLeapCooldown = 5.0;
	
	final static ItemStack[] axeItems = new ItemStack[]{new ItemStack(Material.GOLDEN_AXE)};
	final static ItemStack[] axeArmor = new ItemStack[]{new ItemStack(Material.CHAINMAIL_BOOTS), null, null, new ItemStack(Material.CHAINMAIL_HELMET)};
	final static ItemStack axeOffHand = null;
	
		//GUN
	final static String gunName = "Gun";
	final static int gunMaxHealth = 200;
	final static float gunMoveSpeed = 0.25f;
	final static int gunBaseDamage = 75;
	
	public final static double gunShootCooldown = 2.0;
	public final static double gunHealCooldown = 5.0;
	
	final static ItemStack[] gunItems = new ItemStack[]{new ItemStack(Material.NETHERITE_HOE)};
	final static ItemStack[] gunArmor = new ItemStack[]{null, new ItemStack(Material.CHAINMAIL_LEGGINGS), null, null};
	final static ItemStack gunOffHand = null;
	
	//CHAMPION INITIALIZATION
	
	public static ChampConstructor knife = new ChampConstructor(knifeName, knifeMaxHealth, knifeMoveSpeed, knifeBaseDamage, knifeItems, knifeArmor, knifeOffHand);
	public static ChampConstructor sword = new ChampConstructor(swordName, swordMaxHealth, swordMoveSpeed, swordBaseDamage, swordItems, swordArmor, swordOffHand);
	public static ChampConstructor axe = new ChampConstructor(axeName, axeMaxHealth, axeMoveSpeed, axeBaseDamage, axeItems, axeArmor, axeOffHand);
	public static ChampConstructor gun = new ChampConstructor(gunName, gunMaxHealth, gunMoveSpeed, gunBaseDamage, gunItems, gunArmor, gunOffHand);
	
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
		pl.getInventory().setItemInOffHand(cc.offHand);
		
		if(playerChamp.containsKey(pl)) {
			
			playerChamp.replace(pl, cc);
			
		} else {
			
			playerChamp.put(pl, cc);
			
		}
	}
}
