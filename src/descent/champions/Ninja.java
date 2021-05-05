package descent.champions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import descent.Main;
import descent.Ray;

public class Ninja extends Champ {
	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Ninja";
	public static final float MOVE_SPEED = 0.28f;
	public static final double NATURAL_REGEN = 7.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GOLDEN_SWORD) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_ENDERMAN_HURT;
	
	//Damage
	public static final int DAGGAR_DAMAGE = 26;
	//Cool downs
	public static final float DAGGAR_COOLDOWN = 0.15f;
	public static final float FLASH_COOLDOWN = 1.5f;
	private long timeAtLastSwing;
	private long timeAtLastFlash;
	
	public Ninja(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastSwing = System.currentTimeMillis() - (int)(1000 * DAGGAR_COOLDOWN);
		timeAtLastFlash = System.currentTimeMillis() - (int)(1000 * FLASH_COOLDOWN);
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD && (System.currentTimeMillis() - timeAtLastSwing > (1000 * DAGGAR_COOLDOWN))) {
			Bukkit.broadcastMessage(PLAYER.getLocation().getDirection().getX() + " + " + defend.getLocation().getDirection().getX());
			if(PLAYER.getLocation().getDirection().getX() == defend.getLocation().getDirection().getX()) {
				PLAYER.playSound(defend.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1.5f);
				champ.takeDamage(DAGGAR_DAMAGE*2);
				timeAtLastSwing = System.currentTimeMillis();
			} else {
				PLAYER.playSound(defend.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
				champ.takeDamage(DAGGAR_DAMAGE);
				timeAtLastSwing = System.currentTimeMillis();
			}
			if(PLAYER.isInvisible()) {
				PLAYER.setInvisible(false);
				Main.sendEquipmentInvisiblePacket(PLAYER, false);
			}
		}
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK) && (System.currentTimeMillis() - timeAtLastFlash > (1000 * FLASH_COOLDOWN))) {
			Ray.teleportRayCast(PLAYER, 10);
			timeAtLastFlash = System.currentTimeMillis();
			PLAYER.setInvisible(false);
			Main.sendEquipmentInvisiblePacket(PLAYER, false);
		}
	}
}
