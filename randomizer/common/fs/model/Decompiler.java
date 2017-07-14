package randomizer.common.fs.model;

import randomizer.common.fs.data.ScriptSingleton;
import randomizer.common.utils.ByteUtils;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Decompiler {

	private int ref;
	private List<String> result;
	private byte[] bytes;
	private String fileName;

	public Decompiler() {}

	public String decompile(Path path) throws Exception {
	    this.fileName = path.toFile().getName();
		List<Integer> headers = new ArrayList<>();
		result = new ArrayList<>();
		bytes = Files.readAllBytes(path);
		int pointerOffset = ByteUtils.toInt(bytes, 0x1C);
		ref = ByteUtils.toInt(bytes, 0x20);

		int count = 0;
		int temp;
		while((temp = ByteUtils.toInt(bytes, pointerOffset + count * 4)) != 0) {
			headers.add(temp);
			count++;
		}

		for(int x = 0; x < headers.size(); x++) {
			int i = headers.get(x);
			int evtOffset = ByteUtils.toInt(bytes, i + 4);
			int type = ByteUtils.toInt(bytes, i + 8);
			int unknown = ByteUtils.toInt(bytes, i + 0x10);
			int subOffset = ByteUtils.toInt(bytes, i + 0x14);
			int evtEnd;
			if(x != headers.size() - 1)
				evtEnd = headers.get(x + 1);
			else
				evtEnd = ref;
			boolean unknownCheck = (unknown == 0 && subOffset == 0);
			boolean hasSub = (evtOffset != subOffset) && (!unknownCheck);
			byte[] evtBytes = Arrays.copyOfRange(bytes, evtOffset, evtEnd);

			// Cut off the end signifier.
			int endIndex = -1;
			for(int y = evtBytes.length - 1; y > -1; y--) {
				if(evtBytes[y] == 0x54) {
					endIndex = y;
					break;
				}
			}
			byte[] trimmedEvtBytes = new byte[endIndex];
			System.arraycopy(evtBytes, 0, trimmedEvtBytes, 0, endIndex);

			result.add("Event(0x" + Long.toHexString(type) + "," +  unknownCheck + ")");

			// Decompile subheader...
			if(hasSub) {
				result.add("Subheader");
				if(unknown != 0)
				    result.add("routine(\"" + ByteUtils.getString(bytes, i + 0x18) + "\")");
				else
				    decSubheader(Arrays.copyOfRange(bytes, subOffset, evtOffset), type);
				result.add("end");
			}

			// Decompile event...
			EventDecompiler evtDecompiler = new EventDecompiler();
			int line = result.size() + headers.indexOf(i) + 1;
			//System.out.println("Decompiling event " + x + " at " + Long.toHexString(i));
			result.addAll(evtDecompiler.parseEventString(trimmedEvtBytes, this, line));
			result.add("end\n");
		}
		headers.clear();

        StringBuilder res = new StringBuilder();
        for(String s : result)
            res.append(s).append(System.lineSeparator());
        return res.toString();
	}

	private void decSubheader(byte[] subBytes, int evType) throws Exception {
		if(ScriptSingleton.getInstance().getSubheaders().containsKey(evType)) {
			Byte[] vals = ScriptSingleton.getInstance().getSubheaders().get(evType);
			for(int x = 0; x < vals.length; x++) {
				int val = (int) Integer.toUnsignedLong(ByteUtils.toInt(subBytes, x * 4));
				if(vals[x] == 0) {
					result.add("int(0x" + Integer.toHexString(val) + ")");
				}
				else {
					String res = ByteUtils.getString(bytes, val + ref);
					if(res.equals(""))
						res = "null";
					result.add("string(\"" + res + "\")");
				}
			}
			return;
		}
		for(int x = 0; x < subBytes.length / 4; x++) {
			int val = (int) Integer.toUnsignedLong(ByteUtils.toInt(subBytes, x * 4));
			if(isPointer(bytes, val)) {
				String res = ByteUtils.getString(bytes, val + ref);
				if(res.equals(""))
					res = "null";
				result.add("string(\"" + res + "\")");
			}
			else {
				result.add("int(0x" + Integer.toHexString(val) + ")");
			}
		}
	}

	private boolean isPointer(byte[] bytes, int offset) {
        return offset >= 0 && offset + ref < bytes.length && offset != 0 && bytes[offset + ref - 1] == 0;
    }

	String parseString(int offset) {
        try {
            return ByteUtils.getString(bytes, ref + offset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getFileName() {
        return fileName;
    }
}