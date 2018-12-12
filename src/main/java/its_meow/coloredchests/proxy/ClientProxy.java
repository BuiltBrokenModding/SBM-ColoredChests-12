package its_meow.coloredchests.proxy;

import its_meow.coloredchests.chest.RenderChest;
import its_meow.coloredchests.chest.TileEntityColoredChest;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	public void init(FMLInitializationEvent event) {
		super.init(event);
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityColoredChest.class, new RenderChest());
	}
    
}
