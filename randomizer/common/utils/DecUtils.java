package randomizer.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

/**
 * Created by Ethan on 3/30/2017.
 */
public class DecUtils
{
    /**
     * Converts an integer into its byte representation.
     *
     * @param i the integer that should be converted to bytes
     * @return the byte representation of the integer
     */
    public static byte[] getBytes(int i) {
        byte[] result = new byte[4];
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            result[0] = (byte) (i);
            result[1] = (byte) (i >> 8);
            result[2] = (byte) (i >> 16);
            result[3] = (byte) (i >> 24);
        } else {
            result[0] = (byte) (i >> 24);
            result[1] = (byte) (i >> 16);
            result[2] = (byte) (i >> 8);
            result[3] = (byte) (i);
        }
        return result;
    }

    /**
     * Reads a file's bytes into an array and returns them.
     *
     * @param source the file from which the bytes should be read
     * @return the file's bytes
     */
    public static byte[] getFileBytes(File source) {
        byte[] bytes;
        try (FileInputStream input = new FileInputStream(source)) {
            bytes = new byte[(int) source.length()];
            input.read(bytes);
        } catch (IOException ex) {
            ex.printStackTrace();
            bytes = new byte[0];
        }
        return bytes;
    }

    /**
     * Reads an unsigned 16-bit integer from the specified index in a byte array.
     *
     * @param bytes the array of bytes from which the integer will be read
     * @param index the index at which the integer is found
     * @return the 16-bit unsigned integer at the specified index
     */
    public static int getUInt16(byte[] bytes, int index) {
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            return ((bytes[index + 1] & 0xff) << 8) + (bytes[index] & 0xff);
        } else {
            return ((bytes[index] & 0xff) << 8) + (bytes[index + 1] & 0xff);
        }
    }

    /**
     * Reads an unsigned 24-bit integer from the specified index in a byte array.
     *
     * @param bytes the array of bytes from which the integer will be read
     * @param index the index at which the integer is found
     * @return the 24-bit unsigned integer at the specified index
     */
    public static int getUInt24(byte[] bytes, int index) {
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            return ((bytes[index + 2] & 0xff) << 16) +
                    ((bytes[index + 1] & 0xff) << 8) + (bytes[index] & 0xff);
        } else {
            return ((bytes[index + 0] & 0xff) << 16) +
                    ((bytes[index + 1] & 0xff) << 8) + (bytes[index + 2] & 0xff);
        }
    }

    /**
     * Reads an unsigned 32-bit integer from the specified index in a byte array.
     *
     * @param bytes the array of bytes from which the integer will be read
     * @param index the index at which the integer is found
     * @return the 32-bit unsigned integer at the specified index
     */
    public static int getUInt32(byte[] bytes, int index) {
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            return ((bytes[index + 3] & 0xff) << 24) +
                    ((bytes[index + 2] & 0xff) << 16) +
                    ((bytes[index + 1] & 0xff) << 8) + (bytes[index] & 0xff);
        } else {
            return ((bytes[index] & 0xff) << 24) +
                    ((bytes[index + 1] & 0xff) << 16) +
                    ((bytes[index + 2] & 0xff) << 8) + (bytes[index + 3] & 0xff);
        }
    }
}
