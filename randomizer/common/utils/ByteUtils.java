package randomizer.common.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ByteUtils {
    public static short toShort(byte[] encodedValues, int index) {
        return (short) (((encodedValues[index + 1] & 0xff) << 8) + (encodedValues[index] & 0xff));
    }

    public static int toBigEndianShort(byte[] encodedValues, int index) {
        return (((encodedValues[index] << 8) & 0xFF00) | (encodedValues[index + 1] & 0xFF));
    }

    public static int toInt(byte[] encodedValue, int index) {
        int value = (encodedValue[index + 3] << (Byte.SIZE * 3));
        value |= (encodedValue[index + 2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[index + 1] & 0xFF) << (Byte.SIZE);
        value |= (encodedValue[index] & 0xFF);
        return value;
    }

    public static byte[] toByteArray(short value) {
        byte[] ret = new byte[2];
        ret[0] = (byte) (value & 0xff);
        ret[1] = (byte) ((value >> 8) & 0xff);
        return ret;
    }

    public static List<Byte> toByteList(byte[] arr) {
        List<Byte> bytes = new ArrayList<>();
        for (byte b : arr)
            bytes.add(b);
        return bytes;
    }

    public static byte[] toByteArray(int value) {
        byte[] encodedValue = new byte[Integer.SIZE / Byte.SIZE];
        encodedValue[3] = (byte) (value >> (Byte.SIZE * 3));
        encodedValue[2] = (byte) (value >> (Byte.SIZE * 2));
        encodedValue[1] = (byte) (value >> Byte.SIZE);
        encodedValue[0] = (byte) value;
        return encodedValue;
    }

    public static String getString(byte[] source, int index) throws UnsupportedEncodingException {
        int end = index;
        while (source[end] != 0) {
            end++;
        }
        return new String(Arrays.copyOfRange(source, index, end), "shift-jis");
    }

    public static String toString(byte[] input) {
        StringBuilder builder = new StringBuilder("[");
        for(int x = 0; x < input.length; x++) {
            builder.append("0x").append(Long.toHexString(input[x] & 0xFF));
            if(x != input.length - 1)
                builder.append(", ");
        }
        return builder.append("]").toString();
    }
}
