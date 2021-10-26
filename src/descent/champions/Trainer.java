package descent.champions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Trainer extends Champ {
	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Trainer";
	public static final float MOVE_SPEED = 0.29f;
	public static final double NATURAL_REGEN = 8.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { null, new ItemStack(Material.YELLOW_DYE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, null, null, null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_SKELETON_STEP;
	public static final float HURT_PITCH = 0.4f;

	// Sound
	public static final Sound SUN_SOUND = Sound.ENTITY_LIGHTNING_BOLT_THUNDER;
	public static final Sound PUNCH_SOUND = Sound.BLOCK_GRAVEL_HIT;

	// Damage
	public static final float PUNCH_DAMAGE = 30.0f;
	public static final float SUN_DAMAGE = 40.0f;

	// Cool downs
	public static final float SUN_COOLDOWN = 3.5f;
	public static final float PUNCH_COOLDOWN = 0.45f;
	private long timeAtLastPunch;
	private long timeAtLastSun;

	public Trainer(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND,
				HURT_PITCH);
		timeAtLastPunch = 0;
		timeAtLastSun = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.YELLOW_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSun > (1000 * SUN_COOLDOWN))) {
			Entity ent = PLAYER.getWorld().spawnEntity(
					new Location(PLAYER.getWorld(), PLAYER.getLocation().getX(),
							PLAYER.getLocation().getY() + PLAYER.getEyeHeight(), PLAYER.getLocation().getZ()),
					EntityType.SNOWBALL);
			Snowball sun = (Snowball) ent;
			Vector speed = PLAYER.getLocation().getDirection();
			speed.multiply(4);
			sun.setShooter(PLAYER);
			sun.setCustomName(PLAYER.getName());
			sun.setVelocity(speed);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), SUN_SOUND, 1f, 1f);
			}

			timeAtLastSun = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityMelee(Champ champ) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.AIR
				&& (System.currentTimeMillis() - timeAtLastPunch > (1000 * PUNCH_COOLDOWN))
				&& !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
						.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			if (champ.takeDamage(PUNCH_DAMAGE)) {
				onKill(champ);
			}

			onHit();
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), PUNCH_SOUND, 2f, 1.2f);
			}

			timeAtLastPunch = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		if (projectile instanceof Snowball && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
				.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			if (champ.takeDamage(SUN_DAMAGE)) {
				onKill(champ);
			}
			onHit();
		}
	}

}
