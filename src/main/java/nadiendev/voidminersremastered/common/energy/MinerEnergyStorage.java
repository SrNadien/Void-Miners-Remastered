package nadiendev.voidminersremastered.common.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

public class MinerEnergyStorage extends EnergyStorage {
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

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void removeEnergy(int remove) {
        this.energy = Math.max(0, this.energy - remove);
    }
}
