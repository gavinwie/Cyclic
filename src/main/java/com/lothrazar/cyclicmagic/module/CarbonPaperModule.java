package com.lothrazar.cyclicmagic.module;
import com.lothrazar.cyclicmagic.item.ItemPaperCarbon;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraftforge.common.config.Configuration;

public class CarbonPaperModule extends BaseModule {
  private boolean moduleEnabled;
  public void onInit() {
    ItemRegistry.carbon_paper = new ItemPaperCarbon();
    ItemRegistry.addItem(ItemRegistry.carbon_paper, "carbon_paper");
  }
  @Override
  public void syncConfig(Configuration config) {
    moduleEnabled = config.getBoolean("CarbonPaper", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }
  @Override
  public boolean isEnabled() {
    return moduleEnabled;
  }
}
