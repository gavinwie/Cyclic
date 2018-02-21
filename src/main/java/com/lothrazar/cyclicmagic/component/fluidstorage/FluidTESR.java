package com.lothrazar.cyclicmagic.component.fluidstorage;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidTESR extends TileEntitySpecialRenderer<TileEntityBucketStorage> {
  @Override
  public void render(TileEntityBucketStorage te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    FluidStack fluidStack = te.getCurrentFluidStack();
    if (fluidStack == null) {
      return;
    }
    //TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
    GlStateManager.pushMatrix();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glEnable(GL11.GL_CULL_FACE);
    GL11.glDisable(GL11.GL_LIGHTING);
    if (fluidStack != null) {
      Fluid fluid = fluidStack.getFluid();
      Tessellator tess = Tessellator.getInstance();
      BufferBuilder buffer = tess.getBuffer();
      bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      //TODO: fluid liumin
      UtilRender.glowOn(fluid.getLuminosity());
      TextureAtlasSprite still = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());
      TextureAtlasSprite flow = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFlowing().toString());
      //so we get range smaller THAN [0,1] -> avoids texture layer fighting
      double posY = 0.02 + (.95 * ((float) fluidStack.amount / (float) te.getCapacity()));
      int icolor = fluidStack.getFluid().getColor(fluidStack);
      //RGB encoded in hexval integer
      float red = (icolor >> 16 & 0xFF) / 255.0F;
      float green = (icolor >> 8 & 0xFF) / 255.0F;
      float blue = (icolor & 0xFF) / 255.0F;
      float alph = 1.0F;
      // THANKS FOR POST http://www.minecraftforge.net/forum/topic/44388-1102-render-fluid-level-in-tank-with-tesr/
      // T/B for top and bottom
      float T = 15.5F / 16F;
      float B = 0.5F / 16F;
      int S = 1, E = 15;//for start and end. vertex ranges from [0,16];
      //TOP SIDE
      buffer.setTranslation(x, y, z);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos(B, posY, T).tex(still.getInterpolatedU(S), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, T).tex(still.getInterpolatedU(E), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, B).tex(still.getInterpolatedU(E), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(B, posY, B).tex(still.getInterpolatedU(S), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      tess.draw();
      //BOTTOM SIDE
      buffer.setTranslation(x, y - posY + B, z);//
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos(B, posY, B).tex(still.getInterpolatedU(S), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, B).tex(still.getInterpolatedU(E), still.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, T).tex(still.getInterpolatedU(E), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(B, posY, T).tex(still.getInterpolatedU(S), still.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      tess.draw();
      //the +Z side 
      buffer.setTranslation(x, y, z);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos(T, B, T).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, T).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(B, posY, T).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(B, B, T).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      tess.draw();
      //now the opposite: -Z side 
      buffer.setTranslation(x, y, z + 1);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos(T, posY, -1 * T).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, B, -1 * T).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(B, B, -1 * T).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(B, posY, -1 * T).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      tess.draw();
      // the +X side  
      buffer.setTranslation(x, y, z);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos(T, B, B).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, B).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, T).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, B, T).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      tess.draw();
      // the -X side  
      buffer.setTranslation(x - 1 + 2 * B, y, z);
      buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
      buffer.pos(T, posY, B).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, B, B).tex(flow.getInterpolatedU(S), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, B, T).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(E)).color(red, green, blue, alph).endVertex();
      buffer.pos(T, posY, T).tex(flow.getInterpolatedU(E), flow.getInterpolatedV(S)).color(red, green, blue, alph).endVertex();
      tess.draw();
      buffer.setTranslation(0, 0, 0);
      UtilRender.glowOff();
    }
    GL11.glPopAttrib();
    GlStateManager.popMatrix();
  }
}
/**
 * I learned some of these tips and tricks from Mekanism fluid rendering
 * https://github.com/aidancbrady/Mekanism/blob/f9ec882bcebf685c3b75237bc90d460b217b52a8/src/main/java/mekanism/client/render/MekanismRenderer.java#L429
 *
 */
class UtilRender {
  private static float lightmapLastX;
  private static float lightmapLastY;
  private static boolean optifineBreak = false;
  public static void glowOn(int glow) {
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
    try {
      lightmapLastX = OpenGlHelper.lastBrightnessX;
      lightmapLastY = OpenGlHelper.lastBrightnessY;
    }
    catch (NoSuchFieldError e) {
      optifineBreak = true;
    }
    float glowRatioX = Math.min((glow / 15F) * 240F + lightmapLastX, 240);
    float glowRatioY = Math.min((glow / 15F) * 240F + lightmapLastY, 240);
    if (!optifineBreak) {
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, glowRatioX, glowRatioY);
    }
  }
  public static void glowOff() {
    if (!optifineBreak) {
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapLastX, lightmapLastY);
    }
    GL11.glPopAttrib();
  }
}