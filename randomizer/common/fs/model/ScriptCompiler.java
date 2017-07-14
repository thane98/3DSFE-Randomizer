package randomizer.common.fs.model;

import randomizer.common.utils.ByteUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ScriptCompiler {
    private ArrayList<CompiledEvent> events = new ArrayList<>();
    private ArrayList<Byte> labels = new ArrayList<>();
    private List<Byte> data;
    private int pointerStart;
    private int lineNumbers = 1;
    private int nullCounter = 0;
    private String fileName;


    public ScriptCompiler(String fileName) {
        data = new ArrayList<>();
        labels.add((byte) 0x0);
        this.fileName = fileName;
    }

    public void compile(Path path, String input) throws Exception {
        String[] unsplitEvents = input.split("\\r?\\n\\r?\\n");
        writeHeader(unsplitEvents.length);
        if(input.equals("")) {
            for(int x = 0; x < 4; x++)
                data.remove(data.size() - 1);
            finish(path);
            return;
        }
        for (String unsplitEvent : unsplitEvents) {
            CompiledEvent e = new CompiledEvent();
            String[] temp = unsplitEvent.split("\\r?\\n");
            e.setEventStart(lineNumbers);
            events.add(e);
            lineNumbers += temp.length + 1;
            e.setOffset(data.size());
            e.compile(this, temp);

            for (int x = 0; x < 0x18; x++)
                data.add((byte) 0);
            e.setSubheaderOffset(data.size());
            data.addAll(e.getSubheaderBytes());
            e.setEventOffset(data.size());
            data.addAll(e.getEventBytes());
            if (events.indexOf(e) != unsplitEvents.length - 1) {
                while (data.size() % 4 != 0)
                    data.add((byte) 0);
            }

            byte[] offsetBytes = ByteUtils.toByteArray(e.getOffset());
            byte[] typeBytes = ByteUtils.toByteArray(e.getHeader().getEventType());
            byte[] evtOffsetBytes = ByteUtils.toByteArray(e.getEventOffset());
            byte[] indexBytes = ByteUtils.toByteArray(events.indexOf(e));
            byte[] subheaderOffsetBytes = ByteUtils.toByteArray(e.getSubheaderOffset());
            for (int x = 0; x < 4; x++) {
                data.set(e.getOffset() + x, offsetBytes[x]);
                data.set(e.getOffset() + 0x4 + x, evtOffsetBytes[x]);
                data.set(e.getOffset() + 0x8 + x, typeBytes[x]);
                data.set(e.getOffset() + 0xC + x, indexBytes[x]);
                if (e.getHeader().isRoutine()) {
                    data.set(e.getOffset() + 0x10 + x, subheaderOffsetBytes[x]);
                } else if (e.getHeader().hasSubheader()) {
                    data.set(e.getOffset() + 0x10 + x, (byte) 0);
                    data.set(e.getOffset() + 0x14 + x, subheaderOffsetBytes[x]);
                } else if (!e.getHeader().unknownCheck()) {
                    data.set(e.getOffset() + 0x14 + x, evtOffsetBytes[x]);
                }
            }
        }
        finish(path);
    }

    private void finish(Path path) throws IOException {
        int reference = data.size();
        labels.remove(0);
        data.addAll(labels);
        for (int x = 0; x < nullCounter; x++)
            data.add((byte) 0);
        byte[] ptrStartBytes = ByteUtils.toByteArray(pointerStart);
        byte[] refBytes = ByteUtils.toByteArray(reference);
        for (int x = 0; x < 4; x++) {
            data.set(0x1C + x, ptrStartBytes[x]);
            data.set(0x20 + x, refBytes[x]);
        }
        for (int x = 0; x < events.size(); x++) {
            byte[] temp = ByteUtils.toByteArray(events.get(x).getOffset());
            for (int y = 0; y < 4; y++) {
                data.set(pointerStart + x * 4 + y, temp[y]);
            }
        }
        Files.write(path, ScriptUtils.byteArrayFromList(data));
    }

    private void writeHeader(int totalEvents) throws Exception {

        byte[] header = {0x63, 0x6D, 0x62, 0x00, 0x19, 0x08, 0x11, 0x20, 0x00, 0x00, 0x00, 0x00, 0x28, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        for (Byte b : header)
            data.add(b);
        for (int x = 0; x < 0xC; x++)
            data.add((byte) 0);
        data.addAll(ByteUtils.toByteList(fileName.getBytes("shift-jis")));
        data.add((byte) 0);
        while (data.size() % 4 != 0)
            data.add((byte) 0);
        pointerStart = data.size();
        for (int x = 0; x < totalEvents + 1; x++) {
            for (int y = 0; y < 4; y++)
                data.add((byte) 0);
        }
    }

    int getLabelOffset(String s) throws UnsupportedEncodingException {
        int insertOffset = labels.size() - 1; // Cut off the leading 0.
        ArrayList<Byte> searchBytes = new ArrayList<>();
        if (!s.equals("null")) {
            byte[] labelBytes = s.getBytes("shift-jis");
            for (byte b : labelBytes)
                searchBytes.add(b);
            searchBytes.add((byte) 0x0);

            int labelOffset = ScriptUtils.indexOf(ScriptUtils.byteArrayFromList(labels), ScriptUtils.getSearchBytes(s));
            if (labelOffset != -1)
                return labelOffset;
            else {
                labels.addAll(searchBytes);
                return insertOffset;
            }
        } else {
            searchBytes.add((byte) 0x0);
            searchBytes.add((byte) 0x0);
            int labelOffset = ScriptUtils.indexOf(ScriptUtils.byteArrayFromList(labels),
                    ScriptUtils.byteArrayFromList(searchBytes));
            if (labelOffset != -1)
                return labelOffset;
            else {
                nullCounter++;
                labels.add((byte) 0x0);
                return insertOffset;
            }
        }
    }
}
