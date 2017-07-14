package randomizer.fates.model.structures;

import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;

public class FatesCharacter {
    // Character label data.
    private String name;
    private String pid;
    private String sound;
    private String[] bevAliases;

    // Character stat data.
    private byte[] bitflags;
    private byte[] stats;
    private byte[] growths;
    private byte[] modifiers;
    private Skill[] skills;
    private short personSkill;
    private short id;
    private byte internalLevel;
    private byte level;

    // Character flags.
    private boolean male;
    private boolean promoted;
    private CharacterType characterType;

    // Class data.
    private Job characterClass;
    private Job[] reclasses;

    private FatesCharacter target;

    public FatesCharacter() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String[] getBevAliases() {
        return bevAliases;
    }

    public void setBevAliases(String[] bevAliases) {
        this.bevAliases = bevAliases;
    }

    public byte[] getBitflags() {
        return bitflags;
    }

    public void setBitflags(byte[] bitflags) {
        this.bitflags = bitflags;
    }

    public byte[] getStats() {
        return stats;
    }

    public void setStats(byte[] stats) {
        this.stats = stats;
    }

    public byte[] getGrowths() {
        return growths;
    }

    public void setGrowths(byte[] growths) {
        this.growths = growths;
    }

    public byte[] getModifiers() {
        return modifiers;
    }

    public void setModifiers(byte[] modifiers) {
        this.modifiers = modifiers;
    }

    public Skill[] getSkills() {
        return skills;
    }

    public void setSkills(Skill[] skills) {
        this.skills = skills;
    }

    public short getPersonSkill() {
        return personSkill;
    }

    public void setPersonSkill(short personSkill) {
        this.personSkill = personSkill;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public byte getInternalLevel() {
        return internalLevel;
    }

    public void setInternalLevel(byte internalLevel) {
        this.internalLevel = internalLevel;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public Job getCharacterClass() {
        return characterClass;
    }

    public void setCharacterClass(Job characterClass) {
        this.characterClass = characterClass;
    }

    public Job[] getReclasses() {
        return reclasses;
    }

    public void setReclasses(Job[] reclasses) {
        this.reclasses = reclasses;
    }

    public FatesCharacter getTarget() {
        return target;
    }

    public void setTarget(FatesCharacter target) {
        this.target = target;
    }

    public String getTaglessPid() {
        return pid.substring(4);
    }

    public String getAid() {
        return pid.replace("PID_", "AID_");
    }

    public String getFid() {
        return pid.replace("PID_", "FID_");
    }

    public String getMPid() {
        return pid.replace("PID_", "MPID_");
    }

    public String getMPidH() {
        return pid.replace("PID_", "MPID_H_");
    }

    public CharacterType getCharacterType() {
        return characterType;
    }

    public void setCharacterType(CharacterType characterType) {
        this.characterType = characterType;
    }
}
