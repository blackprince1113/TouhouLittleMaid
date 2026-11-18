package com.github.tartaricacid.touhoulittlemaid.event;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID)
public final class TemporaryChunkLoader {
    private static final long LOAD_DURATION_TICKS = 60;
    private static final Map<MinecraftServer, Long> SERVER_TICKS = new WeakHashMap<>();
    private static final Map<ServerLevel, Map<Long, Long>> TEMPORARY_CHUNKS = new HashMap<>();

    private TemporaryChunkLoader() {
    }

    public static void loadForThreeSeconds(ServerLevel level, ChunkPos chunkPos) {
        long chunkKey = chunkPos.toLong();
        Map<Long, Long> scheduledChunks = TEMPORARY_CHUNKS.computeIfAbsent(level, key -> new HashMap<>());
        long currentTick = SERVER_TICKS.getOrDefault(level.getServer(), 0L);
        if (scheduledChunks.containsKey(chunkKey)) {
            scheduledChunks.put(chunkKey, currentTick + LOAD_DURATION_TICKS);
        } else if (!level.getForcedChunks().contains(chunkKey)) {
            level.setChunkForced(chunkPos.x, chunkPos.z, true);
            scheduledChunks.put(chunkKey, currentTick + LOAD_DURATION_TICKS);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = event.getServer();
        long currentTick = SERVER_TICKS.merge(server, 1L, Long::sum);
        Iterator<Map.Entry<ServerLevel, Map<Long, Long>>> levels = TEMPORARY_CHUNKS.entrySet().iterator();
        while (levels.hasNext()) {
            Map.Entry<ServerLevel, Map<Long, Long>> levelEntry = levels.next();
            ServerLevel level = levelEntry.getKey();
            if (level.getServer() != server) {
                continue;
            }
            Iterator<Map.Entry<Long, Long>> chunks = levelEntry.getValue().entrySet().iterator();
            while (chunks.hasNext()) {
                Map.Entry<Long, Long> chunk = chunks.next();
                if (currentTick >= chunk.getValue()) {
                    ChunkPos chunkPos = new ChunkPos(chunk.getKey());
                    level.setChunkForced(chunkPos.x, chunkPos.z, false);
                    chunks.remove();
                }
            }
            if (levelEntry.getValue().isEmpty()) {
                levels.remove();
            }
        }
    }
}
