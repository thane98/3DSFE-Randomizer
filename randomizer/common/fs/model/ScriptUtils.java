package randomizer.common.fs.model;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

class ScriptUtils {
    static String[] parseEventParameters(String input) {
        if (input.contains("("))
            input = input.substring(input.indexOf('(') + 1, input.length() - 1);
        ArrayList<String> preliminaryResults = new ArrayList<>();
        while (input.contains(",")) {
            String nextParam;
            if (input.startsWith("string"))
                nextParam = input.substring(0, input.indexOf("\")") + 1);
            else
                nextParam = input.substring(0, input.indexOf(')') + 1);
            input = input.substring(input.indexOf(",") + 1);
            preliminaryResults.add(nextParam);
        }
        preliminaryResults.add(input);
        return preliminaryResults.toArray(new String[preliminaryResults.size()]);
    }

    static String[] parseEmbeddedParameters(String input) {
        if (input.contains("("))
            input = input.substring(input.indexOf('(') + 1, input.length() - 1);
        String[] split = input.split(",");
        for (int x = 0; x < split.length; x++)
            split[x] = split[x].trim();
        return split;
    }

    static String parseSingleParameter(String input) {
        if (input.startsWith("string") || input.startsWith("routine"))
            return ScriptUtils.parseStringParameter(input);
        if (input.contains("("))
            input = input.substring(input.indexOf('(') + 1, input.length() - 1);
        return input;
    }

    private static String parseStringParameter(String input) {
        if (!(input.startsWith("string") || input.startsWith("routine")))
            return "";
        else {
            input = input.replace("string", "");
            input = input.replace("routine", "");
            input = input.substring(1, input.length() - 1);
            if (input.startsWith("\""))
                input = input.replace("\"", "");
            return input;
        }
    }

    static short shortFromByteArray(byte[] input, int index) {
        byte[] bytes = new byte[2];
        int x = 0;
        while (x < 2) {
            bytes[x] = input[index + x];
            x++;
        }
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getShort();
    }

    static String readCoordBytes(byte[] input, int index) {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < 4; x++) {
            result.append(Long.toHexString(input[index + x] & 0xFF));
            if (x < 3)
                result.append(",");
        }
        return result.toString();
    }

    static byte[] getSearchBytes(String input) throws UnsupportedEncodingException {
        ArrayList<Byte> bytes = new ArrayList<>();
        bytes.add((byte) 0);
        for (Byte b : input.getBytes("shift-jis"))
            bytes.add(b);
        bytes.add((byte) 0);

        byte[] temp = new byte[bytes.size()];
        for (int x = 0; x < bytes.size(); x++)
            temp[x] = bytes.get(x);
        return temp;
    }

    static byte[] byteArrayFromList(List<Byte> input) {
        byte[] output = new byte[input.size()];
        for (int x = 0; x < input.size(); x++)
            output[x] = input.get(x);
        return output;
    }

    static int indexOf(byte[] outerArray, byte[] smallerArray) {
        for (int i = 0; i < outerArray.length - smallerArray.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < smallerArray.length; ++j) {
                if (outerArray[i + j] != smallerArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    static void writeShortToList(ArrayList<Byte> input, short value) {
        byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                .putShort(value).array();
        for (byte b : bytes)
            input.add(b);
    }

    static void writeShortToList(ArrayList<Byte> input, short value, int index) {
        byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                .putShort(value).array();
        for (int x = 0; x < 2; x++)
            input.set(index + x, bytes[x]);
    }
}
