package its_meow.coloredchests;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CreativeTab extends CreativeTabs {

	public CreativeTab(String tab) {
		super(tab);
	}
	
	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(Blocks.CHEST);
	}

	
}
