package moe.kfn.figuraextrabone;

import com.mojang.logging.LogUtils;
import moe.kfn.figuraextrabone.imp.WizardEntryEx;
import moe.kfn.figuraextrabone.lua.plugins.ExtraBonePlugin;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(FiguraExtraBone.MODID)
public class FiguraExtraBone
{
    public static final String MODID = "figuraextrabone";
    private static final Logger LOGGER = LogUtils.getLogger();

    public FiguraExtraBone(FMLJavaModLoadingContext context)
    {
        LOGGER.info("Figura Extrabone by MagicLab | Asamikafune");
        new ExtraBonePlugin();
        WizardEntryEx.init();
    }
}
