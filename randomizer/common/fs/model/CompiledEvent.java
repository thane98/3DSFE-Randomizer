package randomizer.common.fs.model;

import java.util.ArrayList;

public class CompiledEvent {
    private HeaderInformation header;
    private String[] eventText;
    private int offset;
    private int subheaderOffset;
    private int eventOffset;
    private int eventStart;
    private ArrayList<Byte> subheaderBytes = new ArrayList<>();
    private ArrayList<Byte> eventBytes = new ArrayList<>();

    CompiledEvent() {
    }

    void compile(ScriptCompiler compiler, String[] text) throws Exception {
        //Eliminate whitespace first.
        this.eventText = text;
        parseHeaderInformation();
        if(header == null) {
            eventBytes = new ArrayList<>();
            return;
        }
        for (int y = 0; y < eventText.length; y++) {
            eventText[y] = eventText[y].trim();
            if (eventText[y].startsWith("routine"))
                header.setRoutine();
        }

        if (header.hasSubheader())
            subheaderBytes = SubheaderParser.getSubheaderFromText(compiler, eventText);
        String[] onlyEvent = new String[eventText.length - getEventLine()];
        System.arraycopy(eventText, getEventLine(), onlyEvent, 0, eventText.length - getEventLine());
        EventCompiler eventCompiler = new EventCompiler();
        eventBytes = eventCompiler.createEvent(compiler, onlyEvent, getEventLine() + eventStart);
    }

    private int getEventLine() {
        if (!header.hasSubheader())
            return 1;
        else {
            int x = 1;
            while (!eventText[x].equals("end")) {
                x++;
            }
            return x + 1;
        }
    }

    private void parseHeaderInformation() {
        if (eventText[0].startsWith("Event")) {
            header = new HeaderInformation();
            String[] parameters = ScriptUtils.parseEmbeddedParameters(eventText[0]);
            header.setEventType(Integer.decode(parameters[0]));
            header.setHasSubheader(eventText[1].contains("Subheader"));
            header.setUnknownCheck(Boolean.parseBoolean(parameters[1]));
        }
    }

    int getOffset() {
        return offset;
    }

    void setOffset(int offset) {
        this.offset = offset;
    }

    ArrayList<Byte> getSubheaderBytes() {
        return subheaderBytes;
    }

    ArrayList<Byte> getEventBytes() {
        return eventBytes;
    }

    int getSubheaderOffset() {
        return subheaderOffset;
    }

    void setSubheaderOffset(int subheaderOffset) {
        this.subheaderOffset = subheaderOffset;
    }

    int getEventOffset() {
        return eventOffset;
    }

    void setEventOffset(int eventOffset) {
        this.eventOffset = eventOffset;
    }

    void setEventStart(int eventStart) {
        this.eventStart = eventStart;
    }

    HeaderInformation getHeader() {
        return header;
    }
}
