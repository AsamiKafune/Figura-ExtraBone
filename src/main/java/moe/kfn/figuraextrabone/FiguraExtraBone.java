package moe.kfn.figuraextrabone;

import com.mojang.logging.LogUtils;
import moe.kfn.figuraextrabone.imp.WizardEntryEx;
import moe.kfn.figuraextrabone.lua.plugins.ExtraBonePlugin;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import org.slf4j.Logger;

@Mod(FiguraExtraBone.MODID)
public class FiguraExtraBone {
    public static final String MODID = "figuraextrabone";
    public static final Logger LOGGER = LogUtils.getLogger();

    public FiguraExtraBone(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Figura Extrabone by MagicLab | Asamikafune");
        new ExtraBonePlugin();
        WizardEntryEx.init();
    }
}
