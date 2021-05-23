package descent.champions;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

public class Alchemist extends Champ {
	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Alchemist";
	public static final float MOVE_SPEED = 0.26f;
	public static final double NATURAL_REGEN = 8.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GLASS_BOTTLE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.GOLDEN_BOOTS), null,
			null, null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_GRASS_BREAK;

	// Damage
	public static final int POTION_HEAL = 45;

	// Cool downs
	public static final float POTION_COOLDOWN = 2.0f;

	private long timeAtLastPotion;
	
	public Alchemist(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastPotion = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastPotion > (1000 * POTION_COOLDOWN))) {
			
			ThrownPotion tp = (ThrownPotion) PLAYER.getWorld().spawnEntity(PLAYER.getEyeLocation(), EntityType.SPLASH_POTION);
			
			ItemStack potionItem = new ItemStack(Material.POTION);
			PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta(); 
			
			potionMeta.setColor(Color.RED);
			
			potionItem.setItemMeta(potionMeta);
			
			tp.setItem(potionItem);
			tp.setBounce(true);
			tp.setShooter(PLAYER);
			tp.setCustomName("HEAL");
			
			tp.setVelocity(PLAYER.getLocation().getDirection());
			
			timeAtLastPotion = System.currentTimeMillis();
		}
	}
	
}