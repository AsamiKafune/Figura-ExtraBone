package moe.kfn.figuraextrabone.utils;

import com.zigythebird.playeranim.PlayerAnimLibMod;
import com.zigythebird.playeranim.accessors.IPlayerAnimationState;
import com.zigythebird.playeranim.animation.PlayerAnimManager;
import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.animation.layered.AnimationStack;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Random;
import java.util.UUID;

public class PlayerBlendHelper {


    public static float getBlend(UUID playerUuid, String modelPart){
        AbstractClientPlayerEntity player = getPlayer(playerUuid);
        PlayerAnimManager manager = PlayerAnimationAccess.getPlayerAnimManager(player);
        PlayerAnimBone bone = new PlayerAnimBone(modelPart);
        return manager.get3DTransform(bone).getBend();
    }


    private static AbstractClientPlayerEntity getPlayer(UUID playerUuid) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) return null;

        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p.getUuid().equals(playerUuid) && p instanceof AbstractClientPlayerEntity) {
                return p;
            }
        }
        return null;
    }
}