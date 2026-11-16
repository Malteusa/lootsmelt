package com.malts.lootsmelt;

import java.util.ArrayList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class LootSmeltHandler {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDrops(LivingDropsEvent event) {
        final EntityLivingBase entity = event.entityLiving;
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) {
            return;
        }
        if (!entity.isBurning() && (event.source == null || !event.source.isFireDamage() || entity.isImmuneToFire())) {
            return;
        }
        if (entity instanceof EntityPlayer) {
            return;
        }

        final ArrayList<EntityItem> drops = event.drops;
        if (drops == null) {
            return;
        }
        final int initialSize = drops.size();
        if (initialSize == 0) {
            return;
        }

        final FurnaceRecipes furnace = FurnaceRecipes.smelting();
        for (int i = 0; i < initialSize; i++) {
            final EntityItem entityItem = drops.get(i);
            if (entityItem == null) {
                continue;
            }

            final ItemStack stack = entityItem.getEntityItem();
            if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
                continue;
            }

            final ItemStack result = furnace.getSmeltingResult(stack);
            if (result == null || result.getItem() == null || result.stackSize <= 0) {
                continue;
            }

            final int totalCount = stack.stackSize * result.stackSize;
            final ItemStack smelted = result.copy();
            final int maxStackSize = Math.max(1, smelted.getMaxStackSize());

            if (totalCount <= maxStackSize) {
                smelted.stackSize = totalCount;
                entityItem.setEntityItemStack(smelted);
            } else {
                smelted.stackSize = maxStackSize;
                entityItem.setEntityItemStack(smelted);

                int remaining = totalCount - maxStackSize;
                while (remaining > 0) {
                    final int splitSize = Math.min(remaining, maxStackSize);
                    final ItemStack extraStack = result.copy();
                    extraStack.stackSize = splitSize;

                    final EntityItem extraEntity = new EntityItem(
                        entityItem.worldObj,
                        entityItem.posX,
                        entityItem.posY,
                        entityItem.posZ,
                        extraStack);
                    extraEntity.delayBeforeCanPickup = entityItem.delayBeforeCanPickup;
                    extraEntity.lifespan = entityItem.lifespan;
                    extraEntity.motionX = entityItem.motionX;
                    extraEntity.motionY = entityItem.motionY;
                    extraEntity.motionZ = entityItem.motionZ;
                    drops.add(extraEntity);

                    remaining -= splitSize;
                }
            }
        }
    }
}