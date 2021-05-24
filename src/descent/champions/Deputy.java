package descent.champions;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import descent.Ray;

public class Deputy extends Champ {
	public static final double MAX_HEALTH = 200;
	public static final String CHAMP_NAME = "Deputy";
	public static final float MOVE_SPEED = 0.25f;
	public static final double NATURAL_REGEN = 2.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.NETHERITE_HOE), new ItemStack(Material.RED_DYE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, new ItemStack(Material.CHAINMAIL_LEGGINGS), null,
			null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_ARMOR_STAND_BREAK;

	// Damage
	public static final int GUN_DAMAGE = 40;
	// Cool downs
	public static final float SHOOT_COOLDOWN = 0.65f;
	public static final float HEAL_COOLDOWN = 9.0f;
	
	public static final int HEAL_AMOUNT = 50;
	
	private long timeAtLastShot;
	private long timeAtLastHeal;

	public Deputy(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastShot = 0;
		timeAtLastHeal = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastShot > (1000 * SHOOT_COOLDOWN))) {
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.getWorld().playSound(PLAYER.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1.5f);
			}
			Ray.playerDamageRayCast(PLAYER, 99);
			timeAtLastShot = System.currentTimeMillis();
		} else if(PLAYER.getInventory().getItemInMainHand().getType() == Material.RED_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastHeal > (1000 * HEAL_COOLDOWN))) {
			
			Collection<Entity> entities = PLAYER.getWorld().getNearbyEntities(PLAYER.getLocation(), 6, 6, 6);
			for (Entity e : entities) {
				if (e instanceof Player) {
					Player p = (Player) e;
					Champ c = Champ.getChamp(p);
					c.heal(HEAL_AMOUNT);
				}
			}
			timeAtLastHeal = System.currentTimeMillis();
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
