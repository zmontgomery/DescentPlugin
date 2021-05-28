package descent.champions;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Impaler extends Champ {
	public static final double MAX_HEALTH = 200;
	public static final String CHAMP_NAME = "Impaler";
	public static final float MOVE_SPEED = 0.28f;
	public static final double NATURAL_REGEN = 4.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.WOODEN_SWORD),
			new ItemStack(Material.BLACK_DYE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { null, null, null,
			new ItemStack(Material.LEATHER_HELMET) };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.ITEM_AXE_STRIP;
	
	public static final Sound THROW_SOUND = Sound.ENTITY_BAT_TAKEOFF;
	public static final Sound WRAITH_SOUND = Sound.ENTITY_ENDERMAN_SCREAM;

	// Cool downs
	public static final float KNIFE_THROW_COOLDOWN = 0.16f;
	public static final float WRAITH_COOLDOWN = 15.0f;
	
	public static final float WRAITH_RUNOUT = 5.0f;
	
	private boolean wraith;

	private long timeAtLastWraith;
	private long timeAtLastThrow;
	// Damage
	public static final int KNIFE_DAMAGE = 16;

	public Impaler(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastWraith = 0;
		timeAtLastThrow = 0;
		wraith = false;
	}

	@Override
	public void use(Action click) {
		if(wraith) {
			return;
		}
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.WOODEN_SWORD
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastThrow > (1000 * KNIFE_THROW_COOLDOWN))) {
			Arrow knife1 = PLAYER.getWorld().spawnArrow(
					new Location(PLAYER.getWorld(), PLAYER.getLocation().getX(),
							PLAYER.getLocation().getY() + PLAYER.getEyeHeight(), PLAYER.getLocation().getZ()),
					PLAYER.getLocation().getDirection(), 2, 0);
			knife1.setShooter(PLAYER);
			knife1.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
			knife1.setCustomName(PLAYER.getName());
			knife1.setBounce(false);
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), THROW_SOUND, 0.3f, 0.8f);
			}
			

			timeAtLastThrow = System.currentTimeMillis();
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.BLACK_DYE
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastWraith > (1000 * WRAITH_COOLDOWN))) {
			ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
			ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
			ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
			ItemStack helm =  new ItemStack(Material.LEATHER_HELMET);
			LeatherArmorMeta bootCol = (LeatherArmorMeta) boot.getItemMeta();
			bootCol.setColor(Color.fromRGB(0, 0, 0));
			LeatherArmorMeta legCol = (LeatherArmorMeta) leg.getItemMeta();
			legCol.setColor(Color.fromRGB(0, 0, 0));
			LeatherArmorMeta chestCol = (LeatherArmorMeta) chest.getItemMeta();
			chestCol.setColor(Color.fromRGB(0, 0, 0));
			LeatherArmorMeta helmCol = (LeatherArmorMeta) helm.getItemMeta();
			helmCol.setColor(Color.BLACK);
			ItemStack[] wraithClothes = {boot, leg, chest, helm};
			PotionEffect inv = new PotionEffect(PotionEffectType.INVISIBILITY, (int) (WRAITH_RUNOUT * 20), 1);
			this.takeEffect(inv);
			PLAYER.getInventory().setContents(new ItemStack[] {null, null});
			PLAYER.getInventory().setArmorContents(wraithClothes);
			PLAYER.getInventory().setItemInOffHand(null);
			PLAYER.setWalkSpeed(Impaler.MOVE_SPEED + 0.1f);
			wraith = true;
			Thread runout = new Thread(() -> {
				try {
					Thread.sleep((long)(WRAITH_RUNOUT * 1000));
				} catch (InterruptedException e) {
					// squash
				}
				PLAYER.getInventory().setContents(Impaler.ITEMS);
				PLAYER.getInventory().setArmorContents(CLOTHES);
				PLAYER.getInventory().setItemInOffHand(Impaler.LEFT_HAND);
				PLAYER.setWalkSpeed(Impaler.MOVE_SPEED);
				wraith = false;
			});
			runout.start();
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), WRAITH_SOUND, 0.4f, 0.8f);
			}
			timeAtLastWraith = System.currentTimeMillis();
		}
	}

	@Override
	public void abilityRanged(Champ champ, Projectile projectile) {
		if (projectile instanceof Arrow && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName().equals(Champ.BOARD.getEntryTeam(champ.PLAYER.getName()).getName())) {
			if(champ.takeDamage(KNIFE_DAMAGE)){
				onKill(champ);
			}
			onHit();
		}
	}
	
	@Override
	public boolean takeDamage(double amount) {
		boolean killed = false;
		if(!wraith) {
			killed = super.takeDamage(amount);
		}
		return killed;
	}
	
}
