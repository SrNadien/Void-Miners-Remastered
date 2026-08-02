package nadiendev.voidminersremastered.init;

import com.mojang.serialization.Codec;
import nadiendev.voidminersremastered.VoidMinersRemastered;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, VoidMinersRemastered.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> MAX_STORAGE_UPGRADE_SLOTS = register("max_storage_upgrade_slots",
            builder -> builder.persistent(Codec.INT));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }
}
