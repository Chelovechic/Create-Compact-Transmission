package com.lucse.create_compact_transmission.content.smartspeeddoubler;

import com.lucse.create_compact_transmission.CCTBlocks;
import com.lucse.create_compact_transmission.CCTGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public class SmartSpeedDoublerScreen extends AbstractSimiScreen {

    private static final int ROW_HEIGHT = 18;
    private static final int START_Y = 24;
    private static final int LABEL_TO_INPUT_GAP = 4;
    private static final int INPUT_WIDTH = 56;
    private static final int INPUT_HEIGHT = 14;
    private static final int CONFIRM_GAP_BELOW_ROWS = 6;

    private static final String LABEL_MAIN = "main:";
    private static final String LABEL_TOP = "top:";
    private static final String LABEL_BOTTOM = "botton:";

    private int labelColumnLeft = 8;
    private int inputX = 52;

    private final ItemStack renderedItem = CCTBlocks.SMART_SPEED_DOUBLER.asStack();
    private final CCTGuiTextures background = CCTGuiTextures.SMART_SPEED_DOUBLER;
    private IconButton confirmButton;
    private SmartSpeedDoublerBlockEntity be;

    private ScrollInput multiplierInput;
    private ScrollInput multiplierTopInput;
    private ScrollInput multiplierBottomInput;

    public SmartSpeedDoublerScreen(SmartSpeedDoublerBlockEntity be) {
        super(CreateLang.translateDirect("gui.smart_speed_doubler.title"));
        this.be = be;
    }

    public static void open(SmartSpeedDoublerBlockEntity be) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new SmartSpeedDoublerScreen(be));
    }

    @Override
    protected void init() {
        setWindowSize(background.getWidth(), background.getHeight());
        setWindowOffset(0, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;

        int maxLabelW = Math.max(Math.max(font.width(LABEL_MAIN), font.width(LABEL_TOP)), font.width(LABEL_BOTTOM));
        int contentWidth = maxLabelW + LABEL_TO_INPUT_GAP + INPUT_WIDTH;
        int contentLeft = x + (background.getWidth() - contentWidth) / 2;
        labelColumnLeft = contentLeft;
        inputX = contentLeft + maxLabelW + LABEL_TO_INPUT_GAP;

        multiplierInput = new CompactScrollInput(inputX, y + START_Y + 2, INPUT_WIDTH, INPUT_HEIGHT)
                .calling(state -> be.multiplierRatio = SmartSpeedDoublerBlockEntity.sanitizeMultiplierRatio(state))
                .withRange(SmartSpeedDoublerBlockEntity.minMultiplierRatio(), SmartSpeedDoublerBlockEntity.maxMultiplierRatio() + 1)
                .withShiftStep(10)
                .setState(be.multiplierRatio);
        addRenderableWidget(multiplierInput);

        multiplierTopInput = new CompactScrollInput(inputX, y + START_Y + ROW_HEIGHT + 2, INPUT_WIDTH, INPUT_HEIGHT)
                .calling(state -> be.multiplierRatioTop = SmartSpeedDoublerBlockEntity.sanitizeMultiplierRatio(state))
                .withRange(SmartSpeedDoublerBlockEntity.minMultiplierRatio(), SmartSpeedDoublerBlockEntity.maxMultiplierRatio() + 1)
                .withShiftStep(10)
                .setState(be.multiplierRatioTop);
        addRenderableWidget(multiplierTopInput);

        multiplierBottomInput = new CompactScrollInput(inputX, y + START_Y + ROW_HEIGHT * 2 + 2, INPUT_WIDTH, INPUT_HEIGHT)
                .calling(state -> be.multiplierRatioBottom = SmartSpeedDoublerBlockEntity.sanitizeMultiplierRatio(state))
                .withRange(SmartSpeedDoublerBlockEntity.minMultiplierRatio(), SmartSpeedDoublerBlockEntity.maxMultiplierRatio() + 1)
                .withShiftStep(10)
                .setState(be.multiplierRatioBottom);
        addRenderableWidget(multiplierBottomInput);

        int rowsBottom = y + START_Y + ROW_HEIGHT * 3;
        int confirmX = inputX + INPUT_WIDTH / 2 - 9;
        int confirmY = rowsBottom + CONFIRM_GAP_BELOW_ROWS;
        confirmButton = new IconButton(confirmX, confirmY, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;

        background.render(graphics, x, y);

        GuiGameElement.of(renderedItem)
                .at(x + background.getWidth() + 6, y + background.getHeight() - 56, -200)
                .render(graphics);

        drawLabeledRow(graphics, y, 0, LABEL_MAIN, be.multiplierRatio);
        drawLabeledRow(graphics, y, 1, LABEL_TOP, be.multiplierRatioTop);
        drawLabeledRow(graphics, y, 2, LABEL_BOTTOM, be.multiplierRatioBottom);
    }

    private void drawLabeledRow(GuiGraphics graphics, int y, int rowIndex, String label, int ratioHundredths) {
        int rowY = y + START_Y + ROW_HEIGHT * rowIndex;
        graphics.drawString(font, label, labelColumnLeft, rowY + 5, 0x5B5B5B, false);

        String ratioText = formatRatio(ratioHundredths);
        int ratioX = inputX + (INPUT_WIDTH - font.width(ratioText)) / 2;
        graphics.drawString(font, ratioText, ratioX, rowY + 5, getRatioColor(ratioHundredths), false);
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

    @Override
    public void removed() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("multiplier", be.multiplierRatio);
        nbt.putInt("multiplierTop", be.multiplierRatioTop);
        nbt.putInt("multiplierBottom", be.multiplierRatioBottom);

        CatnipServices.NETWORK.sendToServer(new SmartSpeedDoublerConfigurationPacket(be.getBlockPos(), nbt));
        super.removed();
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
