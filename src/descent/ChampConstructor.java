package descent;

import org.bukkit.inventory.ItemStack;

public class ChampConstructor {

	String name;
	int maxHealth;
	float moveSpeed;
	int baseDamage;
	
	ItemStack[] items;
	ItemStack[] armor;
	
	public ChampConstructor(String nm, int mh, float ms, int bd, ItemStack[] it, ItemStack[] ar) {
		
		name = nm;
		maxHealth = mh;
		moveSpeed = ms;
		items = it;
		armor = ar;
		baseDamage = bd;
		
	}
	
}
