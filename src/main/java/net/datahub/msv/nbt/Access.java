package net.datahub.msv.nbt;

public interface Access {
    default int getStage() {
        return 0;
    }

    default void setStage(int stage) {}

    default String getMutation() {
        return "none";
    }

    default void setMutation(String mutation) {}

    default String getGift() {
        return "none";
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

    default boolean isInfected() {
        return false;
    }

    default void setInfected(boolean infected) {}
}