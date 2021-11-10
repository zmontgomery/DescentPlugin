package descent.champions;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Alchemist extends Champ {
	private enum BrewType {
		HEAL,
		DAMAGE,
		SLOW,
		SPEED
	}
	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Alchemist";
	public static final float MOVE_SPEED = 0.26f;
	public static final double NATURAL_REGEN = 3.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.DRAGON_BREATH), new ItemStack(Material.RED_DYE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.GOLDEN_BOOTS), null, null,
			null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_GRASS_BREAK;
	public static final float HURT_PITCH = 2.0f;
	
	public static final Sound THROW_SOUND = Sound.ENTITY_EGG_THROW;
	
	// Damage
	public static final int POTION_HEAL = 28;
	public static final int POTION_DAMAGE = 40;

	// Cool downs
	public static final float POTION_COOLDOWN = 1.5f;

	public static final float SPEED_TIME = 3.0f;
	public static final float SLOW_TIME = 3.0f;
	
	private BrewType currentBrew;
	private long timeAtLastPotion;

	public Alchemist(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND, HURT_PITCH);
		currentBrew = BrewType.HEAL;
		timeAtLastPotion = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getHeldItemSlot() == 1) {
			cycle();
		} else if (PLAYER.getInventory().getItemInMainHand().getType() == Material.DRAGON_BREATH
				&& (click == Action.RIGHT_CLICK_AIR || click == Action.RIGHT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastPotion > (1000 * POTION_COOLDOWN))) {
			ThrownPotion tp = (ThrownPotion) PLAYER.getWorld().spawnEntity(PLAYER.getEyeLocation(),
					EntityType.SPLASH_POTION);
			ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
			PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
			potionMeta.setColor(Color.RED);
			potionItem.setItemMeta(potionMeta);
			tp.setItem(potionItem);
			tp.setBounce(true);
			tp.setShooter(PLAYER);
			tp.setCustomName(currentBrew.toString());
			tp.setVelocity(PLAYER.getLocation().getDirection());
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), THROW_SOUND, 1f, 0.9f);
			}
			timeAtLastPotion = System.currentTimeMillis();
		} 
	}

	@Override
	public void abilityPotion(ThrownPotion potion, Collection<LivingEntity> hits) {
		for (Entity ent : hits) {
			if (ent instanceof Player) {
				Player player = (Player) ent;
				Champ champ = Champ.getChamp(player);
				if(potion.getCustomName().equals("HEAL") && Champ.BOARD.getEntryTeam(PLAYER.getName()).getName().equals(Champ.BOARD.getEntryTeam(player.getName()).getName()) ) {
					champ.heal(POTION_HEAL);
				} else if (potion.getCustomName().equals("DAMAGE") && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName().equals(Champ.BOARD.getEntryTeam(player.getName()).getName())) {
					if(champ.takeDamage(Alchemist.POTION_DAMAGE)) {
						onKill(champ);
					}
					onHit();
				} else if(potion.getCustomName().equals("SPEED") && Champ.BOARD.getEntryTeam(PLAYER.getName()).getName().equals(Champ.BOARD.getEntryTeam(player.getName()).getName()) ) {
					champ.takeEffect(new PotionEffect(PotionEffectType.SPEED, (int) (20 * SPEED_TIME), 1));
				} else if (potion.getCustomName().equals("SLOW") && !Champ.BOARD.getEntryTeam(PLAYER.getName()).getName().equals(Champ.BOARD.getEntryTeam(player.getName()).getName())) {
					onHit();
					champ.takeEffect(new PotionEffect(PotionEffectType.SLOW, (int) (20 * SLOW_TIME), 1));
				}
			}
		}
	}
	
	@Override
	public void initialize() {
		super.initialize();
		currentBrew = BrewType.HEAL;
	}
	
	private void cycle() {
		if(currentBrew == BrewType.HEAL) {
			currentBrew = BrewType.DAMAGE;
			PLAYER.getInventory().setItem(1, new ItemStack(Material.FERMENTED_SPIDER_EYE));
		} else if(currentBrew == BrewType.DAMAGE) {
			currentBrew = BrewType.SPEED;
			PLAYER.getInventory().setItem(1, new ItemStack(Material.SUGAR));
		}  else if(currentBrew == BrewType.SPEED) {
			currentBrew = BrewType.SLOW;
			PLAYER.getInventory().setItem(1, new ItemStack(Material.BLACK_DYE));
		}  else if(currentBrew == BrewType.SLOW) {
			currentBrew = BrewType.HEAL;
			PLAYER.getInventory().setItem(1, new ItemStack(Material.RED_DYE));
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + " CurrentPotion=" + currentBrew.toString();
	}
}