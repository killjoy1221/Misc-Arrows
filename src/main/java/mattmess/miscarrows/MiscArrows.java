package mattmess.miscarrows;

import net.minecraft.block.material.MapColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "miscarrows", name= "Misc Arrows", version = "1.0")
public class MiscArrows {
	public static CreativeTabs tab = new CreativeTabs("miscarrows"){

		@Override
		public Item getTabIconItem() {
			// TODO Auto-generated method stub
			return quiver;
		}
		
	};
	public static ItemMiscBow bow = new ItemMiscBow();
	public static Item arrow = new ItemMiscArrow();
	public static ItemStack fireArrow = new ItemStack(arrow, 1, 1);
	public static ItemStack iceArrow = new ItemStack(arrow, 1, 2);
	public static ItemStack slimeArrow = new ItemStack(arrow, 1, 3);
	public static ItemStack potionArrow = new ItemStack(arrow, 1, 4);
	public static ItemStack teleportArrow = new ItemStack(arrow, 1, 5);
	public static ItemStack explosiveArrow = new ItemStack(arrow, 1, 6);
	public static ItemStack itemArrow = new ItemStack(arrow, 1, 7);
	public static Item quiver = new ItemQuiver();
	
	@EventHandler
	public void startup(FMLPreInitializationEvent event){
		GameRegistry.registerItem(bow, "misc_bow");
		GameRegistry.registerItem(arrow, "misc_arrow");
		GameRegistry.registerItem(quiver, "quiver");
		
		GameRegistry.registerCustomItemStack("misc_arrow_fire", fireArrow);
		GameRegistry.registerCustomItemStack("misc_arrow_ice", iceArrow);
		GameRegistry.registerCustomItemStack("misc_arrow_slime", slimeArrow);
		GameRegistry.registerCustomItemStack("misc_arrow_potion", potionArrow);
		GameRegistry.registerCustomItemStack("misc_arrow_teleport", teleportArrow);
		GameRegistry.registerCustomItemStack("misc_arrow_explosive", explosiveArrow);
		GameRegistry.registerCustomItemStack("misc_arrow_item", itemArrow);
		
		GameRegistry.addRecipe(new ItemStack(bow, 1), new Object[]{"ggg", "gDg", "ggg", 'g', Items.gold_nugget, 'D', Items.bow});
		GameRegistry.addRecipe(new ItemStack(quiver, 1), new Object[]{"lll", "l l", "lll", 'l', Items.leather});
		
		GameRegistry.addShapelessRecipe(fireArrow, Items.arrow, Items.magma_cream);
		GameRegistry.addShapelessRecipe(iceArrow, Items.arrow, Items.snowball);
		GameRegistry.addShapelessRecipe(slimeArrow, Items.arrow, Items.slime_ball);
		GameRegistry.addShapelessRecipe(teleportArrow, Items.arrow, Items.ender_pearl);
		GameRegistry.addShapelessRecipe(potionArrow, Items.arrow, Items.potionitem);
		GameRegistry.addShapelessRecipe(explosiveArrow, Items.arrow, Items.gunpowder);
		GameRegistry.addShapelessRecipe(itemArrow, Items.arrow, Items.leather);
		
	}
}
