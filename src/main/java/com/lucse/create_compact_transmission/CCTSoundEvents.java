package com.lucse.create_compact_transmission;

import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CCTSoundEvents {

    private static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, CreateCompactTransmission.MODID);

    private static final DeferredHolder<SoundEvent, SoundEvent> SHIFTER_SOUND_EVENT =
            SOUNDS.register("shifter_sound", () ->
                    SoundEvent.createVariableRangeEvent(CreateCompactTransmission.asResource("shifter_sound")));

    public static final SoundEntry SHIFTER_SOUND = new SoundEntry(
            () -> SHIFTER_SOUND_EVENT.get(),
            SoundSource.BLOCKS
    );

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }

    public static void prepare() {
    }

    public static class SoundEntry {
        private final java.util.function.Supplier<SoundEvent> event;
        private final SoundSource category;

        public SoundEntry(java.util.function.Supplier<SoundEvent> event, SoundSource category) {
            this.event = event;
            this.category = category;
        }

        public ResourceLocation getId() {
            return CreateCompactTransmission.asResource("shifter_sound");
        }

        public SoundEvent getMainEvent() {
            return event.get();
        }

        public void playOnServer(Level world, Vec3i pos) {
            playOnServer(world, pos, 1, 1);
        }

        public void playOnServer(Level world, Vec3i pos, float volume, float pitch) {
            play(world, null, pos, volume, pitch);
        }

        public void play(Level world, Player entity, Vec3i pos) {
            play(world, entity, pos, 1, 1);
        }

        public void play(Level world, Player entity, Vec3i pos, float volume, float pitch) {
            play(world, entity, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, volume, pitch);
        }

        public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
            world.playSound(entity, x, y, z, getMainEvent(), category, volume, pitch);
        }
    }
}
