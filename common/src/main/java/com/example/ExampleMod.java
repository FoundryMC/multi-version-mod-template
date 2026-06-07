package com.example;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExampleMod {

    public static final String MOD_ID = "examplemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private ExampleMod() {
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
