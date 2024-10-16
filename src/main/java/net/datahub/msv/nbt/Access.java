package net.datahub.msv.nbt;

public interface Access {
    Integer stage = 0;
    String mutation = "none";
    String gift = "none";
    Integer freezeCoolDown = 0;
    Integer sneezeCoolDown = 0;
    Integer hallucinationCoolDown = 0;
    Integer infection = 0;
    Boolean infected = false;
    Integer sneezePicking = 0;
    Integer zombieEatingCD = 0;
    Integer itemDroppingCD = 0;

    default int getStage() {
        return this.stage;
    }
    default void setStage(int stage) {}

    default String getMutation() {
        return this.mutation;
    }
    default void setMutation(String mutation) {}

    default String getGift() {
        return this.gift;
    }
    default void setGift(String gift) {}

    default int getFreezeCoolDown() {
        return this.freezeCoolDown;
    }
    default void setFreezeCoolDown(int freezeCoolDown) {}

    default int getSneezeCoolDown() {
        return this.sneezeCoolDown;
    }
    default void setSneezeCoolDown(int sneezeCoolDown) {}

    default int getHallucinationCoolDown() {
        return this.hallucinationCoolDown;
    }
    default void setHallucinationCoolDown(int hallucinationCoolDown) {}

    default int getInfection() {
        return this.infection;
    }
    default void setInfection(int infection) {}

    default Boolean isInfected() {
        return this.infected;
    }
    default void setInfected(Boolean infected) {}

    default int getSneezePicking() {
        return this.sneezePicking;
    }
    default void setSneezePicking(int sneezePicking) {}

    default int getZombieEatingCD() {
        return this.zombieEatingCD;
    }
    default void setZombieEatingCD(int zombieEatingCD) {}

    default int getItemDroppingCD() {
        return this.itemDroppingCD;
    }
    default void setItemDroppingCD(int itemDroppingCD) {}
}