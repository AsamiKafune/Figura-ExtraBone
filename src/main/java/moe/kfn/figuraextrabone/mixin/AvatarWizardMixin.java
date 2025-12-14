package moe.kfn.figuraextrabone.mixin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import moe.kfn.figuraextrabone.imp.BlockBenchModelEx;
import moe.kfn.figuraextrabone.imp.WizardEntryEx;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.figuramc.figura.avatar.local.LocalAvatarFetcher;
import org.figuramc.figura.math.vector.FiguraVec3;
import org.figuramc.figura.utils.ColorUtils;
import org.figuramc.figura.utils.IOUtils;
import org.figuramc.figura.wizards.AvatarWizard;
import org.figuramc.figura.wizards.WizardEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

import static org.figuramc.figura.model.ParentType.*;
import static org.figuramc.figura.model.ParentType.Cape;
import static org.figuramc.figura.model.ParentType.ChestplatePivot;
import static org.figuramc.figura.model.ParentType.HelmetItemPivot;
import static org.figuramc.figura.model.ParentType.HelmetPivot;
import static org.figuramc.figura.model.ParentType.LeftBootPivot;
import static org.figuramc.figura.model.ParentType.LeftElytra;
import static org.figuramc.figura.model.ParentType.LeftElytraPivot;
import static org.figuramc.figura.model.ParentType.LeftItemPivot;
import static org.figuramc.figura.model.ParentType.LeftLeg;
import static org.figuramc.figura.model.ParentType.LeftLeggingPivot;
import static org.figuramc.figura.model.ParentType.LeftParrotPivot;
import static org.figuramc.figura.model.ParentType.LeftShoulderPivot;
import static org.figuramc.figura.model.ParentType.LeftSpyglassPivot;
import static org.figuramc.figura.model.ParentType.LeggingsPivot;
import static org.figuramc.figura.model.ParentType.RightArm;
import static org.figuramc.figura.model.ParentType.RightBootPivot;
import static org.figuramc.figura.model.ParentType.RightElytra;
import static org.figuramc.figura.model.ParentType.RightElytraPivot;
import static org.figuramc.figura.model.ParentType.RightItemPivot;
import static org.figuramc.figura.model.ParentType.RightLeg;
import static org.figuramc.figura.model.ParentType.RightLeggingPivot;
import static org.figuramc.figura.model.ParentType.RightParrotPivot;
import static org.figuramc.figura.model.ParentType.RightShoulderPivot;
import static org.figuramc.figura.model.ParentType.RightSpyglassPivot;

@Environment(EnvType.CLIENT)
@Mixin(value = AvatarWizard.class, remap = false)
public class AvatarWizardMixin {
    @Shadow
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    @Shadow
    private static String playerTexture = "";
    @Shadow
    private static String playerTextureSlim = "";
    @Shadow
    private static String capeTexture = "";
    @Shadow
    private static byte[] iconTexture;
    @Shadow
    private final HashMap<WizardEntry, Object> map = new HashMap<>();

    @Inject(method = "buildModel", at = @At("HEAD"), remap = false, cancellable = true)
    private void onBuildModel(CallbackInfoReturnable<byte[]> cir) {
        cir.setReturnValue(buildModel());
    }

    @Unique
    private byte[] buildMetadata(String name) {
        JsonObject root = new JsonObject();

        //name
        root.addProperty("name", name);

        //description
        String description = (String) map.get(WizardEntry.DESCRIPTION);
        root.addProperty("description", description == null ? "" : description);

        //authors
        String authorStr = (String) map.get(WizardEntry.AUTHORS);
        String playerName = MinecraftClient.getInstance().player.getName().getString();
        String[] authors = authorStr == null ? new String[]{playerName} : authorStr.split(",");
        if (authors.length == 0) authors = new String[]{playerName};

        JsonArray authorsJson = new JsonArray();
        for (String author : authors)
            authorsJson.add(author.trim());

        root.add("authors", authorsJson);

        //color
        root.addProperty("color", "#" + ColorUtils.rgbToHex(ColorUtils.Colors.random().vec));

        //return
        return GSON.toJson(root).getBytes();
    }

