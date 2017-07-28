package randomizer.common.data;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FatesFileData
{
	private static FatesFileData instance;

	// Used in file verification phase.
	private List<File> originalFileList;
	private File rom;

	// Files mapped by chapter ID.
	private Map<String, File> dispos = new HashMap<>();
	private Map<String, File> person = new HashMap<>();
	private Map<String, File> text = new HashMap<>();
	private Map<String, File> script = new HashMap<>();
	private Map<String, File> terrain = new HashMap<>();

	// Individual files used by the randomizer.
    private File gameData;
    private File gMap;
    private File gameDataText;
    private File castleJoin;
    private File bev;

    // Route verification flags.
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

	public File getRom() {
		return rom;
	}

	public void setRom(File rom) {
		this.rom = rom;
	}

    public Map<String, File> getDispos() {
        return dispos;
    }

    public Map<String, File> getPerson() {
        return person;
    }

    public Map<String, File> getText() {
        return text;
    }

    public void setText(Map<String, File> text) {
        this.text = text;
    }

    public Map<String, File> getScript() {
        return script;
    }

    public File getGameData() {
        return gameData;
    }

    public void setGameData(File gameData) {
        this.gameData = gameData;
    }

    public File getGMap() {
        return gMap;
    }

    public void setGMap(File gMap) {
        this.gMap = gMap;
    }

    public void setGameDataText(File gameDataText) {
        this.gameDataText = gameDataText;
    }

    public File getCastleJoin() {
        return castleJoin;
    }

    public void setCastleJoin(File castleJoin) {
        this.castleJoin = castleJoin;
    }

    public File getBev() {
        return bev;
    }

    public void setBev(File bev) {
        this.bev = bev;
    }

    public Map<String, File> getTerrain() {
        return terrain;
    }
}
