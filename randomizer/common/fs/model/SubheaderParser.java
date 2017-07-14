package randomizer.common.fs.model;

import randomizer.common.utils.ByteUtils;

import java.util.ArrayList;

class SubheaderParser {
    static ArrayList<Byte> getSubheaderFromText(ScriptCompiler compiler, String[] input) throws Exception {
        ArrayList<Byte> result = new ArrayList<>();
        int x = 1;
        while (!input[x].startsWith("end")) {
            if (input[x].startsWith("int")) {
                String parsed = ScriptUtils.parseSingleParameter(input[x]);
                if (parsed.toLowerCase().equals("ffffffff")) {
                    for (int y = 0; y < 4; y++)
                        result.add((byte) 0xFF);
                } else {
                    int parsedInt = Long.decode(parsed).intValue();
                    byte[] bytes = ByteUtils.toByteArray(parsedInt);
                    for (byte b : bytes)
                        result.add(b);
                }
            } else if (input[x].startsWith("string")) {
                String s = ScriptUtils.parseSingleParameter(input[x]);
                int offset;
                offset = compiler.getLabelOffset(s);
                byte[] bytes = ByteUtils.toByteArray(offset);
                for (byte b : bytes)
                    result.add(b);
            } else if (input[x].startsWith("routine")) {
                String s = ScriptUtils.parseSingleParameter(input[x]);
                byte[] stringBytes = s.getBytes("shift-jis");
                for (byte b : stringBytes)
                    result.add(b);
                result.add((byte) 0);
            }
            x++;
        }
        return result;
    }
}
