package randomizer.common.data;

import java.io.File;
import java.util.List;

public class FatesFileData
{
	private static FatesFileData instance;

	private List<File> originalFileList;

	private boolean birthrightVerified;
	private boolean conquestVerified;
	private boolean revelationVerified;

	public FatesFileData() {

	}
	
	public static FatesFileData getInstance()
	{
		if(instance == null)
			instance = new FatesFileData();
		return instance;
	}

	public List<File> getOriginalFileList() {
		return originalFileList;
	}

	public void setOriginalFileList(List<File> originalFileList) {
		this.originalFileList = originalFileList;
	}

	public boolean isBirthrightVerified() {
		return birthrightVerified;
	}

	public void setBirthrightVerified(boolean birthrightVerified) {
		this.birthrightVerified = birthrightVerified;
	}

	public boolean isConquestVerified() {
		return conquestVerified;
	}

	public void setConquestVerified(boolean conquestVerified) {
		this.conquestVerified = conquestVerified;
	}

	public boolean isRevelationVerified() {
		return revelationVerified;
	}

	public void setRevelationVerified(boolean revelationVerified) {
		this.revelationVerified = revelationVerified;
	}
}
