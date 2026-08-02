package nadiendev.voidminersremastered.common.energy;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.energy.EnergyStorage;

public class SolarEnergyStorage extends EnergyStorage {
    private long longCapacity;
    private long longEnergy;

    public SolarEnergyStorage(long capacity) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity);
        this.longCapacity = capacity;
        this.longEnergy = 0;
    }

    public SolarEnergyStorage(long capacity, long maxTransfer) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity,
                maxTransfer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxTransfer);
        this.longCapacity = capacity;
        this.longEnergy = 0;
    }

    public SolarEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity,
                maxReceive > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxReceive,
                maxExtract > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxExtract);
        this.longCapacity = capacity;
        this.longEnergy = 0;
    }

    public SolarEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity,
                maxReceive > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxReceive,
                maxExtract > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxExtract,
                energy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)energy);
        this.longCapacity = capacity;
        this.longEnergy = energy;
    }

    public long getLongEnergyStored() {
        return longEnergy;
    }

    public long getLongMaxEnergyStored() {
        return longCapacity;
    }

    public void addLongEnergy(long add) {
        if ((this.longEnergy + add) < 0) {
            this.longEnergy = this.longCapacity;
        } else {
            this.longEnergy = Math.min(this.longCapacity, this.longEnergy + add);
        }
        this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longEnergy;
    }

    public void removeLongEnergy(long remove) {
        this.longEnergy = Math.max(0, this.longEnergy - remove);
        this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longEnergy;
    }

    public void setLongCapacity(long capacity) {
        this.longCapacity = capacity;
        this.longEnergy = Math.min(this.longEnergy, this.longCapacity);
        this.capacity = this.longCapacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longCapacity;
    }

    public void setLongEnergy(long energy) {
        this.longEnergy = Math.max(0, Math.min(longCapacity, energy));
        this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longEnergy;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;

        int receivable = Math.min(maxReceive, this.maxReceive);
        long longReceivable = Math.min(receivable, longCapacity - longEnergy);

        if (!simulate && longReceivable > 0) {
            longEnergy += longReceivable;
            this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) longEnergy;
        }
        return (int) longReceivable;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        int extractable = Math.min(maxExtract, this.maxExtract);
        long longExtractable = Math.min(extractable, longEnergy);

        if (!simulate && longExtractable > 0) {
            longEnergy -= longExtractable;
            this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) longEnergy;
        }
        return (int) longExtractable;
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("energy", longEnergy);
        tag.putLong("capacity", longCapacity);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        if (!(nbt instanceof CompoundTag tag))
            return;

        this.longEnergy = tag.getLong("energy");
        this.longCapacity = tag.getLong("capacity");

        this.energy =
                longEnergy > Integer.MAX_VALUE
                        ? Integer.MAX_VALUE
                        : (int) longEnergy;

        this.capacity =
                longCapacity > Integer.MAX_VALUE
                        ? Integer.MAX_VALUE
                        : (int) longCapacity;
    }
}
