package com.lucse.create_compact_transmission.content.co2scrubber;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class TFMGShapesProxy {

    private static Object cableTubeShaper;
    private static Method getMethod;

    static {
        try {
            Class<?> tfmgShapesClass = Class.forName("com.drmangotea.tfmg.base.TFMGShapes");
            Field cableTubeField = tfmgShapesClass.getField("CABLE_TUBE");
            cableTubeShaper = cableTubeField.get(null);
            getMethod = cableTubeShaper.getClass().getMethod("get", Direction.class);

        } catch (Exception e) {
            cableTubeShaper = null;
            getMethod = null;
        }
    }

    public static VoxelShape getCableTubeShape(Direction facing) {
        if (cableTubeShaper == null || getMethod == null) {
            return Shapes.block();
        }
        try {
            return (VoxelShape) getMethod.invoke(cableTubeShaper, facing);
        } catch (Exception e) {
            return Shapes.block();
        }
    }
}

