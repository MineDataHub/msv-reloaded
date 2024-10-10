package datahub.msv.nbt

interface Access {
    fun getStage(): Int
    fun setStage(int: Int)

    fun getMutation(): String
    fun setMutation(string: String)

    fun getGift(): String
    fun setGift(string: String)

    fun getFreezeCoolDown(): Int
    fun setFreezeCoolDown(int: Int)

    fun getSneezeCoolDown(): Int
    fun setSneezeCoolDown(int: Int)

    fun getHallucinationCoolDown(): Int
    fun setHallucinationCoolDown(int: Int)

    fun getInfection(): Int
    fun setInfection(int: Int)

    fun isInfected(): Boolean
    fun setInfected(boolean: Boolean)
}