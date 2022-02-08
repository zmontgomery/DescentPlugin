package descent.champions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Hunter extends Champ {
	private enum ArrowType {
		NORMAL, FIRE, ICE
	}

	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Hunter";
	public static final float MOVE_SPEED = 0.31f;
	public static final double NATURAL_REGEN = 10.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.BOW), new ItemStack(Material.ARROW, 2) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, null, null,
			new ItemStack(Material.CHAINMAIL_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ENTITY_ITEM_BREAK;
	public static final float HURT_PITCH = 3.0f;

	public static final Sound SHOOT_SOUND = Sound.ENTITY_ARROW_SHOOT;
	public static final Sound FLAME_ARROW_SOUND = Sound.ENTITY_BLAZE_AMBIENT;
	public static final Sound ICE_ARROW_SOUND = Sound.BLOCK_GLASS_BREAK;

	// Damage
	public static final int BOW_DAMAGE = 75;

	public static final float FIRE_COOLDOWN = 6.0f;
	public static final float ICE_COOLDOWN = 6.0f;

	public static final float ARROW_RUNOUT = 4.0f;

	public static final float FIRE_TIME = 6.0f;
	public static final float SLOW_TIME = 4.0f;

	private ArrowType currentArrow;
	private long timeAtLastFire;
	private long timeAtLastIce;
	Thread arrowTimer;

	public Hunter(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND,
				HURT_PITCH);
		timeAtLastFire = 0;
		timeAtLastIce = 0;
		currentArrow = ArrowType.NORMAL;
		arrowTimer = new Thread();
	}

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		if (!Champ.BOARD.getEntryTeam(PLAYER.getName()).getName()
				.equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			if (projectile.getCustomName().equals(ArrowType.FIRE.toString())) {
				champ.ignite(FIRE_TIME);
			} else if (projectile.getCustomName().equals(ArrowType.ICE.toString())) {
				champ.takeEffect(new PotionEffect(PotionEffectType.SLOW, (int) (20 * SLOW_TIME), 2));
			}
			double velocity = Math.pow((Math.pow(projectile.getVelocity().getX(),2) + Math.pow(projectile.getVelocity().getY(),2) + Math.pow(projectile.getVelocity().getZ(),2)), 0.5) / 6.0;
			if (champ.takeDamage(BOW_DAMAGE * velocity)) {
				onKill(champ);
			}
			
			onHit();
			projectile.remove();
		}
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.BOW
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastFire > (1000 * FIRE_COOLDOWN))) {
			setArrow(ArrowType.FIRE);
			arrowTimer.interrupt();
			PLAYER.playSound(PLAYER.getLocation(), FLAME_ARROW_SOUND, 0.7f, 0.6f);
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep((long) (ARROW_RUNOUT * 1000));
				} catch (InterruptedException e) {
					return;
				}
				setArrow(ArrowType.NORMAL);
			});
			timer.start();
			this.arrowTimer = timer;
			timeAtLastFire = System.currentTimeMillis();
		}

	}

	@Override
	public void onSneak() {
		if (System.currentTimeMillis() - timeAtLastIce > (1000 * ICE_COOLDOWN)) {
			setArrow(ArrowType.ICE);
			arrowTimer.interrupt();
			Thread timer = new Thread(() -> {
				try {
					Thread.sleep((long) (ARROW_RUNOUT * 1000));
				} catch (InterruptedException e) {
					return;
				}
				setArrow(ArrowType.NORMAL);
			});
			timer.start();
			this.arrowTimer = timer;
			PLAYER.playSound(PLAYER.getLocation(), ICE_ARROW_SOUND, 0.7f, 0.6f);
			timeAtLastIce = System.currentTimeMillis();
		}
	}

	@Override
	public void bow(double force) {
		Arrow arrow = PLAYER.getWorld().spawnArrow(PLAYER.getEyeLocation(), PLAYER.getLocation().getDirection(), (float) (force * 6), 0);
		arrow.setShooter(PLAYER);
		arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
		arrow.setCritical(true);
		arrow.setBounce(false);
		arrow.setCustomName(currentArrow.toString());
		for (Player player : Bukkit.getOnlinePlayers()) {
			player.playSound(PLAYER.getLocation(), SHOOT_SOUND, 0.7f, 0.6f);
		}
		arrowTimer.interrupt();
		setArrow(ArrowType.NORMAL);

	}
	
	@Override
	public void initialize() {
		super.initialize();
		this.currentArrow = ArrowType.NORMAL;
	}

	public void setArrow(ArrowType arrowType) {
		currentArrow = arrowType;
	}
}
