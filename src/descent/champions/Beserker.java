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
	public static final double NATURAL_REGEN = 4.5;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GOLDEN_AXE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.CHAINMAIL_BOOTS), null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_GILDED_BLACKSTONE_STEP;
	public static final float HURT_PITCH = 3.0f;
	
	public static final Sound LEAP_SOUND = Sound.ENTITY_WOLF_GROWL;
	public static final Sound AXE_SOUND = Sound.ITEM_SHIELD_BLOCK;
	public static final Sound BLOOD_SOUND = Sound.ENTITY_ZOMBIFIED_PIGLIN_ANGRY;
	
	// Damage
	public static final float VERT_LEAP_STRENGTH = 1.7f;
	public static final float HORIZ_LEAP_STRENGTH = 1.5f;
	public static final short VELOCITY_MULTIPLIER = 35;
	public static final short AXE_DAMAGE = 42;

	// Cool downs
	public static final float AXE_COOLDOWN = 0.8f;
	public static final float AXE_LEAP_COOLDOWN = 4.1f;
	private long timeAtLastSwing;
	private long timeAtLastLeap;

	public Beserker(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND, HURT_PITCH);
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
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), LEAP_SOUND, 1f, 0.9f);
			}
		}
	}

	@Override
	public void abilityMelee(Champ champ) {
		Player defend = champ.PLAYER;
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_AXE
				&& (System.currentTimeMillis() - timeAtLastSwing > (1000 * AXE_COOLDOWN)) && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName().equals(Champ.BOARD.getEntryTeam(defend.getName()).getName())) {
			double velocity = Math.abs(PLAYER.getVelocity().getX()) + Math.abs(PLAYER.getVelocity().getY())
					+ Math.abs(PLAYER.getVelocity().getZ());
			int damage = AXE_DAMAGE;
			damage += (velocity * VELOCITY_MULTIPLIER);
			if (velocity > 1.1) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.playSound(defend.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 0.5f);
				}
			} 
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), AXE_SOUND, 0.5f, 2f);
			}
			if(champ.takeDamage(damage)) {
				onKill(champ);
			}
			onHit();
			timeAtLastSwing = System.currentTimeMillis();
		}
	}
	
	@Override
	public void onKill(Champ champ) {
		super.onKill(champ);
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(PLAYER.getLocation(), BLOOD_SOUND, 3f, 1.2f);
		}
		heal(20);
		timeAtLastLeap = 0;
	}
	
}
