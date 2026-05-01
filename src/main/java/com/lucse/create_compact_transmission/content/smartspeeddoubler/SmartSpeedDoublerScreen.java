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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SmartSpeedDoublerScreen extends AbstractSimiScreen {

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
        setWindowOffset(-20, 0);
        super.init();

        int x = guiLeft;
        int y = guiTop;
        int rowHeight = 22;
        int startY = y + 20;

        multiplierInput = new ScrollInput(x + 58, startY, 58, 18)
                .calling(state -> be.multiplierRatio = state)
                .withRange(100, 400)
                .setState(be.multiplierRatio)
                .titled(Component.literal("main: "));
        addRenderableWidget(multiplierInput);

        multiplierTopInput = new ScrollInput(x + 58, startY + rowHeight, 58, 18)
                .calling(state -> be.multiplierRatioTop = state)
                .withRange(100, 400)
                .setState(be.multiplierRatioTop)
                .titled(Component.literal("top: "));
        addRenderableWidget(multiplierTopInput);

        multiplierBottomInput = new ScrollInput(x + 58, startY + rowHeight * 2, 58, 18)
                .calling(state -> be.multiplierRatioBottom = state)
                .withRange(100, 400)
                .setState(be.multiplierRatioBottom)
                .titled(Component.literal("botton: "));
        addRenderableWidget(multiplierBottomInput);

        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
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

        int startY = y + 24;
        int rowHeight = 22;
        graphics.drawString(font, "main:", x + 30, startY, 0x5B5B5B, false);
        graphics.drawString(font, "top:", x + 30, startY + rowHeight, 0x5B5B5B, false);
        graphics.drawString(font, "botton:", x + 30, startY + rowHeight * 2, 0x5B5B5B, false);

        graphics.drawString(font, String.format("%.2f", multiplierInput.getState() / 100.0f), x + 120, startY, 0xFFFFFF, false);
        graphics.drawString(font, String.format("%.2f", multiplierTopInput.getState() / 100.0f), x + 120, startY + rowHeight, 0xFFFFFF, false);
        graphics.drawString(font, String.format("%.2f", multiplierBottomInput.getState() / 100.0f), x + 120, startY + rowHeight * 2, 0xFFFFFF, false);
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
}
