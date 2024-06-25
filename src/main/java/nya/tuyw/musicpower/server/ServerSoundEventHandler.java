package nya.tuyw.musicpower.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;
import nya.tuyw.musicpower.MusicPower;
import nya.tuyw.musicpower.init.ConfigHandler;

public class ServerSoundEventHandler {
    public static void handleSoundEvent(MinecraftServer server, BlockPos soundPos, ResourceLocation soundType) {
        for (ConfigHandler.EffectConfig effectConfig : ConfigHandler.getEffectConfigs()) {
            if (effectConfig.getSound().equals(soundType.toString())) {
                MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectConfig.getBuff()));
                if (mobEffect == null) {
                    MusicPower.LOGGER.warn(effectConfig.getBuff()+"-not found. with-"+effectConfig.getSound());
                    continue;
                }
                int buffStrength = effectConfig.getStrength() - 1;
                int buffDuration = effectConfig.getDuration() * 20;
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    double distanceSq = player.blockPosition().distSqr(soundPos);
                    if (distanceSq < effectConfig.getDistance() * effectConfig.getDistance()) {
                        if (effectConfig.getStrengthDecaywithDistance() || effectConfig.getDurarionDecaywithDistance()) {
                            double distance = Math.sqrt(distanceSq);
                            double maxDistance = effectConfig.getDistance();
                            double distanceF = (maxDistance - distance) / maxDistance;
                            if (effectConfig.getStrengthDecaywithDistance()) {
                                buffStrength = (int) ((buffStrength + 1) * distanceF);
                            }
                            if (effectConfig.getDurarionDecaywithDistance()) {
                                buffDuration = (int) (buffDuration * distanceF);
                            }
                        }
                        if (buffDuration > 0 && buffStrength >= 0) {
                            player.addEffect(new MobEffectInstance(mobEffect, buffDuration, buffStrength));
                            if (!effectConfig.getSendMessage().isEmpty()) {
                                player.sendSystemMessage(Component.literal(effectConfig.getSendMessage()));
                            }
                        }
                    }
                }
            }
        }
    }
}
