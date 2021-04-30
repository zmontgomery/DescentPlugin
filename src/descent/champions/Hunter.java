package descent.champions;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class Hunter extends Champ {
	public static final double MAX_HEALTH = 150;
	public static final String CHAMP_NAME = "Hunter";
	public static final float MOVE_SPEED = 0.25f;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.BOW) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = new ItemStack(Material.ARROW, 64);
	
	//Damage
	public static final int BOW_DAMAGE = 85;

	public Hunter(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
	}

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		champ.takeDamage(BOW_DAMAGE);
	}

	@Override
	public void bow(double force) {
		if (force == 1.0f) {

			Arrow arrow = PLAYER.getWorld().spawnArrow(PLAYER.getEyeLocation(), PLAYER.getLocation().getDirection(), 5,
					0);
			arrow.setShooter(PLAYER);
			arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			arrow.setCritical(true);
			arrow.setCustomName(PLAYER.getName());

		}
	}
}