    @Unique
    private byte[] buildScript() {
        String script = "-- Mirror @MagicLab --\n";

        boolean hasPlayerModel = WizardEntry.PLAYER_MODEL.asBool(map);
        //blend lib
        if (WizardEntryEx.SUPPORT_BLEND.asBool(map))
            script += """
                    local BoneInit = require("extrabone_lib")
                    BoneInit({
                        { models.model_blend.root.Neck, "body" },
                        { models.model_blend.root.Body.chest, "body" },
                        { models.model_blend.root.LeftShoulder, "body" },
                        { models.model_blend.root.RightShoulder, "body" },
                        { models.model_blend.root.LeftShoulder.LeftArm.LArmLower, "leftArm" },
                        { models.model_blend.root.RightShoulder.RightArm.RArmLower, "rightArm" },
                        { models.model_blend.root.LeftLeg.LLegLower, "leftLeg" },
                        { models.model_blend.root.RightLeg.RLegLower, "rightLeg" }
                    })
                    """;

        //hide player
        if (hasPlayerModel && WizardEntry.HIDE_PLAYER.asBool(map))
            script += """

                    --hide vanilla model
                    vanilla_model.PLAYER:setVisible(false)
                    """;

        //hide armor
        boolean hideArmor = WizardEntry.HIDE_ARMOR.asBool(map);
        if (hideArmor)
            script += """

                    --hide vanilla armor model
                    vanilla_model.ARMOR:setVisible(false)
                    """;

        //helmet item fix :3
        if (hasPlayerModel && hideArmor && WizardEntry.HELMET_ITEM_PIVOT.asBool(map))
            script += """
                    --re-enable the helmet item
                    vanilla_model.HELMET_ITEM:setVisible(true)
                    """;

        //hide cape
        if (WizardEntry.HIDE_CAPE.asBool(map))
            script += """

                    --hide vanilla cape model
                    vanilla_model.CAPE:setVisible(false)
                    """;

        //hide cape
        if (WizardEntry.HIDE_ELYTRA.asBool(map))
            script += """

                    --hide vanilla elytra model
                    vanilla_model.ELYTRA:setVisible(false)
                    """;

        //empty events
        if (WizardEntry.EMPTY_EVENTS.asBool(map))
            script += """

                    --entity init event, used for when the avatar entity is loaded for the first time
                    function events.entity_init()
                      --player functions goes here
                    end

                    --tick event, called 20 times per second
                    function events.tick()
                      --code goes here
                    end

                    --render event, called every time your avatar is rendered
                    --it have two arguments, "delta" and "context"
                    --"delta" is the percentage between the last and the next tick (as a decimal value, 0.0 to 1.0)
                    --"context" is a string that tells from where this render event was called (the paperdoll, gui, player render, first person)
                    function events.render(delta, context)
                      --code goes here
                    end
                    """;

        //return
        return script.getBytes();
    }

