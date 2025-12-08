package moe.kfn.figuraextrabone.lua;

import moe.kfn.figuraextrabone.utils.PlayerBlendHelper;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.figuramc.figura.math.vector.FiguraVec3;
import org.joml.Vector3f;

import java.util.UUID;

@LuaWhitelist
@LuaTypeDoc(
        name = "ExtraBone",
        value = "extra_bone"
)
public class ExtraBoneAPI {
    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = {String.class,String.class},
                    argumentNames = {"uuid","modelPart"}
            ),
            value = "extra_bone.get_blend"
    )
    public static FiguraVec3 getBone(String uuid, String modelPart) {
        try {
            Vector3f rot = PlayerBlendHelper.getBlend(UUID.fromString(uuid), modelPart);
            return FiguraVec3.of(rot.x, rot.y, rot.z);
        } catch (Exception e) {
            return FiguraVec3.of(0, 0, 0);
        }
    }

    @Override
    public String toString() {
        return "ExtraBoneAPI";
    }
}
