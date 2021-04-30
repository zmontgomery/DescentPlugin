package descent.champions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import descent.Ray;

public class Avatar extends Champ {
	public static final double MAX_HEALTH = 400;
	public static final String CHAMP_NAME = "Avatar";
	public static final float MOVE_SPEED = 0.4f;
	public static final float SHOOT_COOLDOWN = 0.1f;
	public static final int GUN_DAMAGE = 64;
	public static final float AXE_COOLDOWN = 0.75f;
	public static final float VERT_LEAP_STRENGTH = 1.5f;
	public static final float HORIZ_LEAP_STRENGTH = 1.5f;
	public static final int VELOCITY_DAMAGE = 25;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.NETHERITE_HOE),
			new ItemStack(Material.GOLDEN_AXE), new ItemStack(Material.BOW) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, new ItemStack(Material.CHAINMAIL_LEGGINGS), null,
			null };
	public static final ItemStack LEFT_HAND = new ItemStack(Material.ARROW, 64);

	public Avatar(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND);
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)) {
			PLAYER.getWorld().playSound(PLAYER.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1f);
			Ray.playerDamageRayCast(PLAYER, 99);
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)) {
			PLAYER.setVelocity(new Vector(PLAYER.getLocation().getDirection().getX() * HORIZ_LEAP_STRENGTH,
					PLAYER.getLocation().getDirection().getY() * VERT_LEAP_STRENGTH,
					PLAYER.getLocation().getDirection().getZ() * HORIZ_LEAP_STRENGTH));
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

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		champ.takeDamage(85);
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE) {
			double velocity = Math.abs(PLAYER.getVelocity().getX()) + Math.abs(PLAYER.getVelocity().getY())
					+ Math.abs(PLAYER.getVelocity().getZ());
			PLAYER.sendMessage(velocity + "");
			int damage;
			if (velocity > 1) {
				damage = 65 + VELOCITY_DAMAGE;
				PLAYER.playSound(defend.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
			} else {
				damage = 65;
				PLAYER.playSound(defend.getLocation(), Sound.ITEM_SHIELD_BREAK, 1f, 1f);
			}
			defend.playSound(defend.getLocation(), Sound.ENTITY_GENERIC_HURT, 1f, 1f);
			champ.takeDamage(damage);
		}
	}

	@Override
	public void bow(double force) {
		if (force == 1.0f) {

			Arrow arrow = PLAYER.getWorld().spawnArrow(PLAYER.getEyeLocation(), PLAYER.getLocation().getDirection(), 5,
					0);

			arrow.setCritical(true);
			arrow.setCustomName(PLAYER.getName());

		}
	}
}
