package com.github.tartaricacid.touhoulittlemaid.client.gui.item;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.other.SoulSpellContainer;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.SoulSpellPageMessage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SoulSpellScreen extends AbstractContainerScreen<SoulSpellContainer> {
    private static final ResourceLocation CONTAINER_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public SoulSpellScreen(SoulSpellContainer menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void init() {
        super.init();
        int buttonSize = 12;
        int buttonHeight = 12;
        int inwardShift = Math.round(((imageWidth - buttonSize) / 2.0F - 2) * 0.30F);
        int outwardShift = Math.round(((imageWidth - buttonSize) / 2.0F) * 0.15F);
        int downwardShift = Math.round(18 * (0.30F - 0.10F));
        int buttonX = leftPos + 2 + inwardShift - outwardShift;
        int buttonY = topPos - 1 + downwardShift;
        addRenderableWidget(new Button(buttonX, buttonY, buttonSize, buttonHeight, Component.literal("<"), button ->
                NetworkHandler.CHANNEL.sendToServer(new SoulSpellPageMessage(menu.getPage() - 1))));
        addRenderableWidget(new Button(leftPos + imageWidth - (buttonX - leftPos) - buttonSize, buttonY, buttonSize, buttonHeight, Component.literal(">"), button ->
                NetworkHandler.CHANNEL.sendToServer(new SoulSpellPageMessage(menu.getPage() + 1))));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, CONTAINER_TEXTURE);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        drawCenteredString(poseStack, font,
                Component.translatable("gui.touhou_little_maid.soul_spell.header", menu.getPage() + 1, menu.getPageCount()),
                leftPos + imageWidth / 2, topPos + 5, 0xFFFFFF);
        renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        font.draw(poseStack, playerInventoryTitle, 8, 130, 0x404040);
    }
}
