package descent.champions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import descent.Main;

public class Ninja extends Champ {
	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Ninja";
	public static final float MOVE_SPEED = 0.29f;
	public static final double NATURAL_REGEN = 7.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GOLDEN_SWORD) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_ENDERMAN_HURT;
	
	public static final Sound STAB_SOUND = Sound.ENTITY_PLAYER_ATTACK_SWEEP;
	public static final Sound FLASH_SOUND = Sound.ENTITY_FIREWORK_ROCKET_BLAST;
	public static final Sound CLOAK_SOUND = Sound.ITEM_FIRECHARGE_USE;

	// Damage
	public static final int DAGGAR_DAMAGE = 23;
	// Cool downs
	public static final float DAGGAR_COOLDOWN = 0.15f;
	public static final float FLASH_COOLDOWN = 1.5f;
	public static final float CLOAK_COOLDOWN = 6.0f;

	public static final int FLASH_DISTANCE = 12;

	private long timeAtLastSwing;
	private long timeAtLastFlash;
	private long timeAtLastCloak;

	public Ninja(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastSwing = 0;
		timeAtLastFlash = 0;
		timeAtLastCloak = 0;

//        Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(Main.class), new Runnable() {
//            @Override
//            public void run() {
//            	if(player.isSneaking()) {
//            		player.setVelocity(new Vector(player.getVelocity().getX(), 5, player.getVelocity().getZ()));
//            	}
//            }
//        }, 0L, 1L);
	}

	@Override
	public void abilityMelee(Champ champ) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD
				&& (System.currentTimeMillis() - timeAtLastSwing > (1000 * DAGGAR_COOLDOWN))) {
			champ.takeDamage(DAGGAR_DAMAGE);
			onHit();
			timeAtLastSwing = System.currentTimeMillis();
			if (PLAYER.isInvisible()) {
				PLAYER.setInvisible(false);
				Main.sendEquipmentInvisiblePacket(PLAYER, false);
			}
			PLAYER.playSound(PLAYER.getLocation(), STAB_SOUND, 100, 1.5f);
		}
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastFlash > (1000 * FLASH_COOLDOWN))) {
			teleportRayCast();
			PLAYER.setInvisible(false);
			Main.sendEquipmentInvisiblePacket(PLAYER, false);
			PLAYER.getWorld().playSound(PLAYER.getLocation(), FLASH_SOUND, 100, 1.5f);

			timeAtLastFlash = System.currentTimeMillis();
		}
	}

	@Override
	public boolean takeDamage(double amount) {
		boolean killed = false;
		killed = super.takeDamage(amount);
		if (PLAYER.isInvisible()) {
			PLAYER.setInvisible(false);
			Main.sendEquipmentInvisiblePacket(PLAYER, false);
		}
		return killed;
	}

	@Override
	public void onDrop(Material item) {
		if (item == Material.GOLDEN_SWORD && (System.currentTimeMillis() - timeAtLastCloak > (1000 * CLOAK_COOLDOWN))) {

			PLAYER.setInvisible(true);
			PLAYER.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, PLAYER.getLocation().getX(),
					PLAYER.getLocation().getY() + 1, PLAYER.getLocation().getZ(), 75, 0.5, 1, 0.5, 0, null, true);
			PLAYER.getWorld().playSound(PLAYER.getLocation(), CLOAK_SOUND, 100, 1);
			PLAYER.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true));

			Main.sendEquipmentInvisiblePacket(PLAYER, true);

			new BukkitRunnable() {

				@Override
				public void run() {
					PLAYER.setInvisible(false);
					Main.sendEquipmentInvisiblePacket(PLAYER, false);
				}

			}.runTaskLater(Main.getPlugin(Main.class), 100);
			timeAtLastCloak = System.currentTimeMillis();
		}
		return;
	}

	private void teleportRayCast() {
		World w = PLAYER.getWorld();
		Location l = PLAYER.getEyeLocation();
		Vector v = PLAYER.getLocation().getDirection();

		double x = v.getX();
		double y = v.getY();
		double z = v.getZ();

		Location endLocation = PLAYER.getEyeLocation();

		for (double i = 0; i < FLASH_DISTANCE; i = i + 0.1) {

			endLocation = new Location(w, l.getX() + (i * x), l.getY() + (i * y), l.getZ() + (i * z), l.getYaw(),
					l.getPitch());

			if (i > 1) {

				if (PLAYER.getWorld().getBlockAt(endLocation).getType().isSolid() == false) {

					w.spawnParticle(Particle.CRIT, endLocation.getX(), endLocation.getY(), endLocation.getZ(), 1, 0, 0,
							0, 0);

				} else {
					endLocation = new Location(w, endLocation.getX(), endLocation.getY() - 1, endLocation.getZ());
					PLAYER.teleport(endLocation);
					return;
				}
			}
		}
		PLAYER.teleport(endLocation);

	}
}
