package com.lucse.create_compact_transmission.content.fourspeedtransmission;

import com.lucse.create_compact_transmission.CCTBlocks;
import com.lucse.create_compact_transmission.CCTGuiTextures;
import com.lucse.create_compact_transmission.CCTPackets;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class FourSpeedTransmissionScreen extends AbstractSimiScreen {

    private final ItemStack renderedItem = CCTBlocks.FOUR_SPEED_TRANSMISSION.asStack();
    private final CCTGuiTextures background = CCTGuiTextures.FOUR_SPEED_TRANSMISSION;
    private IconButton confirmButton;
    private FourSpeedTransmissionBlockEntity be;

    private ScrollInput gearRInput;
    private ScrollInput gear1Input;
    private ScrollInput gear2Input;
    private ScrollInput gear3Input;
    private ScrollInput gear4Input;

    public FourSpeedTransmissionScreen(FourSpeedTransmissionBlockEntity be) {
        super(CreateLang.translateDirect("gui.four_speed_transmission.title"));
        this.be = be;
    }

    public static void open(FourSpeedTransmissionBlockEntity be) {
        net.minecraft.client.Minecraft.getInstance().setScreen(new FourSpeedTransmissionScreen(be));
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
        gearRInput = new ScrollInput(x + 58, startY, 58, 18)
                .calling(state -> be.gearReverseRatio = state)
                .withRange(-100, -10)
                .setState(be.gearReverseRatio)
                .titled(Component.literal("R: "));
        addRenderableWidget(gearRInput);
        gear1Input = new ScrollInput(x + 58, startY + rowHeight, 58, 18)
                .calling(state -> be.gear1Ratio = state)
                .withRange(10, 300)
                .setState(be.gear1Ratio)
                .titled(Component.literal("1: "));
        addRenderableWidget(gear1Input);
        gear2Input = new ScrollInput(x + 58, startY + rowHeight * 2, 58, 18)
                .calling(state -> be.gear2Ratio = state)
                .withRange(10, 300)
                .setState(be.gear2Ratio)
                .titled(Component.literal("2: "));
        addRenderableWidget(gear2Input);
        gear3Input = new ScrollInput(x + 58, startY + rowHeight * 3, 58, 18)
                .calling(state -> be.gear3Ratio = state)
                .withRange(10, 300)
                .setState(be.gear3Ratio)
                .titled(Component.literal("3: "));
        addRenderableWidget(gear3Input);
        gear4Input = new ScrollInput(x + 58, startY + rowHeight * 4, 58, 18)
                .calling(state -> be.gear4Ratio = state)
                .withRange(10, 300)
                .setState(be.gear4Ratio)
                .titled(Component.literal("4: "));
        addRenderableWidget(gear4Input);

        confirmButton = new IconButton(x + background.getWidth() - 33, y + background.getHeight() - 24, AllIcons.I_CONFIRM);
        confirmButton.withCallback(this::onClose);
        addRenderableWidget(confirmButton);
    }

    @Override
    protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        int x = guiLeft;
        int y = guiTop;
        int rowHeight = 22;

        background.render(graphics, x, y);
        GuiGameElement.of(renderedItem)
                .at(x + background.getWidth() + 6, y + background.getHeight() - 56, -200)
                .render(graphics);
        int startY = y + 24;
        graphics.drawString(font, "R:", x + 30, startY, 0x5B5B5B, false);
        graphics.drawString(font, "1:", x + 30, startY + rowHeight, 0x5B5B5B, false);
        graphics.drawString(font, "2:", x + 30, startY + rowHeight * 2, 0x5B5B5B, false);
        graphics.drawString(font, "3:", x + 30, startY + rowHeight * 3, 0x5B5B5B, false);
        graphics.drawString(font, "4:", x + 30, startY + rowHeight * 4, 0x5B5B5B, false);
        graphics.drawString(font, String.format("%.2f", gearRInput.getState() / 100.0f), x + 120, startY, 0xFFFFFF, false);
        graphics.drawString(font, String.format("%.2f", gear1Input.getState() / 100.0f), x + 120, startY + rowHeight, 0xFFFFFF, false);
        graphics.drawString(font, String.format("%.2f", gear2Input.getState() / 100.0f), x + 120, startY + rowHeight * 2, 0xFFFFFF, false);
        graphics.drawString(font, String.format("%.2f", gear3Input.getState() / 100.0f), x + 120, startY + rowHeight * 3, 0xFFFFFF, false);
        graphics.drawString(font, String.format("%.2f", gear4Input.getState() / 100.0f), x + 120, startY + rowHeight * 4, 0xFFFFFF, false);
    }

    @Override
    public void removed() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("gearR", be.gearReverseRatio);
        nbt.putInt("gear1", be.gear1Ratio);
        nbt.putInt("gear2", be.gear2Ratio);
        nbt.putInt("gear3", be.gear3Ratio);
        nbt.putInt("gear4", be.gear4Ratio);
        
        CCTPackets.getChannel().sendToServer(new FourSpeedTransmissionConfigurationPacket(be.getBlockPos(), nbt));
        super.removed();
    }
}

