package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import com.lucse.create_compact_transmission.CCTGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public class FourSpeedTransmissionScreen extends AbstractSimiScreen {

    private static final int ROW_HEIGHT = 18;
    private static final int START_Y = 24;
    private static final int RATIO_INPUT_X = 30;
    private static final int RATIO_INPUT_WIDTH = 56;
    private static final int ADD_BUTTON_X = 29;
    private static final int REMOVE_BUTTON_X = 49;
    private static final int BOTTOM_BUTTON_Y_OFFSET = 24;
    private static final int CONFIRM_BUTTON_X_OFFSET = 107;

    private final CCTGuiTextures background = CCTGuiTextures.FOUR_SPEED_TRANSMISSION;
    private final FourSpeedTransmissionBlockEntity be;
    private final int[] editedRatios;
    private int editedGearCount;

    private final ScrollInput[] gearInputs = new ScrollInput[FourSpeedTransmissionBlockEntity.MAX_GEARS];

    private IconButton confirmButton;
    private IconButton addGearButton;
    private IconButton removeGearButton;

    public FourSpeedTransmissionScreen(FourSpeedTransmissionBlockEntity be) {
        super(Component.translatable("gui.four_speed_transmission.title"));
        this.be = be;
        this.editedRatios = be.getGearRatiosCopy();
        this.editedGearCount = be.getGearCount();
    }

    public static void open(FourSpeedTransmissionBlockEntity be) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new FourSpeedTransmissionScreen(be));
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        setWindowOffset(0, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        for (int i = 0; i < FourSpeedTransmissionBlockEntity.MAX_GEARS; i++) {
            int index = i;
            int rowY = y + START_Y + ROW_HEIGHT * i;

            ScrollInput input = new CompactScrollInput(x + RATIO_INPUT_X, rowY + 2, RATIO_INPUT_WIDTH, 14)
                    .calling(state -> {
                        editedRatios[index] = FourSpeedTransmissionBlockEntity.sanitizeRatio(state);
                    })
                    .withRange(FourSpeedTransmissionBlockEntity.MIN_RATIO, FourSpeedTransmissionBlockEntity.MAX_RATIO + 1)
                    .withShiftStep(10)
                    .setState(editedRatios[i]);
            gearInputs[i] = input;
            addRenderableWidget(input);
        }

        addGearButton = new IconButton(x + ADD_BUTTON_X, y + background.getHeight() - BOTTOM_BUTTON_Y_OFFSET, AllIcons.I_ADD);
        addGearButton.green = true;
        addGearButton.withCallback(this::addGear);
        addRenderableWidget(addGearButton);

        removeGearButton = new IconButton(x + REMOVE_BUTTON_X, y + background.getHeight() - BOTTOM_BUTTON_Y_OFFSET, AllIcons.I_TRASH);
        removeGearButton.withCallback(this::removeGear);
        addRenderableWidget(removeGearButton);

        confirmButton = new IconButton(x + background.getWidth() - CONFIRM_BUTTON_X_OFFSET,
                y + background.getHeight() - BOTTOM_BUTTON_Y_OFFSET, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);

        updateVisibleGears();
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);
        for (int i = 0; i < editedGearCount; i++) {
            int rowY = y + START_Y + ROW_HEIGHT * i;
            String ratioText = formatRatio(editedRatios[i]);
            int ratioX = x + RATIO_INPUT_X + (RATIO_INPUT_WIDTH - font.width(ratioText)) / 2;
            graphics.drawString(font, ratioText, ratioX, rowY + 5, getRatioColor(editedRatios[i]), false);
        }
    }

    @Override
    public void removed() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("GearCount", editedGearCount);
        nbt.putIntArray("GearRatios", editedRatios);
        
        CatnipServices.NETWORK.sendToServer(new FourSpeedTransmissionConfigurationPacket(be.getBlockPos(), nbt));
        super.removed();
    }

    private void addGear() {
        if (editedGearCount >= FourSpeedTransmissionBlockEntity.MAX_GEARS) {
            return;
        }
        editedGearCount++;
        updateVisibleGears();
    }

    private void removeGear() {
        if (editedGearCount <= 1) {
            return;
        }
        editedGearCount--;
        updateVisibleGears();
    }

    private void updateVisibleGears() {
        for (int i = 0; i < FourSpeedTransmissionBlockEntity.MAX_GEARS; i++) {
            boolean visible = i < editedGearCount;
            gearInputs[i].visible = visible;
            gearInputs[i].active = visible;
        }

        addGearButton.active = editedGearCount < FourSpeedTransmissionBlockEntity.MAX_GEARS;
        removeGearButton.active = editedGearCount > 1;
    }

    private static String formatRatio(int value) {
        return String.format(Locale.ROOT, "%.2fx", value / 100.0f);
    }

    private static int getRatioColor(int value) {
        if (value == 0) {
            return 0x9A9A9A;
        }
        return value < 0 ? 0xD97373 : 0xFFFFFF;
    }

    private static class CompactScrollInput extends ScrollInput {

        public CompactScrollInput(int x, int y, int width, int height) {
            super(x, y, width, height);
            title = null;
            updateTooltip();
        }

        @Override
        protected void updateTooltip() {
            toolTip.clear();
        }
    }
}
