package mattmess.miscarrows.item;

import java.util.ArrayList;
import java.util.List;

import mattmess.miscarrows.EntityMiscArrow;
import mattmess.miscarrows.MiscArrows;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMiscBow extends ItemBow {

    public static final String[] bowPullIconNameArray = new String[] {"pulling_0", "pulling_1", "pulling_2"};
	@SideOnly(Side.CLIENT)
	private IIcon iconArray[];
	
	public ItemMiscBow(){
		super();
		this.setUnlocalizedName("misc_bow");
		this.setTextureName("miscarrows:misc_bow");
		this.setCreativeTab(MiscArrows.tab);
		this.setMaxDamage(512);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister icon){
		this.itemIcon = icon.registerIcon(this.getIconString() + "_standby");
        this.iconArray = new IIcon[bowPullIconNameArray.length];

        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = icon.registerIcon(this.getIconString() + "_" + bowPullIconNameArray[i]);
        }
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getItemIconForUseDuration(int i){
		return iconArray[i];
	}
	
	@Override
	public void onCreated(ItemStack itemstack, World world, EntityPlayer player){
		itemstack.stackTagCompound = new NBTTagCompound();
		itemstack.stackTagCompound.setInteger("arrow", 0);
	}
	
	private void createNBTCompound(ItemStack itemstack){
		if(itemstack.stackTagCompound == null)
			itemstack.stackTagCompound = new NBTTagCompound();
	}
	
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool){
		createNBTCompound(itemstack);
		int id = itemstack.stackTagCompound.getInteger("arrow");
		list.add("Selected Arrow: " + EntityMiscArrow.Type.valueOf(id).name());
	}
	
	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int i){
		
		int j = this.getMaxItemUseDuration(stack) - i;

		if(player.isSneaking() && j <= 4){
			openSelectArrowGui(stack, player);
			return;
		}
		
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, j);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return;
        }
        j = event.charge;

        boolean flag = player.capabilities.isCreativeMode || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        if (flag || (this.listHasArrow(player.inventory.mainInventory, getSelectedArrow(stack))))
        {
            float f = (float)j / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D)
            {
                return;
            }

            if (f > 1.0F)
            {
                f = 1.0F;
            }

            if (!world.isRemote)
            {
            EntityMiscArrow entityarrow = new EntityMiscArrow(world, player, f * 2.0F, this.getSelectedArrow(stack));

            if (f == 1.0F)
            {
                entityarrow.setIsCritical(true);
            }

            int k = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);

            if (k > 0)
            {
                entityarrow.setDamage(entityarrow.getDamage() + (double)k * 0.5D + 0.5D);
            }

            int l = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);

            if (l > 0)
            {
                entityarrow.setKnockbackStrength(l);
            }

            if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) > 0)
            {
                entityarrow.setFire(100);
            }

            stack.damageItem(1, player);
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
            if(flag){
            	entityarrow.canBePickedUp = 2;
            }else{
            	this.eatItemWithDamage(player.inventory, getSelectedArrow(stack));
            }

                world.spawnEntityInWorld(entityarrow);
            }
        }
	}

	private void openSelectArrowGui(ItemStack itemstack, EntityPlayer player) {
		ArrayList<ItemStack> arrows = getArrowStacks(player.inventory);
		if(arrows.size() == 0)
			return;
		//if(player.worldObj.isRemote)
			player.openGui(MiscArrows.instance, 0, player.worldObj, 0, 0, 0);
		
	}

	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
		if(!listHasArrow(player.inventory.mainInventory, getSelectedArrow(itemstack))){
			selectFirstArrow(player);
		}
        ArrowNockEvent event = new ArrowNockEvent(player, itemstack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
        {
            return event.result;
        }

       // if(itemstack.stackTagCompound.getInteger("arrow") == 0)
       // 	return itemstack;
        if (this.listHasArrow(player.inventory.mainInventory, getSelectedArrow(itemstack)))
        {
            player.setItemInUse(itemstack, this.getMaxItemUseDuration(itemstack));
        }

        return itemstack;
    }
	
	private ItemStack getSelectedItemStack(InventoryPlayer inventory, ItemStack bow, int id){
		ItemStack arrow;
		for(ItemStack stack : inventory.mainInventory){
			if(stack == null) continue;
			if(isItemArrow(stack) && stack.getItemDamage() == id){
				return stack;
			}
		}
		return null;
	}
	
	private void selectFirstArrow(EntityPlayer player) {
		for(ItemStack stack : player.inventory.mainInventory){
			if(stack == null)
				continue;
			if(isItemArrow(stack)){
				selectArrow(player,stack);
				return;
			}
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack){
		return EnumAction.bow;
	}
	
	public void selectArrow(EntityPlayer player, ItemStack arrow){
		ItemStack bow = player.getHeldItem();
		if(bow.getItem() != MiscArrows.bow)
			return;
		NBTTagCompound tag = bow.stackTagCompound;
		if(arrow == null){
			tag.removeTag("arrow");
		}else{
			tag.setInteger("arrow", arrow.getItemDamage());
		}
	}
	
	public int getSelectedArrow(ItemStack bow){
		if(bow == null)
			return 0;
		return bow.stackTagCompound.getInteger("arrow");
	}
	
	public static ArrayList<ItemStack> getArrowStacks(InventoryPlayer inventory){
		ArrayList<ItemStack> types = Lists.newArrayList();
		for(ItemStack item : inventory.mainInventory){
			if(item == null) continue;
			if(isItemArrow(item) && !listHasArrow(types.toArray(new ItemStack[0]), item.getItemDamage())){
				ItemStack copy = item.copy();
				copy.stackSize = getArrowCount(inventory, item.getItemDamage());
				types.add(copy);
			}
			
			/*if(item.getItem().equals(MiscArrows.quiver)){
				ItemQuiver quiver = (ItemQuiver) item.getItem();
				InventoryQuiver quivInv = quiver.getInventory(item);
				for(ItemStack itemstack : quivInv.inventory){
					if(itemstack != null)
						types.add(itemstack);
				}
			}*/
		}
		return types;
	}
	
	private static boolean listHasArrow(ItemStack[] mainInventory, int item){
		for(ItemStack item1 : mainInventory){
			if(item1 == null) continue;
			if(isItemArrow(item1) && item1.getItemDamage() == item){
				return true;
			}
		}
		return false;
	}

	private static int getArrowCount(InventoryPlayer inventory, int damage){
		int count = 0;
		for(ItemStack itemstack : inventory.mainInventory){
			if(itemstack == null)
				continue;
			if(isItemArrow(itemstack) && itemstack.getItemDamage() == damage){
				count += itemstack.stackSize;
			}
		}
		return count;
	}
	
	private void eatItemWithDamage(InventoryPlayer inventory, int damage){
		for(int i = 0;i<inventory.mainInventory.length; i++){
			ItemStack itemstack = inventory.mainInventory[i];
			if(itemstack == null)
				continue;
			if(isItemArrow(itemstack) && itemstack.getItemDamage() == damage){
				itemstack.stackSize--;
				if(itemstack.stackSize <=0)
					inventory.setInventorySlotContents(i, null);
				return;
			}
		}
	}
	
	private static boolean isItemArrow(ItemStack itemstack){
		return itemstack.getItem() == Items.arrow || itemstack.getItem() == MiscArrows.arrow;
	}
}
