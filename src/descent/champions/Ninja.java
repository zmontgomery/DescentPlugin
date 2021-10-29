package descent.champions;

import org.bukkit.Bukkit;
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
	public static final double MAX_HEALTH = 150;
	public static final String CHAMP_NAME = "Ninja";
	public static final float MOVE_SPEED = 0.29f;
	public static final double NATURAL_REGEN = 7.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GOLDEN_SWORD) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_ENDERMAN_HURT;
	public static final float HURT_PITCH = 1.0f;

	public static final Sound STAB_SOUND = Sound.ENTITY_PLAYER_ATTACK_SWEEP;
	public static final Sound FLASH_SOUND = Sound.ENTITY_FIREWORK_ROCKET_BLAST;
	public static final Sound CLOAK_SOUND = Sound.ITEM_FIRECHARGE_USE;

	// Damage
	public static final int DAGGAR_DAMAGE = 22;
	// Cool downs
	public static final float DAGGAR_COOLDOWN = 0.17f;
	public static final float FLASH_COOLDOWN = 2.0f;
	public static final float CLOAK_COOLDOWN = 11.0f;

	public static final double FLASH_DISTANCE = 6;
	public static final float LIFE_STEAL = 0.1f;

	private long timeAtLastSwing;
	private long timeAtLastFlash;
	private long timeAtLastCloak;

	public Ninja(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND,
				HURT_PITCH);
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
				&& (System.currentTimeMillis() - timeAtLastSwing > (1000 * DAGGAR_COOLDOWN))
				&& !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
						.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			heal(DAGGAR_DAMAGE * LIFE_STEAL);
			if (champ.takeDamage(DAGGAR_DAMAGE)) {
				onKill(champ);
			}
			onHit();
			timeAtLastSwing = System.currentTimeMillis();
			if (PLAYER.isInvisible()) {
				PLAYER.setInvisible(false);
				Main.sendEquipmentInvisiblePacket(PLAYER, false);
			}
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), STAB_SOUND, 1, 2.5f);
			}

		}
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_SWORD
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastFlash > (1000 * FLASH_COOLDOWN))) {
			double distance = FLASH_DISTANCE;
			if(PLAYER.isSneaking()) {
				distance = distance * 2;
			}
			teleportRayCast(distance);
			PLAYER.setInvisible(false);
			Main.sendEquipmentInvisiblePacket(PLAYER, false);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), FLASH_SOUND, 100, 1.5f);
			}

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
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), CLOAK_SOUND, 100, 1);
			}

			PLAYER.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1, true));
			extinguish();
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

	private void teleportRayCast(double distance) {
		World world = PLAYER.getWorld();
		Location eye = PLAYER.getEyeLocation();
		Vector direction = PLAYER.getLocation().getDirection();

		double x = direction.getX();
		double y = direction.getY();
		double z = direction.getZ();

		Location endLocation = eye;
		Location oldEnd = null;
		int j = 0;
		for (double i = 0; i < distance; i = i + .2) {
			if (j % 5 == 0) {
				oldEnd = new Location(world, eye.getX() + (i * x), eye.getY() + (i * y), eye.getZ() + (i * z), eye.getYaw(),
						eye.getPitch());
			}
			endLocation = new Location(world, eye.getX() + (i * x), eye.getY() + (i * y), eye.getZ() + (i * z), eye.getYaw(),
					eye.getPitch());

			// Particle
			if (isValid(endLocation)) {
				world.spawnParticle(Particle.CRIT, eye, 1, 0, 0, 0, 0);
			} else {
				if (oldEnd != null) {
					oldEnd.setY(oldEnd.getY() - PLAYER.getEyeHeight() + .2);
					PLAYER.teleport(oldEnd);
				}
				return;
			}
			j++;
		}
		endLocation = new Location(world, endLocation.getX(), endLocation.getY() - PLAYER.getEyeHeight() + .2, endLocation.getZ(), eye.getYaw(),
				eye.getPitch());
		PLAYER.teleport(endLocation);
	}

	private boolean isValid(Location loc) {
		double height = PLAYER.getEyeHeight();
		World world = PLAYER.getWorld();
		float safeAura = 0.32f;
		Location[] spots = new Location[7];
		spots[0] = new Location(world, loc.getX() + safeAura, loc.getY(), loc.getZ());
		spots[1] = new Location(world, loc.getX() - safeAura, loc.getY(), loc.getZ());
		spots[2] = new Location(world, loc.getX(), loc.getY() + safeAura, loc.getZ());
		spots[3] = new Location(world, loc.getX(), loc.getY() - height + .1, loc.getZ());
		spots[4] = new Location(world, loc.getX(), loc.getY(), loc.getZ() + safeAura);
		spots[5] = new Location(world, loc.getX(), loc.getY(), loc.getZ() - safeAura);
		spots[6] = loc;
		for(Location spot : spots) {
			Material block = world.getBlockAt(spot).getType();
			if (block != Material.AIR && block != Material.WATER && block != Material.WALL_TORCH && block != Material.TORCH) {
				return false;
			} 
		}
		return true;
	}
}
