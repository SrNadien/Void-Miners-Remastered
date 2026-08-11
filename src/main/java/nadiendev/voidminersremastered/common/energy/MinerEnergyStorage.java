package nadiendev.voidminersremastered.common.energy;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

/**
 * Energy buffer of the miner controller.
 *
 * <p>Ported from {@code net.neoforged.neoforge.energy.EnergyStorage} (deprecated {@code forRemoval} since
 * 1.21.9, and no longer what {@code Capabilities.Energy.BLOCK} resolves to) to
 * {@link SimpleEnergyHandler}, the transaction-aware {@code EnergyHandler} implementation of
 * {@code net.neoforged.neoforge.transfer.energy}.
 *
 * <p>The legacy accessor names ({@link #getEnergyStored()} / {@link #getMaxEnergyStored()}) are kept so
 * the Jade providers and the block entity renderers keep compiling unchanged.
 *
 * <p>NBT note: this class no longer serializes itself. {@code MinerControllerBE} stores the energy as a
 * plain int under the key {@code "energy"}, which is byte-for-byte the same NBT the 1.21.1 build wrote
 * ({@code EnergyStorage.serializeNBT} returned an {@code IntTag}).
 */
public class MinerEnergyStorage extends SimpleEnergyHandler {

    public MinerEnergyStorage(int capacity) {
        super(capacity);
    }

    public MinerEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public MinerEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public MinerEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    /** Legacy {@code IEnergyStorage} accessor, kept for the tooltips / Jade / renderers. */
    public int getEnergyStored() {
        return this.energy;
    }

    /** Legacy {@code IEnergyStorage} accessor, kept for the tooltips / Jade / renderers. */
    public int getMaxEnergyStored() {
        return this.capacity;
    }

    public int getMaxReceive() {
        return this.maxInsert;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }

    public void setEnergy(int energy) {
        int previous = this.energy;
        this.energy = Math.max(0, energy);
        if (previous != this.energy) {
            onEnergyChanged(previous);
        }
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(0, capacity);
    }

    public void removeEnergy(int remove) {
        int previous = this.energy;
        this.energy = Math.max(0, this.energy - remove);
        if (previous != this.energy) {
            onEnergyChanged(previous);
        }
    }
}
