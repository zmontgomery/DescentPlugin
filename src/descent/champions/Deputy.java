package descent.champions;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class Deputy extends Champ {
	public static final double MAX_HEALTH = 200;
	public static final String CHAMP_NAME = "Deputy";
	public static final float MOVE_SPEED = 0.255f;
	public static final double NATURAL_REGEN = 2.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.NETHERITE_HOE),
			new ItemStack(Material.RED_DYE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, new ItemStack(Material.CHAINMAIL_LEGGINGS), null,
			null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_ARMOR_STAND_BREAK;
	public static final float HURT_PITCH = 3.0f;

	public static final Sound HEAL_SOUND = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;

	// Damage
	public static final int GUN_DAMAGE = 30;
	// Cool downs
	public static final float SHOOT_COOLDOWN = 0.35f;
	public static final float HEAL_COOLDOWN = 9.0f;

	public static final int HEAL_AMOUNT = 50;
	public static final int SHOT_DISTANCE = 100;

	private long timeAtLastShot;
	private long timeAtLastHeal;

	public Deputy(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND,
				HURT_PITCH);
		timeAtLastShot = 0;
		timeAtLastHeal = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.NETHERITE_HOE
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastShot > (1000 * SHOOT_COOLDOWN))) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1f, 1.6f);
			}
			shoot();
			timeAtLastShot = System.currentTimeMillis();
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.RED_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastHeal > (1000 * HEAL_COOLDOWN))) {

			Collection<Entity> entities = PLAYER.getWorld().getNearbyEntities(PLAYER.getLocation(), 6, 6, 6);
			for (Entity e : entities) {
				if (e instanceof Player) {
					Player player = (Player) e;
					Champ champ = Champ.getChamp(player);
					if (Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
							.equals(Champ.BOARD.getEntryTeam(player.getName()).getName())) {
						champ.heal(HEAL_AMOUNT);
					}
				}
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), HEAL_SOUND, 1f, 0.6f);
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
		if (champ != this) {
			if (champ.takeDamage(totalDamage)) {
				onKill(champ);
			}
			onHit();
		}
	}

	private void shoot() {
		boolean headShot = false;

		World w = PLAYER.getWorld();
		Location l = new Location(w, PLAYER.getLocation().getX(), PLAYER.getLocation().getY() + PLAYER.getEyeHeight(),
				PLAYER.getLocation().getZ());
		Vector v = PLAYER.getLocation().getDirection();

		double x = v.getX();
		double y = v.getY();
		double z = v.getZ();

		for (double i = 0; i < SHOT_DISTANCE; i = i + 0.1) {

			Location bulletLocation = new Location(w, l.getX() + (i * x), l.getY() + (i * y), l.getZ() + (i * z));

			Collection<Entity> entities = w.getNearbyEntities(bulletLocation, 0.05, 0.05, 0.05);

			if (i > 1) {

				if (PLAYER.getWorld().getBlockAt(bulletLocation).getType().isSolid() == false) {
					w.spawnParticle(Particle.CRIT, bulletLocation.getX(), bulletLocation.getY(), bulletLocation.getZ(),
							1, 0, 0, 0, 0);
					for (Entity ent : entities) {
						if (ent instanceof Player) {
							Player hit = (Player) ent;
							if (!Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
									.equals(Champ.BOARD.getEntryTeam(hit.getName()).getName())) {
								if (Math.abs(hit.getEyeLocation().getY() - bulletLocation.getY()) < 0.24) {
									headShot = true;
									PLAYER.playSound(PLAYER.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1f, 2.2f);
								}
								Champ pldefend = Champ.getChamp(hit);
								this.abilityHitscan(pldefend, headShot);
							}

							return;
						}
					}

				} else {

					return;

				}
			}
		}
	}

}
