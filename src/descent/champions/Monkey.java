package descent.champions;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import descent.Main;

public class Monkey extends Champ {
	public static final double MAX_HEALTH = 225;
	public static final String CHAMP_NAME = "Monkey";
	public static final float MOVE_SPEED = 0.27f;
	public static final double NATURAL_REGEN = 4.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { null, new ItemStack(Material.CROSSBOW),
			new ItemStack(Material.GOLDEN_BOOTS) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.LEATHER_BOOTS), null, null,
			null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_ZOMBIE_AMBIENT;
	public static final float HURT_PITCH = 3.0f;
	public static final Sound PUNCH_SOUND = Sound.BLOCK_GRAVEL_HIT;
	public static final Sound GUN_SOUND = Sound.ENTITY_FIREWORK_ROCKET_BLAST;
	public static final Sound BANANA_SOUND = Sound.ENTITY_EGG_THROW;

	private long timeAtLastPunch;
	private long timeAtLastShoot;
	private long timeAtLastBanana;

	public static final int SHOOT_DAMAGE = 62;
	public static final int PUNCH_DAMAGE = 17;

	public static final float MELEE_COOLDOWN = 0.11f;
	public static final float SHOOT_COOLDOWN = 1.5f;
	public static final float BANANA_COOLDOWN = 5.0f;

	public static final float GUN_CHARGE_TIME = 0.85f;

	public static final float STUN_TIME = 1.8f;

	public Monkey(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND,
				HURT_PITCH);
		timeAtLastPunch = 0;
		timeAtLastShoot = 0;
		timeAtLastBanana = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.CROSSBOW
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastShoot > (1000 * SHOOT_COOLDOWN))) {

			stun(GUN_CHARGE_TIME);
			timeAtLastShoot = System.currentTimeMillis();
			Runnable timer = new Runnable() {
				@Override
				public void run() {
					Arrow bullet = PLAYER.getWorld()
							.spawnArrow(new Location(PLAYER.getWorld(), PLAYER.getLocation().getX(),
									PLAYER.getLocation().getY() + PLAYER.getEyeHeight(), PLAYER.getLocation().getZ()),
									PLAYER.getLocation().getDirection(), 6, 0);
					bullet.setShooter(PLAYER);
					bullet.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
					bullet.setCustomName(PLAYER.getName());
					bullet.setBounce(false);
					for (Player player : Bukkit.getOnlinePlayers()) {
						player.playSound(PLAYER.getLocation(), GUN_SOUND, 0.3f, 0.8f);
					}
					timeAtLastShoot = System.currentTimeMillis();
				}

			};
			Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), timer, (long) (GUN_CHARGE_TIME * 20));

		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GOLDEN_BOOTS
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastBanana > (1000 * BANANA_COOLDOWN))) {

			ThrownPotion tp = (ThrownPotion) PLAYER.getWorld().spawnEntity(PLAYER.getEyeLocation(),
					EntityType.SPLASH_POTION);

			ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
			PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();

			potionMeta.setColor(Color.YELLOW);

			potionItem.setItemMeta(potionMeta);

			tp.setItem(potionItem);
			tp.setBounce(true);
			tp.setShooter(PLAYER);

			tp.setVelocity(PLAYER.getLocation().getDirection());
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), BANANA_SOUND, 1f, 0.9f);
			}

			timeAtLastBanana = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		if (projectile instanceof Arrow && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
				.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			if (champ.takeDamage(SHOOT_DAMAGE)) {
				onKill(champ);
			}
			onHit();
		}
	}

	@Override
	public void abilityMelee(Champ champ) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.AIR
				&& (System.currentTimeMillis() - timeAtLastPunch > (1000 * MELEE_COOLDOWN))
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
	public void abilityPotion(ThrownPotion potion, Collection<LivingEntity> hits) {

		for (Entity ent : hits) {
			if (ent instanceof Player) {
				Player player = (Player) ent;
				Champ champ = Champ.getChamp(player);
				if (!Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
						.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
					champ.stun(STUN_TIME);
					player.setVelocity(PLAYER.getVelocity().setY(0.45));
					onHit();
				}
			}
		}
	}

}
