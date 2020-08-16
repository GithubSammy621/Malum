
package com.kittykitcatcat.malum.items.curios;

import com.kittykitcatcat.malum.SpiritConsumer;
import com.kittykitcatcat.malum.SpiritDataHelper;
import com.kittykitcatcat.malum.SpiritDescription;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.capability.ICurio;

import java.util.ArrayList;

import static com.kittykitcatcat.malum.SpiritDataHelper.makeGenericSpiritDependantTooltip;

public class CurioVampireNecklace extends Item implements ICurio, SpiritConsumer, SpiritDescription
{
    public CurioVampireNecklace(Properties builder)
    {
        super(builder);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT unused)
    {
        return CurioProvider.createProvider(new ICurio()
        {
            @Override
            public void playEquipSound(LivingEntity entityLivingBase)
            {
                entityLivingBase.world.playSound(null, entityLivingBase.getPosition(),
                        SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, SoundCategory.NEUTRAL,
                        1.0f, 1.0f);
            }

            @Override
            public boolean canRightClickEquip()
            {
                return true;
            }
        });
    }

    @Override
    public int durability()
    {
        return 8;
    }

    @Override
    public String spirit()
    {
        return "minecraft:husk";
    }

    @Override
    public ArrayList<ITextComponent> components()
    {
        ArrayList<ITextComponent> components = new ArrayList<>();
        components.add(makeGenericSpiritDependantTooltip("malum.tooltip.vampirenecklace.desc", SpiritDataHelper.getName(spirit())));
        return components;
    }
}