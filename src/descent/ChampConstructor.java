package descent;

import org.bukkit.inventory.ItemStack;

public class ChampConstructor {

	String name;
	int maxHealth;
	float moveSpeed;
	int baseDamage;
	
	ItemStack[] items;
	ItemStack[] armor;
	ItemStack offHand;
	
	public ChampConstructor(String name, int maxHealth, float moveSpeed, int baseDamage, ItemStack[] items, ItemStack[] armor, ItemStack offHand) {
		
		this.name = name;
		this.maxHealth = maxHealth;
		this.moveSpeed = moveSpeed;
		this.items = items;
		this.armor = armor;
		this.baseDamage = baseDamage;
		this.offHand = offHand;
		
	}
	
}
