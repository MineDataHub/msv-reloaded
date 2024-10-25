package net.datahub.msv.access;

public interface PlayerAccess {
    default int getStage() {
        return 0;
    }
    default void setStage(int stage) {}

    default String getMutation() {
        return "";
    }
    default void setMutation(String mutation) {}

    default String getGift() {
        return "";
    }
    default void setGift(String gift) {}

    default int getFreezeCoolDown() {
        return 0;
    }
    default void setFreezeCoolDown(int freezeCoolDown) {}

    default int getSneezeCoolDown() {
        return 0;
    }
    default void setSneezeCoolDown(int sneezeCoolDown) {}

    default int getHallucinationCoolDown() {
        return 0;
    }
    default void setHallucinationCoolDown(int hallucinationCoolDown) {}

    default int getInfection() {
        return 0;
    }
    default void setInfection(int infection) {}

    default int getSneezePicking() {
        return 0;
    }
    default void setSneezePicking(int sneezePicking) {}

    default int getZombieEatingCD() {
        return 0;
    }
    default void setZombieEatingCD(int zombieEatingCD) {}

    default int getItemDroppingCD() {
        return 0;
    }
    default void setItemDroppingCD(int itemDroppingCD) {}
    
    default int getFrozenTime() {
        return 0;
    }
    default void setFrozenTime(int frozenTime) {}
    default void addFrozenTime(int frozenTime) {}
}