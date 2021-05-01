package descent.champions;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import descent.Ray;

public class ShaneLee extends Champ {
	public static final double MAX_HEALTH = 325;
	public static final String CHAMP_NAME = "Lee Shane";
	public static final float MOVE_SPEED = 0.25f;
	public static final double NATURAL_REGEN = 8.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { null, new ItemStack(Material.FEATHER), new ItemStack(Material.SLIME_BALL)};
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.GOLDEN_BOOTS), null,
			new ItemStack(Material.GOLDEN_CHESTPLATE), null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_NETHERITE_BLOCK_BREAK;

	// Damage
	public static final int PUNCH_DAMAGE = 62;
	public static final int SONIC_KICK_DAMAGE = 100;
	public static final int SAFE_HEAL_AMOUNT = 20;
	// Cool downs
	public static final float PUNCH_COOLDOWN = 1.2f;
	public static final float SONIC_WAVE_COOLDOWN = 6.0f;
	public static final float SAFE_COOLDOWN = 4.0f;

	private long timeAtLastPunch;
	private long timeAtLastSonicWave;
	private long timeAtLastSafe;
	
	private Champ marked;

	public ShaneLee(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastPunch = System.currentTimeMillis() - (int) (1000 * PUNCH_COOLDOWN);
		timeAtLastSonicWave = System.currentTimeMillis() - (int) (1000 * SONIC_WAVE_COOLDOWN);
		marked = null;
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.AIR
				&& (System.currentTimeMillis() - timeAtLastPunch > (1000 * PUNCH_COOLDOWN))) {
			PLAYER.playSound(defend.getLocation(), Sound.BLOCK_ANCIENT_DEBRIS_BREAK, 1f, 1f);
			champ.takeDamage(PUNCH_DAMAGE);
			timeAtLastPunch = System.currentTimeMillis();
		}
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.FEATHER
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSonicWave > (1000 * SONIC_WAVE_COOLDOWN))) {
			
			Arrow arrow = PLAYER.getWorld().spawnArrow(PLAYER.getEyeLocation(), PLAYER.getLocation().getDirection(), 2,
					0);
			arrow.setShooter(PLAYER);
			arrow.setBounce(false);
			arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			arrow.setGravity(false);
			arrow.setCritical(true);
			arrow.setCustomName("MARK");
			
			timeAtLastSonicWave = System.currentTimeMillis();
		} else if(PLAYER.getInventory().getItemInMainHand().getType() == Material.WHITE_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)) {
				PLAYER.getInventory().setItem(1, new ItemStack(Material.FEATHER));
				marked.takeDamage(SONIC_KICK_DAMAGE);
				PLAYER.teleport(marked.PLAYER);
				marked = null;
				timeAtLastSonicWave = System.currentTimeMillis();
		} else if(PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN))){
					this.heal(SAFE_HEAL_AMOUNT);
					timeAtLastSafe = System.currentTimeMillis();
		} else if(PLAYER.getInventory().getItemInMainHand().getType() == Material.SLIME_BALL
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastSafe > (1000 * SAFE_COOLDOWN))){
					Ray.safeRayCast(PLAYER, 10);
		}
	}
	
	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		if (projectile instanceof Arrow) {
			Arrow arrow = (Arrow)projectile;
			if(arrow.getCustomName().equals("MARK")) {
				PLAYER.getInventory().setItem(1, new ItemStack(Material.WHITE_DYE));
				this.marked = champ;
			}
		} else if(projectile == null) {
			PLAYER.teleport(champ.PLAYER);
			champ.heal(SAFE_HEAL_AMOUNT);
			this.heal(SAFE_HEAL_AMOUNT);
			timeAtLastSafe = System.currentTimeMillis();
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " MARKED=" + this.marked;
	}
	
}
