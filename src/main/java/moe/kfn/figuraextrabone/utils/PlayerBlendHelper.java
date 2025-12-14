package moe.kfn.figuraextrabone.utils;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import org.joml.Vector3f;

import java.util.UUID;

public class PlayerBlendHelper {

    public static Vector3f getBlend(UUID playerUuid, String modelPart) {
        AbstractClientPlayerEntity player = getPlayer(playerUuid);
        if (player == null) return new Vector3f(0, 0, 0);

        Vec3f bendVec = getAnimationTransform(player, modelPart, TransformType.BEND);
        if (bendVec == null || bendVec == Vec3f.ZERO) {
            return new Vector3f(0, 0, 0);
        }

        return new Vector3f(bendVec.getX(), bendVec.getY(), bendVec.getZ());
    }

    private static AnimationStack getAnimationStack(AbstractClientPlayerEntity player) {
        try {
            return PlayerAnimationAccess.getPlayerAnimLayer(player);
        } catch (Exception e) {
        }
        return null;
    }

    private static Vec3f getAnimationTransform(AbstractClientPlayerEntity player, String partName, TransformType type) {
        try {
            AnimationStack stack = getAnimationStack(player);
            if (stack == null) return Vec3f.ZERO;

            float partialTicks = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
            return stack.get3DTransform(partName, type, partialTicks, Vec3f.ZERO);
        } catch (Exception e) {
            return Vec3f.ZERO;
        }
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