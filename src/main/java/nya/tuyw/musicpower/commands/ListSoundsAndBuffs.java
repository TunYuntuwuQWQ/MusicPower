package nya.tuyw.musicpower.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import nya.tuyw.musicpower.MusicPower;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mod.EventBusSubscriber(modid = MusicPower.MODID)
public class ListSoundsAndBuffs {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("musicpower")
                        .then(Commands.literal("listsounds")
                                .requires(source -> source.hasPermission(2))
                                .executes(ListSoundsAndBuffs::listSounds))
                        .then(Commands.literal("listbuffs")
                                .requires(source -> source.hasPermission(2))
                                .executes(ListSoundsAndBuffs::listBuffs))
        );
    }
    private static int listSounds(CommandContext<CommandSourceStack> context) {
        File directory = FMLPaths.CONFIGDIR.get().resolve("MusicPower").toFile();
        File soundsFile = new File(directory, "ListedSounds.txt");

        try (FileWriter soundWriter = new FileWriter(soundsFile)) {
            soundWriter.write("Sounds:\n");
            for (ResourceLocation sound : ForgeRegistries.SOUND_EVENTS.getKeys()) {
                soundWriter.write(sound.toString() + "\n");
            }
            context.getSource().sendSuccess(()->Component.literal("Successfully listed all sounds to " + soundsFile.getAbsolutePath()), false);
            return 1;
        } catch (IOException e) {
            MusicPower.LOGGER.error("Failed to write ListedSounds.txt", e);
            context.getSource().sendFailure(Component.literal("Failed to write ListedSounds.txt"));
            return 0;
        }
    }
    private static int listBuffs(CommandContext<CommandSourceStack> context) {
        File directory = FMLPaths.CONFIGDIR.get().resolve("MusicPower").toFile();
        File buffsFile = new File(directory, "ListedBuffs.txt");

        try (FileWriter buffWriter = new FileWriter(buffsFile)) {
            buffWriter.write("Buffs:\n");
            for (ResourceLocation effect : ForgeRegistries.MOB_EFFECTS.getKeys()) {
                buffWriter.write(effect.toString() + "\n");
            }
            context.getSource().sendSuccess(()->Component.literal("Successfully listed all buffs to " + buffsFile.getAbsolutePath()), false);
            return 1;
        } catch (IOException e) {
            MusicPower.LOGGER.error("Failed to write ListedBuffs.txt", e);
            context.getSource().sendFailure(Component.literal("Failed to write ListedBuffs.txt"));
            return 0;
        }
    }
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ListSoundsAndBuffs.register(event.getDispatcher());
    }
}
