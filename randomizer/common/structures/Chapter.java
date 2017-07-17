package randomizer.common.structures;

import randomizer.common.enums.ChapterType;

public class Chapter {
    private ChapterType type;
    private String cid;
    private String parentPid;
    private String childPid;
    private int id;

    public String getChildPid() {
        return childPid;
    }

    public void setChildPid(String childPid) {
        this.childPid = childPid;
    }

    public String getParentPid() {
        return parentPid;
    }

    public void setParentPid(String parentPid) {
        this.parentPid = parentPid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public ChapterType getType() {
        return type;
    }

    public void setType(ChapterType type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
