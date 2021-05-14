package descent.champions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import descent.Ray;

public class Deputy extends Champ {
	public static final double MAX_HEALTH = 200;
	public static final String CHAMP_NAME = "Deputy";
	public static final float MOVE_SPEED = 0.25f;
	public static final double NATURAL_REGEN = 2.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.NETHERITE_HOE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, new ItemStack(Material.CHAINMAIL_LEGGINGS), null,
			null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = null;

	// Damage
	public static final int GUN_DAMAGE = 54;
	// Cool downs
	public static final float SHOOT_COOLDOWN = 0.7f;
	private long timeAtLastShot;

	public Deputy(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastShot = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastShot > (1000 * SHOOT_COOLDOWN))) {
			PLAYER.getWorld().playSound(PLAYER.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f);
			Ray.playerDamageRayCast(PLAYER, 99);
			timeAtLastShot = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityHitscan(Champ champ, boolean headShot) {
		int totalDamage = GUN_DAMAGE;
		if (headShot) {
			totalDamage = totalDamage * 2;
		}
		champ.takeDamage(totalDamage);
	}

}
