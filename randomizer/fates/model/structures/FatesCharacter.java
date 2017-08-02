package randomizer.fates.model.structures;

import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.common.utils.ByteUtils;
import randomizer.fates.singletons.FatesData;
import randomizer.fates.singletons.FatesGui;

import java.util.Arrays;

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
    private boolean royal = false;
    private CharacterType characterType;

    // Class data.
    private Job characterClass;
    private Job[] reclasses;

    // Randomizer-only data.
    private String targetPid;
    private String linkedPid; // Used for Parent/child randomization.
    private transient boolean hasSwappedStats;

    public FatesCharacter() {}

    @Override
    public String toString() {
        boolean[] options = FatesGui.getInstance().getSelectedOptions();
        StringBuilder res = new StringBuilder();
        res.append("Name: ").append(name).append("\n");
        if(id != 0) {
            res.append("Stats: ").append(ByteUtils.toString(stats)).append("\n");
            res.append("Growths: ").append(ByteUtils.toString(growths)).append("\n");
            res.append("Modifiers: ").append(ByteUtils.toString(modifiers)).append("\n");
        }
        if(options[0]) {
            res.append("Class: ").append(characterClass).append("\n");
            res.append("Reclasses: ").append(Arrays.toString(reclasses)).append("\n");
        }
        if(options[1]) {
            res.append("Skills: ").append(Arrays.toString(skills)).append("\n");
            res.append("Personal Skill: ").append(FatesData.getInstance().getSkillById(personSkill)).append("\n");
        }
        if(options[3]) {
            res.append("Replacing: ").append(FatesData.getInstance().getByPid(targetPid).getName()).append("\n");
            if(linkedPid != null)
                res.append("Parent/Child: ").append(FatesData.getInstance().getByPid(linkedPid).getName()).append("\n");
            res.append("Level: ").append(level).append("\n");
            res.append("Internal Level: ").append(internalLevel).append("\n");
        }
        return res.toString();
    }

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

    public String getTargetPid() {
        return targetPid;
    }

    public void setTargetPid(String targetPid) {
        this.targetPid = targetPid;
    }

    public String getLinkedPid() {
        return linkedPid;
    }

    public void setLinkedPid(String linkedPid) {
        this.linkedPid = linkedPid;
    }

    public boolean hasSwappedStats() {
        return hasSwappedStats;
    }

    public void setHasSwappedStats(boolean hasSwappedStats) {
        this.hasSwappedStats = hasSwappedStats;
    }

    public boolean isRoyal() {
        return royal;
    }

    public void setRoyal(boolean royal) {
        this.royal = royal;
    }

    public String getPidA() {
        return pid.replace("PID_", "PID_A_");
    }

    public String getPidB() {
        return pid.replace("PID_", "PID_B_");
    }

    public String getPidC() {
        return pid.replace("PID_", "PID_C_");
    }
}
