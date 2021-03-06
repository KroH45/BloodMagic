package WayofTime.alchemicalWizardry.common.rituals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import WayofTime.alchemicalWizardry.api.rituals.IMasterRitualStone;
import WayofTime.alchemicalWizardry.api.rituals.RitualComponent;
import WayofTime.alchemicalWizardry.api.rituals.RitualEffect;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import WayofTime.alchemicalWizardry.common.Int3;
import WayofTime.alchemicalWizardry.common.items.routing.InputRoutingFocus;
import WayofTime.alchemicalWizardry.common.items.routing.OutputRoutingFocus;
import WayofTime.alchemicalWizardry.common.spell.complex.effect.SpellHelper;

public class RitualEffectItemRouting extends RitualEffect
{
	Random rand = new Random();
	
    @Override
    public void performEffect(IMasterRitualStone ritualStone)
    {
        String owner = ritualStone.getOwner();

        int currentEssence = SoulNetworkHandler.getCurrentEssence(owner);
        World world = ritualStone.getWorld();
        int x = ritualStone.getXCoord();
        int y = ritualStone.getYCoord();
        int z = ritualStone.getZCoord();

        if (world.getWorldTime() % 20 != 0)
        {
            return;
        }
                
        int xBufOffset = 0;
        int yBufOffset = 1;
        int zBufOffset = 0;
        
        TileEntity bufferTile = world.getTileEntity(x + xBufOffset, y + yBufOffset, z + zBufOffset);
        
        if(!(bufferTile instanceof IInventory))
        {
        	return;
        }
                
        IInventory bufferInventory = (IInventory)bufferTile;
        
        for(int i=0; i<4; i++)
        {
        	Int3 inputFocusChest = this.getInputBufferChestLocation(i);
        	TileEntity inputFocusInv = world.getTileEntity(x + inputFocusChest.xCoord, y + inputFocusChest.yCoord, z + inputFocusChest.zCoord);
        	if(inputFocusInv instanceof IInventory)
        	{
        		for(int j=0; j<((IInventory) inputFocusInv).getSizeInventory(); j++)
        		{
        			ItemStack stack = ((IInventory) inputFocusInv).getStackInSlot(j);
        			if(stack != null && stack.getItem() instanceof InputRoutingFocus)
        			{
        				InputRoutingFocus inputFocus = (InputRoutingFocus)stack.getItem();
        				TileEntity inputChest = world.getTileEntity(inputFocus.xCoord(stack), inputFocus.yCoord(stack), inputFocus.zCoord(stack));
        				if(inputChest instanceof IInventory)
        				{
        					IInventory inputChestInventory = (IInventory)inputChest;
        					ForgeDirection syphonDirection = inputFocus.getSetDirection(stack);
        					boolean[] canSyphonList = new boolean[inputChestInventory.getSizeInventory()];
        					if(inputChest instanceof ISidedInventory)
        					{
        						int[] validSlots = ((ISidedInventory) inputChest).getAccessibleSlotsFromSide(syphonDirection.ordinal());
        						for(int in : validSlots)
        						{
        							System.out.println("" + in);
        							canSyphonList[in] = true;
        						}
        					}else
        					{
        						for(int n=0; n<inputChestInventory.getSizeInventory(); n++)
        						{
        							canSyphonList[n] = true;
        						}
        					}
        					
        					for(int n=0; n<inputChestInventory.getSizeInventory(); n++)
        					{
        						if(canSyphonList[n])
        						{
        							ItemStack syphonedStack = inputChestInventory.getStackInSlot(n);
        							if(syphonedStack == null)
        							{
        								continue;
        							}
        							
        							ItemStack newStack = SpellHelper.insertStackIntoInventory(syphonedStack, bufferInventory, ForgeDirection.DOWN);
        							if(newStack != null && newStack.stackSize <= 0)
        							{
        								newStack = null;
        							}
        							inputChestInventory.setInventorySlotContents(n, newStack);
        							break;
        						}
        					}
        				}
        			}
        		}
        	}
        }
        
        for(int i=0; i<4; i++)
        {
        	Int3 outputFocusChest = this.getOutputBufferChestLocation(i);
        	TileEntity outputFocusInv = world.getTileEntity(x + outputFocusChest.xCoord, y + outputFocusChest.yCoord, z + outputFocusChest.zCoord);
        	if(outputFocusInv instanceof IInventory)
        	{
        		IInventory outputFocusInventory = (IInventory)outputFocusInv;
//        		for(int j=0; j<((IInventory) outputFocusInv).getSizeInventory(); j++)
        		{
        			ItemStack stack = ((IInventory) outputFocusInv).getStackInSlot(0);
        			if(stack != null && stack.getItem() instanceof OutputRoutingFocus) //TODO change to output routing focus
        			{
        				boolean transferEverything = true;
        				for(int j=1; j<outputFocusInventory.getSizeInventory(); j++)
        				{
        					if(outputFocusInventory.getStackInSlot(j) != null)
        					{
        						transferEverything = false;
        						break;
        					}
        				}
        				
        				OutputRoutingFocus outputFocus = (OutputRoutingFocus)stack.getItem();
        				TileEntity outputChest = world.getTileEntity(outputFocus.xCoord(stack), outputFocus.yCoord(stack), outputFocus.zCoord(stack)); //Destination
    					ForgeDirection inputDirection = outputFocus.getSetDirection(stack);
        				
        				if(transferEverything)
        				{
            				if(outputChest instanceof IInventory)
            				{
            					IInventory outputChestInventory = (IInventory)outputChest;
            					
            					for(int n=0; n<bufferInventory.getSizeInventory(); n++)
            					{
        							ItemStack syphonedStack = bufferInventory.getStackInSlot(n);
        							if(syphonedStack == null)
        							{
        								continue;
        							}
        							
        							ItemStack newStack = SpellHelper.insertStackIntoInventory(syphonedStack, outputChestInventory, inputDirection);
        							if(newStack != null && newStack.stackSize <= 0)
        							{
        								newStack = null;
        							}
        							bufferInventory.setInventorySlotContents(n, newStack);
            						break;
            					}
            				}
        				}else
        				{
        					
            				
        					if(!(outputChest instanceof IInventory))
        					{
        						continue;
        					}
        					
        					for(int j=1; j<outputFocusInventory.getSizeInventory(); j++)
        					{
        						ItemStack keyStack = outputFocusInventory.getStackInSlot(j);
        						if(keyStack == null)
        						{
        							continue;	
        						}
        						
        						for(int n=0; n<bufferInventory.getSizeInventory(); n++)
    							{
    								ItemStack checkStack = bufferInventory.getStackInSlot(n);
    								if(checkStack == null)
    								{
    									continue;
    								}
    								
    								if(outputFocus.doesItemMatch(keyStack, checkStack))
    								{
    									ItemStack newStack = SpellHelper.insertStackIntoInventory(checkStack, (IInventory)outputChest, inputDirection);
            							if(newStack != null && newStack.stackSize <= 0)
            							{
            								newStack = null;
            							}
            							bufferInventory.setInventorySlotContents(n, newStack);
                						break;
    								}
    							}
        					}
        				}
        			}
        		}
        	}
        }
    }
    
