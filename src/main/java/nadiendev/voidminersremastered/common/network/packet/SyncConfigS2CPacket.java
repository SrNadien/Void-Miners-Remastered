package nadiendev.voidminersremastered.common.network.packet;

import nadiendev.voidminersremastered.VoidMinersRemastered;
import nadiendev.voidminersremastered.config.MinerConfigLoader;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;


public record SyncConfigS2CPacket(Map<String, MinerConfigLoader.Config> minerConfigs) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncConfigS2CPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VoidMinersRemastered.MODID, "sync_config"));

    public static final StreamCodec<ByteBuf, SyncConfigS2CPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.map(
            HashMap::new,
            ByteBufCodecs.STRING_UTF8,
            MinerConfigLoader.Config.STREAM_CODEC
        ),
        SyncConfigS2CPacket::minerConfigs,
        SyncConfigS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncConfigS2CPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            MinerConfigLoader.getInstance().MINER_CONFIGS = packet.minerConfigs();
        });
    }
}