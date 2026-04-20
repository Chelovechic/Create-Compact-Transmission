package com.lucse.create_compact_transmission.content.fueling;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TFMGEngineProxy {

    private static Class<?> abstractEngineBlockEntityClass;
    private static Class<?> regularEngineBlockEntityClass;
    private static Field rpmField;
    private static Method canWorkMethod;
    private static Field fuelTankField;
    private static Object tfmgFluidsGasoline;
    private static Object tfmgFluidsDiesel;
    private static Object tfmgFluidsCreosote;
    private static TagKey<Fluid> gasolineTag;
    private static TagKey<Fluid> dieselTag;
    private static TagKey<Fluid> creosoteTag;

    static {
        try {
            abstractEngineBlockEntityClass = Class.forName("com.drmangotea.tfmg.content.engines.base.AbstractEngineBlockEntity");
            regularEngineBlockEntityClass = Class.forName("com.drmangotea.tfmg.content.engines.types.regular_engine.RegularEngineBlockEntity");
            rpmField = abstractEngineBlockEntityClass.getField("rpm");
            canWorkMethod = abstractEngineBlockEntityClass.getMethod("canWork");
            fuelTankField = abstractEngineBlockEntityClass.getField("fuelTank");
            Class<?> tfmgFluidsClass = Class.forName("com.drmangotea.tfmg.registry.TFMGFluids");
            Field gasolineField = tfmgFluidsClass.getField("GASOLINE");
            Field dieselField = tfmgFluidsClass.getField("DIESEL");
            Field creosoteField = tfmgFluidsClass.getField("CREOSOTE");

            tfmgFluidsGasoline = gasolineField.get(null);
            tfmgFluidsDiesel = dieselField.get(null);
            tfmgFluidsCreosote = creosoteField.get(null);
            try {
                Class<?> tfmgFluidTagsClass = Class.forName("com.drmangotea.tfmg.registry.TFMGTags$TFMGFluidTags");
                Field gasolineTagField = tfmgFluidTagsClass.getField("GASOLINE");
                Field dieselTagField = tfmgFluidTagsClass.getField("DIESEL");
                Field creosoteTagField = tfmgFluidTagsClass.getField("CREOSOTE");

                @SuppressWarnings("unchecked")
                TagKey<Fluid> gTag = (TagKey<Fluid>) gasolineTagField.get(null);
                @SuppressWarnings("unchecked")
                TagKey<Fluid> dTag = (TagKey<Fluid>) dieselTagField.get(null);
                @SuppressWarnings("unchecked")
                TagKey<Fluid> cTag = (TagKey<Fluid>) creosoteTagField.get(null);

                gasolineTag = gTag;
                dieselTag = dTag;
                creosoteTag = cTag;
            } catch (Exception e) {
                gasolineTag = FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", "gasoline"));
                dieselTag = FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", "diesel"));
                creosoteTag = FluidTags.create(ResourceLocation.fromNamespaceAndPath("forge", "creosote"));
            }
        } catch (Exception e) {
            abstractEngineBlockEntityClass = null;
            regularEngineBlockEntityClass = null;
            rpmField = null;
            canWorkMethod = null;
            fuelTankField = null;
            tfmgFluidsGasoline = null;
            tfmgFluidsDiesel = null;
            tfmgFluidsCreosote = null;
        }
    }

    public static class EngineInfo {
        public final boolean isRegularEngine;
        public final boolean isRunning;
        public final float rpm;

        public EngineInfo(boolean isRegularEngine, boolean isRunning, float rpm) {
            this.isRegularEngine = isRegularEngine;
            this.isRunning = isRunning;
            this.rpm = rpm;
        }

        public boolean isRunning() {
            return isRunning;
        }
    }

    public static boolean isAvailable() {
        return abstractEngineBlockEntityClass != null;
    }

    public static boolean isEngine(BlockEntity blockEntity) {
        return blockEntity != null
                && abstractEngineBlockEntityClass != null
                && abstractEngineBlockEntityClass.isAssignableFrom(blockEntity.getClass());
    }

    public static boolean isAllowedFuel(Fluid fluid) {
        if (fluid == null) {
            return false;
        }
        if (gasolineTag != null && fluid.is(gasolineTag)) {
            return true;
        }
        if (dieselTag != null && fluid.is(dieselTag)) {
            return true;
        }
        if (creosoteTag != null && fluid.is(creosoteTag)) {
            return true;
        }
        try {
            if (tfmgFluidsGasoline != null) {
                Method getMethod = tfmgFluidsGasoline.getClass().getMethod("get");
                Fluid gasoline = (Fluid) getMethod.invoke(tfmgFluidsGasoline);
                Fluid diesel = (Fluid) getMethod.invoke(tfmgFluidsDiesel);
                Fluid creosote = (Fluid) getMethod.invoke(tfmgFluidsCreosote);

                return fluid == gasoline || fluid == diesel || fluid == creosote;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static EngineInfo getEngineInfo(BlockEntity engineBE) {
        if (!isEngine(engineBE)) {
            return null;
        }

        try {
            boolean isRegularEngine = regularEngineBlockEntityClass != null
                    && regularEngineBlockEntityClass.isAssignableFrom(engineBE.getClass());
            float rpm = 0;
            if (rpmField != null) {
                rpm = rpmField.getFloat(engineBE);
            }
            boolean isRunning;
            if (canWorkMethod != null) {
                isRunning = (Boolean) canWorkMethod.invoke(engineBE);
            } else {
                isRunning = rpm > 0;
            }

            return new EngineInfo(isRegularEngine, isRunning, rpm);
        } catch (Exception e) {
            return null;
        }
    }

    public static EngineInfo getEngineInfo(BlockGetter level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        String blockName = ForgeRegistries.BLOCKS.getKey(block).toString();
        if (!blockName.startsWith("tfmg:regular_engine")
                && !blockName.startsWith("tfmg:turbine_engine")
                && !blockName.startsWith("tfmg:radial_engine")) {
            return null;
        }
        return getEngineInfo(level.getBlockEntity(pos));
    }

    public static Object getFuelTank(BlockEntity engineBE) {
        if (!isEngine(engineBE) || fuelTankField == null) {
            return null;
        }

        try {
            return fuelTankField.get(engineBE);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object getFuelTank(net.minecraft.world.level.Level level, BlockPos pos) {
        return getFuelTank(level.getBlockEntity(pos));
    }

    public static int getTankSpace(Object fuelTank) {
        if (fuelTank == null) {
            return 0;
        }

        try {
            Method getSpaceMethod = fuelTank.getClass().getMethod("getSpace");
            return (Integer) getSpaceMethod.invoke(fuelTank);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean canReceiveFuel(BlockEntity engineBE) {
        EngineInfo info = getEngineInfo(engineBE);
        if (info == null || !info.isRunning() || info.rpm <= 0) {
            return false;
        }

        return getTankSpace(getFuelTank(engineBE)) > 0;
    }

    public static int fillFuelTank(Object fuelTank, net.minecraftforge.fluids.FluidStack fluid) {
        if (fuelTank == null || fluid.isEmpty()) {
            return 0;
        }
        try {
            Class<?> ifluidHandlerClass = Class.forName("net.minecraftforge.fluids.capability.IFluidHandler");
            Method fillMethod = ifluidHandlerClass.getMethod("fill",
                    net.minecraftforge.fluids.FluidStack.class,
                    Class.forName("net.minecraftforge.fluids.capability.IFluidHandler$FluidAction"));
            @SuppressWarnings({"unchecked", "rawtypes"})
            Class actionClass = Class.forName("net.minecraftforge.fluids.capability.IFluidHandler$FluidAction");
            Enum<?> executeAction = Enum.valueOf(actionClass, "EXECUTE");

            return (Integer) fillMethod.invoke(fuelTank, fluid, executeAction);
        } catch (Exception e) {
        }
        return 0;
    }
}
