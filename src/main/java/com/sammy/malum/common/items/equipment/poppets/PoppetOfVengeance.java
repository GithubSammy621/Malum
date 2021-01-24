package com.sammy.malum.common.items.equipment.poppets;

import com.sammy.malum.core.init.MalumDamageSources;
import com.sammy.malum.core.systems.tileentities.SimpleInventory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class PoppetOfVengeance extends PoppetItem
{
    public PoppetOfVengeance(Properties properties)
    {
        super(properties);
    }
    
    @Override
    public void effect(ItemStack poppet, LivingHurtEvent event, World world, PlayerEntity playerEntity, LivingEntity target, SimpleInventory inventory, int slot)
    {
        float amount = event.getAmount() * 0.1f;
        event.setAmount(event.getAmount() - amount);
        target.attackEntityFrom(MalumDamageSources.VOODOO, amount * 2);
        target.hurtResistantTime = 0;
        super.effect(poppet, event, world, playerEntity, target, inventory, slot);
    }
}