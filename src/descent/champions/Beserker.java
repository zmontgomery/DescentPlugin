package descent.champions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Beserker extends Champ {
	public static final double MAX_HEALTH = 225;
	public static final String CHAMP_NAME = "Beserker";
	public static final float MOVE_SPEED = 0.26f;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GOLDEN_AXE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.CHAINMAIL_BOOTS), null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;

	// Damage
	public static final float VERT_LEAP_STRENGTH = 1.5f;
	public static final float HORIZ_LEAP_STRENGTH = 1.5f;
	public static final short VELOCITY_DAMAGE = 25;
	public static final short AXE_DAMAGE = 65;

	// Cool downs
	public static final float AXE_COOLDOWN = 0.75f;
	public static final float AXE_LEAP_COOLDOWN = 4.0f;
	private long timeAtLastSwing;
	private long timeAtLastLeap;

	public Beserker(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
		timeAtLastSwing = System.currentTimeMillis() - (int)(1000 * AXE_COOLDOWN);
		timeAtLastLeap = System.currentTimeMillis() - (int)(1000 * AXE_LEAP_COOLDOWN);
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastLeap > (1000 * AXE_LEAP_COOLDOWN))) {
			PLAYER.setVelocity(new Vector(PLAYER.getLocation().getDirection().getX() * HORIZ_LEAP_STRENGTH,
					PLAYER.getLocation().getDirection().getY() * VERT_LEAP_STRENGTH,
					PLAYER.getLocation().getDirection().getZ() * HORIZ_LEAP_STRENGTH));
			timeAtLastLeap = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE
				&& (System.currentTimeMillis() - timeAtLastSwing > (1000 * AXE_COOLDOWN))) {
			double velocity = Math.abs(PLAYER.getVelocity().getX()) + Math.abs(PLAYER.getVelocity().getY())
					+ Math.abs(PLAYER.getVelocity().getZ());
			int damage = AXE_DAMAGE;
			if (velocity > 1) {
				damage += VELOCITY_DAMAGE;
				PLAYER.playSound(defend.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
			} else {
				PLAYER.playSound(defend.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
			}
			defend.playSound(defend.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			champ.takeDamage(damage);
			timeAtLastSwing = System.currentTimeMillis();
		}
	}
}
