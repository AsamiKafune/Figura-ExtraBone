package moe.kfn.figuraextrabone.lua.plugins;

import moe.kfn.figuraextrabone.FiguraExtraBone;
import moe.kfn.figuraextrabone.lua.ExtraBoneAPI;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.entries.FiguraAPI;
import org.figuramc.figura.entries.annotations.FiguraAPIPlugin;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaTypeDoc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.figuramc.figura.lua.FiguraAPIManager.API_GETTERS;
import static org.figuramc.figura.lua.FiguraAPIManager.WHITELISTED_CLASSES;

@FiguraAPIPlugin
@LuaWhitelist
public class ExtraBonePlugin implements FiguraAPI {

    public static final Class<?>[] FIGURAEMOTEFIX_PLUGIN_CLASSES = new Class[] {
            ExtraBonePlugin.class,
    };

    public static final Class<?>[] FIGURAEMOTEFIX_DOC_CLASSES = new Class[] {};

    public static final String PLUGIN_ID = FiguraExtraBone.MODID;
    private Avatar avatar;

    public ExtraBonePlugin(Avatar avatar) {
        this.avatar = avatar;
    }

    public ExtraBonePlugin() {
        WHITELISTED_CLASSES.add(ExtraBoneAPI.class);
        API_GETTERS.put("ExtraBone", r -> new ExtraBoneAPI());
    }

    @Override
    public FiguraAPI build(Avatar avatar) {
        return new ExtraBonePlugin(avatar);
    }

    @Override
    public String getName() {
        return PLUGIN_ID;
    }

    @Override
    public Collection<Class<?>> getWhitelistedClasses() {
        List<Class<?>> classesToRegister = new ArrayList<>();
        for (Class<?> aClass : FIGURAEMOTEFIX_PLUGIN_CLASSES) {
            if (aClass.isAnnotationPresent(LuaWhitelist.class)) {
                classesToRegister.add(aClass);
            }
        }
        return classesToRegister;
    }

    @Override
    public Collection<Class<?>> getDocsClasses() {
        List<Class<?>> classesToRegister = new ArrayList<>();
        for (Class<?> aClass : FIGURAEMOTEFIX_DOC_CLASSES) {
            if (aClass.isAnnotationPresent(LuaTypeDoc.class)) {
                classesToRegister.add(aClass);
            }
        }
        return classesToRegister;
    }
}
