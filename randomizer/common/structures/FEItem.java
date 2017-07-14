package randomizer.common.structures;

import randomizer.common.enums.ItemType;

public class FEItem {
    private String iid;
    private String name;
    private ItemType type;

    public FEItem() {

    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