    public Int3 getInputBufferChestLocation(int number)
    {
    	switch(number)
    	{
    	case 0:
    		return new Int3(1, 0, 0);
    	case 1:
    		return new Int3(-1, 0, 0);
    	case 2:
    		return new Int3(0, 0, 1);
    	case 3:
    		return new Int3(0, 0, -1);
    	}
    	return new Int3(0, 0, 0);
    }
    
    public Int3 getOutputBufferChestLocation(int number)
    {
    	switch(number)
    	{
    	case 0:
    		return new Int3(2, 0, 0);
    	case 1:
    		return new Int3(-2, 0, 0);
    	case 2:
    		return new Int3(0, 0, 2);
    	case 3:
    		return new Int3(0, 0, -2);
    	}
    	return new Int3(0, 0, 0);
    }

    @Override
    public int getCostPerRefresh()
    {
        return 0;
    }

    @Override
    public List<RitualComponent> getRitualComponentList()
    {
        ArrayList<RitualComponent> omegaRitual = new ArrayList();
        
        this.addCornerRunes(omegaRitual, 1, 0, RitualComponent.BLANK);
        this.addOffsetRunes(omegaRitual, 2, 1, 0, RitualComponent.FIRE);
        this.addParallelRunes(omegaRitual, 4, 0, RitualComponent.WATER);
        this.addParallelRunes(omegaRitual, 5, 0, RitualComponent.EARTH);
        this.addCornerRunes(omegaRitual, 4, 0, RitualComponent.WATER);

        return omegaRitual;
    }
}
