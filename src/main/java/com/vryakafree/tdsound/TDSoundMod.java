package com.vryakafree.tdsound;

import com.cobblemon.mod.common.api.events.battles.*;
import com.cobblemon.mod.common.api.events.pokemon.PokemonHealthChangedEvent;
import com.cobblemon.mod.common.battles.BattleResult;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;

public class TDSoundMod implements ModInitializer {

    public static final String MODID = "tdsound";

    @Override
    public void onInitialize() {
        // Register event listeners on server start
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // Cobblemon events are global, just subscribe here
            BattleStartEvent.EVENT.register(this::onBattleStart);
            BattleEndEvent.EVENT.register(this::onBattleEnd);
            PokemonHealthChangedEvent.EVENT.register(this::onHealthChange);
            PokemonFaintedEvent.EVENT.register(this::onPokemonFainted);
        });
    }

    private void onBattleStart(BattleStartEvent event) {
        ServerPlayer player = safePlayer(event.getPlayer());
        if (player != null) {
            playSound(player, "battle_theme");
        }
    }

    private void onHealthChange(PokemonHealthChangedEvent event) {
        Pokemon pokemon = event.getPokemon();
        if (pokemon == null || pokemon.getOwnerPlayer() == null)
            return;

        ServerPlayer player = safePlayer(pokemon.getOwnerPlayer());
        if (player == null)
            return;

        double hpPercent = (double) pokemon.getHealth() / pokemon.getMaxHealth();
        if (hpPercent <= 0.20) {
            playSound(player, "panic_theme");
        } else if (hpPercent >= 0.21) {
            playSound(player, "battle_theme");
        }
    }

    private void onPokemonFainted(PokemonFaintedEvent event) {
        Pokemon fainted = event.getFaintedPokemon();
        if (fainted.isOpponent() && fainted.getHealth() == 0) {
            ServerPlayer player = safePlayer(event.getBattle().getPlayer());
            if (player != null) {
                playSound(player, "victory_sound");
            }
        }
    }

    private void onBattleEnd(BattleEndEvent event) {
        if (event.getResult() == BattleResult.FLEE || event.getResult() == BattleResult.DEFEAT) {
            ServerPlayer player = safePlayer(event.getPlayer());
            if (player != null) {
                playSound(player, "flee_sound");
            }
        }
    }

    public void playSound(ServerPlayer player, String soundId) {
        ResourceLocation sound = new ResourceLocation(MODID, soundId);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(sound).value(), SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Nullable
    private ServerPlayer safePlayer(@Nullable Object playerObj) {
        if (playerObj instanceof ServerPlayer)
            return (ServerPlayer) playerObj;
        return null;
    }
}