    @Unique
    private byte[] buildModel(){
        boolean hasPlayer = WizardEntry.PLAYER_MODEL.asBool(map);
        boolean hasElytra = WizardEntry.ELYTRA.asBool(map);
        boolean hasCape = WizardEntry.CAPE.asBool(map);
        boolean hasCapeOrElytra = hasCape || hasElytra;
        boolean slim = WizardEntry.SLIM.asBool(map);
        boolean hasArmor = WizardEntry.ARMOR_PIVOTS.asBool(map);
        boolean isModelBlend = WizardEntryEx.SUPPORT_BLEND.asBool(map);

        //model
        BlockBenchModelEx model = new BlockBenchModelEx("free");

        //textures
        int playerTex = hasPlayer ? model.addImage("Skin", slim ? playerTextureSlim : playerTexture, 64, 64) : -1;
        int capeTex = hasCapeOrElytra ? model.addImage("Cape", capeTexture, 64, 32) : -1;

        //resolution
        if (hasPlayer)
            model.setResolution(64, 64);
        else if (hasCapeOrElytra)
            model.setResolution(64, 32);

        //base bones
        BlockBenchModelEx.Group root = model.addGroup("root", FiguraVec3.of());

        BlockBenchModelEx.Group neck = null;
        BlockBenchModelEx.Group LeftShoulder = null;
        BlockBenchModelEx.Group RightShoulder = null;
        if(isModelBlend) {
            neck = model.addGroup("Neck", FiguraVec3.of(0, 18, 0), root);
            LeftShoulder = model.addGroup("LeftShoulder", FiguraVec3.of(0, 18, 0), root);
            RightShoulder = model.addGroup("RightShoulder", FiguraVec3.of(0, 18, 0), root);
        }

        BlockBenchModelEx.Group head = model.addGroup(Head, FiguraVec3.of(0, 24, 0), isModelBlend?neck:root);
        BlockBenchModelEx.Group body = model.addGroup(Body, FiguraVec3.of(0, 24, 0), root);

        BlockBenchModelEx.Group leftArm = model.addGroup(LeftArm, FiguraVec3.of(-5, 22, 0), isModelBlend?LeftShoulder:root);
        BlockBenchModelEx.Group rightArm = model.addGroup(RightArm, FiguraVec3.of(5, 22, 0), isModelBlend?RightShoulder:root);
        BlockBenchModelEx.Group leftLeg = model.addGroup(LeftLeg, FiguraVec3.of(-1.9, 12, 0), root);
        BlockBenchModelEx.Group rightLeg = model.addGroup(RightLeg, FiguraVec3.of(1.9, 12, 0), root);

        BlockBenchModelEx.Group chest = null;
        BlockBenchModelEx.Group LLowerArm = null;
        BlockBenchModelEx.Group RLowerArm = null;
        BlockBenchModelEx.Group LLegLower = null;
        BlockBenchModelEx.Group RLegLower = null;

        if(isModelBlend) {
            chest = model.addGroup("chest", FiguraVec3.of(0, 18, 0), body);
            LLowerArm = model.addGroup("LArmLower", FiguraVec3.of(-5.5, 17.9375, 0), leftArm);
            RLowerArm = model.addGroup("RArmLower", FiguraVec3.of(5.5, 17.9375, 0), rightArm);
            LLegLower = model.addGroup("LLegLower", FiguraVec3.of(-1.9, 5.9375, 0), leftLeg);
            RLegLower = model.addGroup("RLegLower", FiguraVec3.of(1.9, 5.9375, 0), rightLeg);
        }

        //player
        if (hasPlayer) {

            generateCubeAndLayer(model, "Hat", FiguraVec3.of(-4, 24, -4), FiguraVec3.of(8, 8, 8), 0.5, head, 0, 0, 32, 0, playerTex);

            //body type
            if(isModelBlend) {
                //blend model
                generateCubeAndLayer(model, "Lower Jacket", FiguraVec3.of(-4, 12, -2), FiguraVec3.of(8, 6, 4), 0.25, body, 16, 22, 16, 38, playerTex,2);
                generateCubeAndLayer(model, "Upper Jacket", FiguraVec3.of(-4, 18, -2), FiguraVec3.of(8, 6, 4), 0.25, chest, 16, 16, 16, 32, playerTex,3);

                FiguraVec3 armSize = FiguraVec3.of(slim ? 3 : 4, 6, 4);
                generateCubeAndLayer(model, "Left Sleeve", FiguraVec3.of(slim ? -7 : -8, 18, -2), armSize, 0.25, leftArm, 32, 48, 48, 48, playerTex,0);
                generateCubeAndLayer(model, "Right Sleeve", FiguraVec3.of(4, 18, -2), armSize, 0.25, rightArm, 40, 16, 40, 32, playerTex,0);

                generateCubeAndLayer(model, "LLower Sleeve", FiguraVec3.of(slim ? -7 : -8, 12, -2), armSize, 0.25, LLowerArm, 32, 54, 48, 54, playerTex,1);
                generateCubeAndLayer(model, "RLower Sleeve", FiguraVec3.of(4, 12, -2), armSize, 0.25, RLowerArm, 40, 22, 40, 38, playerTex,1);

                generateCubeAndLayer(model, "Left Pants", FiguraVec3.of(-3.9, 6, -2), FiguraVec3.of(4, 6, 4), 0.25, leftLeg, 16, 48, 0, 48, playerTex,0);
                generateCubeAndLayer(model, "Right Pants", FiguraVec3.of(-0.1, 6, -2), FiguraVec3.of(4, 6, 4), 0.25, rightLeg, 0, 16, 0, 32, playerTex,0);

                generateCubeAndLayer(model, "LLower Pants", FiguraVec3.of(-3.9, 0, -2), FiguraVec3.of(4, 6, 4), 0.25, LLegLower, 16, 54, 0, 54, playerTex,1);
                generateCubeAndLayer(model, "RLower Pants", FiguraVec3.of(-0.1, 0, -2), FiguraVec3.of(4, 6, 4), 0.25, RLegLower, 0, 22, 0, 38, playerTex,1);

            } else {
                //original model
                generateCubeAndLayer(model, "Jacket", FiguraVec3.of(-4, 12, -2), FiguraVec3.of(8, 12, 4), 0.25, body, 16, 16, 16, 32, playerTex);

                FiguraVec3 armSize = FiguraVec3.of(slim ? 3 : 4, 12, 4);
                generateCubeAndLayer(model, "Left Sleeve", FiguraVec3.of(slim ? -7 : -8, 12, -2), armSize, 0.25, leftArm, 32, 48, 48, 48, playerTex);
                generateCubeAndLayer(model, "Right Sleeve", FiguraVec3.of(4, 12, -2), armSize, 0.25, rightArm, 40, 16, 40, 32, playerTex);

                generateCubeAndLayer(model, "Left Pants", FiguraVec3.of(-3.9, 0, -2), FiguraVec3.of(4, 12, 4), 0.25, leftLeg, 16, 48, 0, 48, playerTex);
                generateCubeAndLayer(model, "Right Pants", FiguraVec3.of(-0.1, 0, -2), FiguraVec3.of(4, 12, 4), 0.25, rightLeg, 0, 16, 0, 32, playerTex);
            }
        }

        //cape
        if (hasCape) {
            BlockBenchModelEx.Group cape = model.addGroup(Cape, FiguraVec3.of(0, 24, 2), root);
            BlockBenchModelEx.Cube cube = model.addCube("Cape", FiguraVec3.of(-5, 8, 2), FiguraVec3.of(10, 16, 1), cape);
            cube.generateBoxFaces(0, 0, capeTex, 1, 1);
        }

        //elytra
        if (hasElytra) {
            BlockBenchModelEx.Group elytra = model.addGroup("Elytra", FiguraVec3.of(0, 24, 2), root);

            //left wing
            BlockBenchModelEx.Group leftElytra = model.addGroup(LeftElytra, FiguraVec3.of(-5, 24, 2), elytra);
            BlockBenchModelEx.Cube cube = model.addCube(FiguraVec3.of(-5, 4, 2), FiguraVec3.of(10, 20, 2), leftElytra);
            cube.inflate = 1;
            cube.generateBoxFaces(22, 0, capeTex, 1, 1);

            //right wing
            BlockBenchModelEx.Group rightElytra = model.addGroup(RightElytra, FiguraVec3.of(5, 24, 2), elytra);
            cube = model.addCube(FiguraVec3.of(-5, 4, 2), FiguraVec3.of(10, 20, 2), rightElytra);
            cube.inflate = 1;
            cube.generateBoxFaces(22, 0, capeTex, -1, 1);
        }

        //pivots
        if (WizardEntry.ITEMS_PIVOT.asBool(map)) {
            model.addGroup(LeftItemPivot, FiguraVec3.of(slim ? -5.5 : -6, 12, -2), isModelBlend?LLowerArm:leftArm);
            model.addGroup(RightItemPivot, FiguraVec3.of(slim ? 5.5 : 6, 12, -2), isModelBlend?RLowerArm:rightArm);
        }

        if (WizardEntry.SPYGLASS_PIVOT.asBool(map)) {
            model.addGroup(LeftSpyglassPivot, FiguraVec3.of(-2, 28, -4), head);
            model.addGroup(RightSpyglassPivot, FiguraVec3.of(2, 28, -4), head);
        }

        if (WizardEntry.HELMET_ITEM_PIVOT.asBool(map)) {
            model.addGroup(HelmetItemPivot, FiguraVec3.of(0, 24, 0), head);
        }

        if (WizardEntry.PARROTS_PIVOT.asBool(map)) {
            model.addGroup(LeftParrotPivot, FiguraVec3.of(-6, 24, 0), isModelBlend?chest:body);
            model.addGroup(RightParrotPivot, FiguraVec3.of(6, 24, 0), isModelBlend?chest:body);
        }

        if (hasArmor) {
            model.addGroup(HelmetPivot, FiguraVec3.of(0, 24, 0), head);
            model.addGroup(ChestplatePivot, FiguraVec3.of(0, 24, 0), body);

            model.addGroup(LeftElytraPivot, FiguraVec3.of(0, 24, 0), isModelBlend?chest:body);
            model.addGroup(RightElytraPivot, FiguraVec3.of(0, 24, 0), isModelBlend?chest:body);

            model.addGroup(LeftShoulderPivot, FiguraVec3.of(-6, 24, 0), leftArm);
            model.addGroup(RightShoulderPivot, FiguraVec3.of(6, 24, 0), rightArm);

            model.addGroup(LeggingsPivot, FiguraVec3.of(0, 12, 0), body);

            model.addGroup(LeftLeggingPivot, FiguraVec3.of(-2, 12, 0), leftLeg);
            model.addGroup(RightLeggingPivot, FiguraVec3.of(2, 12, 0), rightLeg);

            model.addGroup(LeftBootPivot, FiguraVec3.of(-2, 0, 0), isModelBlend?LLegLower:leftLeg);
            model.addGroup(RightBootPivot, FiguraVec3.of(2, 0, 0), isModelBlend?RLegLower:rightLeg);
        }

        //return
        return GSON.toJson(model.build()).getBytes();
    }

