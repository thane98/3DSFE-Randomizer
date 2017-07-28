package randomizer.common.structures;

import randomizer.common.enums.ItemType;
import randomizer.common.enums.JobState;

public class Job 
{
	private JobState state;
	private String jid;
	private String name;
	private ItemType itemType;
	private short tiedJob;
	private short id;
	private byte weaponRank;
	private byte gender;
	
	private Skill[] skills;
	
	public Job() {
		
	}
	
	@Override public String toString()
	{
		return name;
	}

	public JobState getState() {
		return state;
	}

	public void setState(JobState state) {
		this.state = state;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public byte getWeaponRank() {
		return weaponRank;
	}

	public void setWeaponRank(byte weaponRank) {
		this.weaponRank = weaponRank;
	}

	public Short getTiedJob() {
		return tiedJob;
	}

	public void setTiedJob(Short tiedJob) {
		this.tiedJob = tiedJob;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType weaponType) {
		this.itemType = weaponType;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(byte gender) {
		this.gender = gender;
	}

	public Skill[] getSkills() {
		return skills;
	}

	public void setSkills(Skill[] skills) {
		this.skills = skills;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
