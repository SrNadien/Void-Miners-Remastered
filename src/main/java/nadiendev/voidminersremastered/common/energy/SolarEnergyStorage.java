package nadiendev.voidminersremastered.common.energy;

import com.google.common.primitives.Ints;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

/**
 * Energy buffer of the solar controller.
 *
 * <p>In 1.21.1 this class extended the (now deprecated) {@code net.neoforged.neoforge.energy.EnergyStorage}
 * and faked 64-bit storage by keeping a shadow {@code long} next to the {@code int} of the superclass.
 * In 26.1.2 the capability type is {@code net.neoforged.neoforge.transfer.energy.EnergyHandler}, whose
 * amount/capacity are natively {@code long}, so the class now implements it directly and the duplicated
 * {@code int} state is gone. Only the per-operation transfer amounts stay {@code int}, because
 * {@code EnergyHandler#insert}/{@code #extract} are {@code int}-based.
 *
 * <p>NBT is unchanged: {@link #serialize(ValueOutput)} writes the very same {@code {energy: long,
 * capacity: long}} compound the 1.21.1 {@code serializeNBT} produced.
 */
public class SolarEnergyStorage implements EnergyHandler, ValueIOSerializable {

    protected long longCapacity;
    protected long longEnergy;
    protected long maxReceive;
    protected long maxExtract;

    private final EnergyJournal energyJournal = new EnergyJournal();

    public SolarEnergyStorage(long capacity) {
        this(capacity, capacity, capacity, 0L);
    }

    public SolarEnergyStorage(long capacity, long maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0L);
    }

    public SolarEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        this(capacity, maxReceive, maxExtract, 0L);
    }

    public SolarEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        this.longCapacity = Math.max(0L, capacity);
        this.maxReceive = Math.max(0L, maxReceive);
        this.maxExtract = Math.max(0L, maxExtract);
        this.longEnergy = Math.max(0L, Math.min(this.longCapacity, energy));
    }

    // ------------------------------------------------------------------ EnergyHandler

    @Override
    public long getAmountAsLong() {
        return this.longEnergy;
    }

    @Override
    public long getCapacityAsLong() {
        return this.longCapacity;
    }

    @Override
    public int insert(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);

        long inserted = Math.min(this.longCapacity - this.longEnergy, Math.min(amount, this.maxReceive));
        if (inserted > 0L) {
            this.energyJournal.updateSnapshots(transaction);
            this.longEnergy += inserted;
            return (int) inserted;
        }
        return 0;
    }

    @Override
    public int extract(int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonNegative(amount);

        long extracted = Math.min(this.longEnergy, Math.min(amount, this.maxExtract));
        if (extracted > 0L) {
            this.energyJournal.updateSnapshots(transaction);
            this.longEnergy -= extracted;
            return (int) extracted;
        }
        return 0;
    }

    // ------------------------------------------------------------------ ValueIOSerializable

    @Override
    public void serialize(ValueOutput output) {
        output.putLong("energy", this.longEnergy);
        output.putLong("capacity", this.longCapacity);
    }

    @Override
    public void deserialize(ValueInput input) {
        this.longCapacity = Math.max(0L, input.getLongOr("capacity", this.longCapacity));
        this.longEnergy = Math.max(0L, input.getLongOr("energy", 0L));
    }

    // ------------------------------------------------------------------ 64-bit API (unchanged)

    public long getLongEnergyStored() {
        return this.longEnergy;
    }

    public long getLongMaxEnergyStored() {
        return this.longCapacity;
    }

    /** Saturating {@code int} view, kept so legacy call sites keep compiling. */
    public int getEnergyStored() {
        return Ints.saturatedCast(this.longEnergy);
    }

    /** Saturating {@code int} view, kept so legacy call sites keep compiling. */
    public int getMaxEnergyStored() {
        return Ints.saturatedCast(this.longCapacity);
    }

    public void addLongEnergy(long add) {
        long previous = this.longEnergy;
        if ((this.longEnergy + add) < 0L) {
            this.longEnergy = this.longCapacity;
        } else {
            this.longEnergy = Math.min(this.longCapacity, this.longEnergy + add);
        }
        if (previous != this.longEnergy) {
            onEnergyChanged(previous);
        }
    }

    public void removeLongEnergy(long remove) {
        long previous = this.longEnergy;
        this.longEnergy = Math.max(0L, this.longEnergy - remove);
        if (previous != this.longEnergy) {
            onEnergyChanged(previous);
        }
    }

    public void setLongCapacity(long capacity) {
        long previous = this.longEnergy;
        this.longCapacity = Math.max(0L, capacity);
        this.longEnergy = Math.min(this.longEnergy, this.longCapacity);
        if (previous != this.longEnergy) {
            onEnergyChanged(previous);
        }
    }

    public void setLongEnergy(long energy) {
        long previous = this.longEnergy;
        this.longEnergy = Math.max(0L, Math.min(this.longCapacity, energy));
        if (previous != this.longEnergy) {
            onEnergyChanged(previous);
        }
    }

    public long getLongMaxReceive() {
        return this.maxReceive;
    }

    public long getLongMaxExtract() {
        return this.maxExtract;
    }

    /**
     * Called after the stored amount changed. For changes made through {@link #insert}/{@link #extract}
     * this runs when the root transaction commits; for the direct setters it runs immediately.
     */
    protected void onEnergyChanged(long previousAmount) {}

    /** Makes {@link #insert}/{@link #extract} honour transaction rollback. */
    private class EnergyJournal extends SnapshotJournal<Long> {
        @Override
        protected Long createSnapshot() {
            return longEnergy;
        }

        @Override
        protected void revertToSnapshot(Long snapshot) {
            longEnergy = snapshot;
        }

        @Override
        protected void onRootCommit(Long originalState) {
            long previousAmount = originalState;
            if (longEnergy != previousAmount) {
                onEnergyChanged(previousAmount);
            }
        }
    }
}
