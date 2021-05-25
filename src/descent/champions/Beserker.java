package descent.champions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Beserker extends Champ {
	public static final double MAX_HEALTH = 225;
	public static final String CHAMP_NAME = "Beserker";
	public static final float MOVE_SPEED = 0.27f;
	public static final double NATURAL_REGEN = 5.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GOLDEN_AXE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.CHAINMAIL_BOOTS), null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_GILDED_BLACKSTONE_STEP;
	

	// Damage
	public static final float VERT_LEAP_STRENGTH = 1.6f;
	public static final float HORIZ_LEAP_STRENGTH = 1.5f;
	public static final short VELOCITY_MULTIPLIER = 20;
	public static final short AXE_DAMAGE = 55;

	// Cool downs
	public static final float AXE_COOLDOWN = 0.80f;
	public static final float AXE_LEAP_COOLDOWN = 4.0f;
	private long timeAtLastSwing;
	private long timeAtLastLeap;

	public Beserker(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastSwing = 0;
		timeAtLastLeap = 0;
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
			damage += (velocity * VELOCITY_MULTIPLIER);
			if (velocity > 1) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.playSound(defend.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
				}
			} 
			champ.takeDamage(damage);
			onHit();
			timeAtLastSwing = System.currentTimeMillis();
		}
	}
}
