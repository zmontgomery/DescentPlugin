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

public class Alchemist extends Champ {
	public static final double MAX_HEALTH = 175;
	public static final String CHAMP_NAME = "Alchemist";
	public static final float MOVE_SPEED = 0.26f;
	public static final double NATURAL_REGEN = 2.0;
	public static final ItemStack[] ITEMS = new ItemStack[] { new ItemStack(Material.GLASS_BOTTLE) };
	public static final ItemStack[] CLOTHES = new ItemStack[] { new ItemStack(Material.GOLDEN_BOOTS), null, null,
			null };
	public static final ItemStack LEFT_HAND = null;
	public static final Sound HURT_SOUND = Sound.BLOCK_GRASS_BREAK;

	public static final Sound THROW_SOUND = Sound.ENTITY_EGG_THROW;
	
	
	// Damage
	public static final int POTION_HEAL = 30;
	public static final int POTION_DAMAGE = 45;

	// Cool downs
	public static final float POTION_COOLDOWN = 1.7f;

	private long timeAtLastPotion;

	public Alchemist(Player player) {
		super(player, CHAMP_NAME, MOVE_SPEED, NATURAL_REGEN, MAX_HEALTH, ITEMS, CLOTHES, LEFT_HAND, HURT_SOUND);
		timeAtLastPotion = 0;
	}

	@Override
	public void use(Action click) {
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE
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
			tp.setCustomName("HEAL");

			tp.setVelocity(PLAYER.getLocation().getDirection());
			for(Player player : Bukkit.getOnlinePlayers()) {
				player.playSound(PLAYER.getLocation(), THROW_SOUND, 1f, 0.9f);
			}

			timeAtLastPotion = System.currentTimeMillis();
		}
		if (PLAYER.getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE
				&& (click == Action.LEFT_CLICK_AIR || click == Action.LEFT_CLICK_BLOCK)
				&& (System.currentTimeMillis() - timeAtLastPotion > (1000 * POTION_COOLDOWN))) {

			ThrownPotion tp = (ThrownPotion) PLAYER.getWorld().spawnEntity(PLAYER.getEyeLocation(),
					EntityType.SPLASH_POTION);

			ItemStack potionItem = new ItemStack(Material.SPLASH_POTION);
			PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();

			potionMeta.setColor(Color.BLACK);

			potionItem.setItemMeta(potionMeta);

			tp.setItem(potionItem);
			tp.setBounce(true);
			tp.setShooter(PLAYER);
			tp.setCustomName("DAMAGE");

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
				}
				
				
			}
		}
	}
}