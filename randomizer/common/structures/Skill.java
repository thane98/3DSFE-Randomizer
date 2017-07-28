package randomizer.common.structures;

import randomizer.common.enums.SkillType;

public class Skill 
{
	private String seid;
	private String name;
	private SkillType type;
	private short id;
	
	public Skill() {
		
	}

	@Override
	public String toString()
	{
		return name;
	}
	
	public String getSeid() {
		return seid;
	}

	public void setSeid(String seid) {
		this.seid = seid;
	}

	public SkillType getType() {
		return type;
	}

	public void setType(SkillType type) {
		this.type = type;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
