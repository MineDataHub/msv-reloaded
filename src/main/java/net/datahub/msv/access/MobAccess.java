package net.datahub.msv.access;

public interface MobAccess {
    default Boolean isInfected() {
        return false;
    }
    default void setInfected(Boolean infected) {}
}
