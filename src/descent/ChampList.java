package descent;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class ChampList {
	
	public static HashMap<Player, ChampConstructor> playerChamp = new HashMap<Player, ChampConstructor>();
	
	//CHAMPION STATS
		//KNIFE
	final static String knifeName = "Impaler";
	final static int knifeMaxHealth = 200;
	final static float knifeMoveSpeed = 0.27f;
	final static int knifeBaseDamage = 36;
	
	public final static double knifeSwingCooldown = 0.75;
	public final static double knifeTrapCooldown = 10.0;
	
	final static ItemStack[] knifeItems = new ItemStack[]{new ItemStack(Material.WOODEN_SWORD)};
	final static ItemStack[] knifeArmor = new ItemStack[]{null, null, null, new ItemStack(Material.LEATHER_HELMET)};
	final static ItemStack knifeOffHand = new ItemStack(Material.WOODEN_SWORD);
	
		//SWORD
	final static String swordName = "Knight";
	final static int swordMaxHealth = 300;
	final static float swordMoveSpeed = 0.23f;
	final static int swordBaseDamage = 45;
	final static int swordShieldMaxHealth = 300;
	
	public final static double swordSwingCooldown = 0.4;
	
	final static ItemStack[] swordItems = new ItemStack[]{new ItemStack(Material.IRON_SWORD)};
	final static ItemStack[] swordArmor = new ItemStack[]{new ItemStack(Material.IRON_BOOTS), new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_CHESTPLATE), new ItemStack(Material.IRON_HELMET)};
	final static ItemStack swordOffHand = new ItemStack(Material.SHIELD);
	
		//AXE
	final static String axeName = "Beserker";
	final static int axeMaxHealth = 225;
	final static float axeMoveSpeed = 0.26f;
	final static int axeBaseDamage = 65;
	
	public final static double axeLeapStrengthVert = 1.5;
	public final static double axeLeapStrengthHoriz = 1.5;
	public final static int axeVelocityDamage = 25;
	
	public final static double axeSwingCooldown = 0.75;
	public final static double axeLeapCooldown = 4.0;
	
	final static ItemStack[] axeItems = new ItemStack[]{new ItemStack(Material.GOLDEN_AXE)};
	final static ItemStack[] axeArmor = new ItemStack[]{new ItemStack(Material.CHAINMAIL_BOOTS), null, null, new ItemStack(Material.CHAINMAIL_HELMET)};
	final static ItemStack axeOffHand = null;
	
		//GUN
	final static String gunName = "Deputy";
	final static int gunMaxHealth = 200;
	final static float gunMoveSpeed = 0.25f;
	final static int gunBaseDamage = 49;
	
	public final static double gunShootCooldown = 0.7;
	public final static double gunHealCooldown = 10.0;
	
	final static ItemStack[] gunItems = new ItemStack[]{new ItemStack(Material.NETHERITE_HOE)};
	final static ItemStack[] gunArmor = new ItemStack[]{null, new ItemStack(Material.CHAINMAIL_LEGGINGS), null, null};
	final static ItemStack gunOffHand = null;
	
		//BOW
	final static String bowName = "Archer";
	final static int bowMaxHealth = 150;
	final static float bowMoveSpeed = 0.25f;
	final static int bowBaseDamage = 90;

	public final static double bowShootCooldown = 0.7;

	final static ItemStack[] bowItems = new ItemStack[]{new ItemStack(Material.BOW)};
	final static ItemStack[] bowArmor = new ItemStack[]{null, null, null, new ItemStack(Material.CHAINMAIL_HELMET)};
	final static ItemStack bowOffHand = new ItemStack(Material.ARROW, 64);
	
	//CHAMPION INITIALIZATION
	
	public static ChampConstructor knife = new ChampConstructor(knifeName, knifeMaxHealth, knifeMoveSpeed, knifeBaseDamage, knifeItems, knifeArmor, knifeOffHand);
	public static ChampConstructor sword = new ChampConstructor(swordName, swordMaxHealth, swordMoveSpeed, swordBaseDamage, swordItems, swordArmor, swordOffHand);
	public static ChampConstructor axe = new ChampConstructor(axeName, axeMaxHealth, axeMoveSpeed, axeBaseDamage, axeItems, axeArmor, axeOffHand);
	public static ChampConstructor gun = new ChampConstructor(gunName, gunMaxHealth, gunMoveSpeed, gunBaseDamage, gunItems, gunArmor, gunOffHand);
	public static ChampConstructor bow = new ChampConstructor(bowName, bowMaxHealth, bowMoveSpeed, bowBaseDamage, bowItems, bowArmor, bowOffHand);
	
	public static void setChamp(Player pl, ChampConstructor cc) {
		
		ScoreboardManager m = Bukkit.getScoreboardManager();
		Scoreboard b = m.getMainScoreboard();
		
		if(b.getEntryTeam(pl.getName()).getName() != "spec") {
		
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
			
			if(cc == ChampList.sword) {
				
				if(ChampCooldowns.swordShieldHealth.containsKey(pl)) {
					
					ChampCooldowns.swordShieldHealth.replace(pl, 300);
				
				} else {
				
					ChampCooldowns.swordShieldHealth.put(pl, 300);
				
				}
			}
			
		} else {
			
			pl.sendMessage("Please pick a team before a champion.");
			
		}
	}
}
