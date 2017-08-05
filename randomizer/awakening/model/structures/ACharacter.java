package randomizer.awakening.model.structures;

import randomizer.awakening.singletons.ACharacters;
import randomizer.common.enums.CharacterType;
import randomizer.common.structures.Job;
import randomizer.common.structures.Skill;
import randomizer.common.utils.ByteUtils;
import randomizer.fates.singletons.FatesGui;

import java.util.Arrays;

public class ACharacter {
    // Character label data.
    private String name;
    private String pid;
    private String sound;

    // Character stat data.
    private byte[] stats;
    private byte[] modifiers;
    private Skill[] skills;
    private short id;
    private byte level;

    // Character flags.
    private boolean male;
    private boolean promoted;
    private CharacterType characterType;

    // Class data.
    private Job characterClass;
    private Job[] reclasses;

    // Randomizer-only data.
    private String targetPid;
    private String linkedPid; // Used for Parent/child randomization.
    private transient boolean hasSwappedStats;

    public ACharacter() {}

    @Override
    public String toString() {
        boolean[] options = FatesGui.getInstance().getSelectedOptions();
        StringBuilder res = new StringBuilder();
        res.append("Name: ").append(name).append("\n");
        if(id != 0) {
            res.append("Stats: ").append(ByteUtils.toString(stats)).append("\n");
            res.append("Modifiers: ").append(ByteUtils.toString(modifiers)).append("\n");
        }
        if(options[0]) {
            res.append("Class: ").append(characterClass).append("\n");
            res.append("Reclasses: ").append(Arrays.toString(reclasses)).append("\n");
        }
        if(options[1]) {
            res.append("Skills: ").append(Arrays.toString(skills)).append("\n");
        }
        if(options[3]) {
            if(targetPid != null)
                res.append("Replacing: ").append(ACharacters.getInstance().getByPid(targetPid).getName()).append("\n");
            if(linkedPid != null)
                res.append("Parent/Child: ").append(ACharacters.getInstance().getByPid(linkedPid).getName()).append("\n");
            res.append("Level: ").append(level).append("\n");
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

    public byte[] getStats() {
        return stats;
    }

    public void setStats(byte[] stats) {
        this.stats = stats;
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
