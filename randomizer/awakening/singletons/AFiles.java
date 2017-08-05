package randomizer.awakening.singletons;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AFiles
{
	private static AFiles instance;

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
    private File characterFile;

	private AFiles() {

	}
	
	public static AFiles getInstance()
	{
		if(instance == null)
			instance = new AFiles();
		return instance;
	}

	public List<File> getOriginalFileList() {
		return originalFileList;
	}

	public void setOriginalFileList(List<File> originalFileList) {
		this.originalFileList = originalFileList;
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

    public Map<String, File> getTerrain() {
        return terrain;
    }

	public File getCharacterFile() {
		return characterFile;
	}

	public void setCharacterFile(File characterFile) {
		this.characterFile = characterFile;
	}
}
