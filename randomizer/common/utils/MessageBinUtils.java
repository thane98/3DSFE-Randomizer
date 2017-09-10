package randomizer.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MessageBinUtils
{
    /**
     * Extracts the passed bytes as a Fire Emblem message archive and returns
     * their content as an array of Strings.
     *
     * @param archive the bytes to extract
     * @return the content of the passed bytes
     */
    public static String[] extractMessageArchive(byte[] archive) {
        return extractMessageArchive(null, archive, false);
    }

    /**
     * Extracts the passed bytes as a Fire Emblem message archive and returns
     * their content as an array of Strings. Saves the content in the given file.
     *
     * @param outname the file to which output should be written
     * @param archive the bytes to extract
     * @return the content of the passed bytes
     */
    public static String[] extractMessageArchive(String outname, byte[] archive) {
        return extractMessageArchive(outname, archive, true);
    }

    /**
     * Extracts the passed bytes as a Fire Emblem message archive and returns
     * their content as an array of Strings. Can save the content in the given
     * file.
     *
     * @param outname the file to which output should be written
     * @param archive the bytes to extract
     * @param save whether or not to save the output
     * @return the content of the passed bytes
     */
    public static String[] extractMessageArchive(String outname, byte[] archive,
                                                 boolean save) {
        if (archive.length < 0x20) {
            return null;
        }
        try {
            ArrayList<Byte> bytes = new ArrayList<>();
            for (int i = 0x20; i < archive.length; i++) {
                if (archive[i] == 0) {
                    break;
                } else {
                    bytes.add(archive[i]);
                }
            }
            byte[] string = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                string[i] = bytes.get(i);
            }
            String archiveName = new String(string, "Shift_JIS");
            int textPartitionLen = ByteUtils.toInt(archive, 4);
            int stringCount = ByteUtils.toInt(archive, 0xC);
            int stringMetaOffset = 0x20 + textPartitionLen;
            int namesOffset = stringMetaOffset + 0x8 * stringCount;
            if (archive.length < (0x20 + (long) textPartitionLen +
                    (0x8 * ((long) stringCount - 1)) + 4)) {
                return null;
            }
            String[] messageNames = new String[stringCount];
            String[] messages = new String[stringCount];
            for (int i = 0; i < stringCount; i++) {
                int messageOffset = 0x20 + ByteUtils.toInt(archive,
                        stringMetaOffset + 0x8 * i);
                int messageLen = 0;
                while (ByteUtils.toShort(archive, messageOffset + messageLen) != 0) {
                    messageLen += 2;
                }
                byte[] message = new byte[messageLen];
                System.arraycopy(archive, messageOffset + 0, message, 0, messageLen);
                try {
                    messages[i] = new String(message, "UTF-16LE").replace("\n","\\n")
                            .replace("\r","\\r");
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                int nameOffset = namesOffset + ByteUtils.toInt(archive,
                        stringMetaOffset + (0x8 * i) + 4);
                bytes.clear();
                for (int j = nameOffset; j < archive.length; j++) {
                    if (archive[j] == 0) {
                        break;
                    } else {
                        bytes.add(archive[j]);
                    }
                }
                string = new byte[bytes.size()];
                for (int j = 0; j < bytes.size(); j++) {
                    string[j] = bytes.get(j);
                }
                messageNames[i] = new String(string, "Shift_JIS");
            }
            ArrayList<String> lines = new ArrayList<>();
            lines.add(archiveName);
            lines.add(System.getProperty("line.separator"));
            lines.add("Message Name: Message");
            lines.add(System.getProperty("line.separator"));
            for (int i = 0; i < stringCount; i++) {
                lines.add(messageNames[i] + ": " + messages[i]);
            }
            if (save) {
                try {
                    Files.write(Paths.get(outname), lines);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return lines.toArray(new String[lines.size()]);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Creates an array of bytes containing a Fire Emblem message archive with the
     * passed content.
     *
     * @param lines the content of the message archive
     * @return the byte data for the message archive
     */
    public static byte[] makeMessageArchive(String[] lines) {
        try {
            int stringCount = lines.length - 6;
            String[] messages = new String[stringCount];
            String[] names = new String[stringCount];
            int[] mPos = new int[stringCount];
            int[] nPos = new int[stringCount];
            for (int i = 6; i < lines.length; i++) {
                int ind = lines[i].indexOf(": ");
                names[i - 6] = lines[i].substring(0, ind);
                messages[i - 6] = lines[i].substring(ind + 2).replace("\\n", "\n")
                        .replace("\\r", "\r");
            }
            byte[] header = new byte[0x20];
            byte[] stringTable;
            byte[] metaTable = new byte[stringCount * 8];
            byte[] namesTable;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (DataOutputStream dos = new DataOutputStream(baos)) {
                    byte[] bytes = lines[0].getBytes("Shift_JIS");
                    dos.write(bytes, 0, bytes.length);
                    dos.write((byte) 0);
                    while (baos.size() % 4 != 0) {
                        dos.write((byte) 0);
                    }
                    for (int i = 0; i < stringCount; i++) {
                        mPos[i] = baos.size();
                        bytes = messages[i].getBytes("UTF-16LE");
                        dos.write(bytes, 0, bytes.length);
                        dos.write((byte) 0);
                        dos.write((byte) 0);
                        while (baos.size() % 4 != 0) {
                            dos.write((byte) 0);
                        }
                    }
                    stringTable = baos.toByteArray();
                }
            }
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                try (DataOutputStream dos = new DataOutputStream(baos)) {
                    for (int i = 0; i < stringCount; i++) {
                        nPos[i] = baos.size();
                        byte[] bytes = names[i].getBytes("Shift_JIS");
                        dos.write(bytes, 0, bytes.length);
                        dos.write((byte) 0);
                    }
                    namesTable = baos.toByteArray();
                }
            }
            for (int i = 0; i < stringCount; i++) {
                System.arraycopy(DecUtils.getBytes(mPos[i]), 0, metaTable, (i * 8), 4);
                System.arraycopy(DecUtils.getBytes(nPos[i]), 0, metaTable, (i * 8) + 4,
                        4);
            }
            byte[] archive = new byte[header.length + stringTable.length +
                    metaTable.length + namesTable.length];
            System.arraycopy(DecUtils.getBytes(archive.length), 0, header, 0, 4);
            System.arraycopy(DecUtils.getBytes(stringTable.length), 0, header, 4, 4);
            System.arraycopy(DecUtils.getBytes(stringCount), 0, header, 0xC, 4);
            System.arraycopy(header, 0, archive, 0, header.length);
            System.arraycopy(stringTable, 0, archive, header.length,
                    stringTable.length);
            System.arraycopy(metaTable, 0, archive, header.length +
                    stringTable.length, metaTable.length);
            System.arraycopy(namesTable, 0, archive, header.length +
                            stringTable.length + metaTable.length,
                    namesTable.length);
            return archive;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
