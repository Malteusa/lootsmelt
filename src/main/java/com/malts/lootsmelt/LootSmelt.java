package com.malts.lootsmelt;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;

@Mod(
    modid = LootSmelt.MODID,
    name = LootSmelt.MODNAME,
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    acceptableRemoteVersions = "*")
public class LootSmelt {

    public static final String MODID = "lootsmelt";
    public static final String MODNAME = "Loot Smelt";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new LootSmeltHandler());
    }
}