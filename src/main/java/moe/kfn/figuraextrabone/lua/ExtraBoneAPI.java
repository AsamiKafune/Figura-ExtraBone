package moe.kfn.figuraextrabone.lua;

import moe.kfn.figuraextrabone.utils.PlayerBlendHelper;
import org.figuramc.figura.lua.LuaWhitelist;
import org.figuramc.figura.lua.docs.LuaMethodDoc;
import org.figuramc.figura.lua.docs.LuaMethodOverload;
import org.figuramc.figura.lua.docs.LuaTypeDoc;
import org.figuramc.figura.math.vector.FiguraVec3;

import java.util.Objects;
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
                    argumentTypes = {String.class, String.class},
                    argumentNames = {"uuid", "modelPart"}
            ),
            value = "extra_bone.get_blend"
    )
    public static FiguraVec3 getBone(String uuid, String modelPart) {
        try {
            if (Objects.equals(modelPart, "leftArm")) {
                modelPart = "left_arm";
            }

            if (Objects.equals(modelPart, "rightArm")) {
                modelPart = "right_arm";
            }

            if (Objects.equals(modelPart, "leftLeg")) {
                modelPart = "left_leg";
            }

            if (Objects.equals(modelPart, "rightLeg")) {
                modelPart = "right_leg";
            }

            return FiguraVec3.of(0,PlayerBlendHelper.getBlend(UUID.fromString(uuid), modelPart),0);
        } catch (Exception e) {
            return FiguraVec3.of(0, 0, 0);
        }
    }

    @Override
    public String toString() {
        return "ExtraBoneAPI";
    }
}
