package net.flame.companionmob.entity;

import java.util.UUID;

public interface Tameable {
    boolean isTamed();
    void setTamed(boolean tamed);

    UUID getOwnerUuid();
    void setOwnerUuid(UUID uuid);
}
