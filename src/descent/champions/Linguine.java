package descent.champions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Linguine extends Champ{
	
	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Linguine";
	public static final float MOVE_SPEED = 0.29f;
	public static final double NATURAL_REGEN = 5.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.BLAZE_POWDER) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.CHAINMAIL_BOOTS), null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_GILDED_BLACKSTONE_STEP;
	
	public Linguine(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
	}
	
	
	
}