    @Inject(method = "build", at = @At("HEAD"), remap = false, cancellable = true)
    private void build(CallbackInfo ci) throws IOException {
        //file io
        Path root = LocalAvatarFetcher.getLocalAvatarDirectory();
        String name = (String) map.get(WizardEntry.NAME);
        String filename = name.replaceAll(IOUtils.INVALID_FILENAME_REGEX, "_");

        Path folder = root.resolve(filename);
        int i = 1;
        while (Files.exists(folder)) {
            folder = root.resolve(filename + "_" + i);
            i++;
        }

        //metadata
        byte[] metadata = buildMetadata(name);

        //script
        byte[] script = null;
        if (WizardEntry.DUMMY_SCRIPT.asBool(map) || WizardEntryEx.SUPPORT_BLEND.asBool(map))
            script = buildScript();

        //model
        byte[] model = null;
        if (WizardEntry.DUMMY_MODEL.asBool(map))
            model = buildModel();

        byte[] readme = null;
        if(WizardEntryEx.SUPPORT_BLEND.asBool(map))
            readme = """
                The Model / Script \"extrabone_lib.lua\" is from the Figura ExtraBone addon.
                For more information, contact Discord: kafunech
                """.getBytes();

        byte[] blendScript = null;
        if(WizardEntryEx.SUPPORT_BLEND.asBool(map))
            blendScript = """
                    -- ## extrabone_lib -> Emotecraft Support Library ##
                    
                    -- This script only supports Figura with the ExtraBone Addon and cannot be used outside of that Addon.
                    -- If you're interested, you can download the mod at: https://modrinth.com/mod/figura_extrabone
                    -- For more information, contact Discord: kafunech
                    
                    local boneList = {}
                    local function ExtraBoneInit(list)
                        boneList = list
                    end
                    
                    events.RENDER:register(function(delta)
                        local uuid = player:getUUID()
                        if client:isModLoaded("figuraextrabone") then
                            for key, value in pairs(boneList) do
                                value[1]:rot(vec((ExtraBone.getBone(uuid,value[2])[2] * (180/math.pi)) * -1, 0, 0))
                            end
                        end
                    end)
                    
                    return ExtraBoneInit
                    """.getBytes();

        //write files
        new IOUtils.DirWrapper(folder)
                .create()
                .write("avatar.json", metadata)
                .write("script.lua", script)
                .write("extrabone_lib.lua", blendScript)
                .write("readme.txt", readme)
                .write(WizardEntryEx.SUPPORT_BLEND.asBool(map) ? "model_blend.bbmodel" : "model.bbmodel", model)
                .write("avatar.png", iconTexture);

        //open file manager
        Util.getOperatingSystem().open(folder.toUri());
        ci.cancel();
    }

    @Unique
    private static void generateCubeAndLayer(BlockBenchModelEx model, String layerName, FiguraVec3 position, FiguraVec3 size, double inflation, BlockBenchModelEx.Group parent, int x1, int y1, int x2, int y2, int texture, int type) {
        BlockBenchModelEx.Cube c = model.addCube(position, size, parent);
        c.generateBoxFaces(x1, y1, texture, type);
        BlockBenchModelEx.Cube l = model.addCube(layerName, position, size, parent);
        l.inflate = inflation;
        l.generateBoxFaces(x2, y2, texture, type);
    }

    @Unique
    private static void generateCubeAndLayer(BlockBenchModelEx model, String layerName, FiguraVec3 position, FiguraVec3 size, double inflation, BlockBenchModelEx.Group parent, int x1, int y1, int x2, int y2, int texture) {
        BlockBenchModelEx.Cube c = model.addCube(position, size, parent);
        c.generateBoxFaces(x1, y1, texture);
        BlockBenchModelEx.Cube l = model.addCube(layerName, position, size, parent);
        l.inflate = inflation;
        l.generateBoxFaces(x2, y2, texture);
    }
}
