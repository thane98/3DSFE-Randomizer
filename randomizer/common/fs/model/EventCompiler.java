package randomizer.common.fs.model;

import randomizer.common.fs.data.ScriptSingleton;
import randomizer.common.utils.ByteUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Stack;

class EventCompiler {
    private Hashtable<Integer, Integer> savedLines = new Hashtable<>();
    private Stack<SimpleEntry<Integer, Integer>> conditionals = new Stack<>();
    private int lengthReduction = -1;

    EventCompiler() {

    }

    ArrayList<Byte> createEvent(ScriptCompiler compiler, String[] input, int lineNumber) throws Exception {
        ArrayList<Byte> result = new ArrayList<>();
        int x = 0;
        while (!input[x].startsWith("end")) {
            compileLine(result, input, x, compiler, lineNumber);
            x++;
        }
        result.add((byte) 0x54);
        result.add((byte) 0);
        savedLines.clear();
        return result;
    }

    private void compileLine(ArrayList<Byte> event, String[] input, int start, ScriptCompiler compiler, int lineNumber) throws Exception {
        savedLines.put(start, event.size());
        if (input[start].startsWith("pass") || input[start].startsWith("fail") || input[start].startsWith("specialCheck")
                || input[start].startsWith("unknownCheck")) {
            updateConditionals(1);
            compileConditional(event, input, start);
        } else if (input[start].startsWith("raw")) {
            String[] parameters = ScriptUtils.parseEmbeddedParameters(input[start]);
            for (String s : parameters)
                event.add((byte) Long.parseLong(s, 16));
            updateConditionals(parameters.length);
        } else if (input[start].startsWith("byte")) {
            byte value = (byte) Long.parseLong(ScriptUtils.parseSingleParameter(input[start]), 16);
            event.add((byte) 0x19);
            event.add(value);
            updateConditionals(2);
        } else if (input[start].startsWith("short")) {
            short value = (short) Long.parseLong(ScriptUtils.parseSingleParameter(input[start]), 16);
            event.add((byte) 0x1A);
            byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                    .putShort(value).array();
            for (byte b : bytes)
                event.add(b);
            updateConditionals(3);
        } else if(input[start].startsWith("int")) {
            int value = (int) Long.parseLong(ScriptUtils.parseSingleParameter(input[start]), 16);
            event.add((byte) 0x1B);
            byte[] bytes = ByteUtils.toByteArray(value);
            for(byte b : bytes)
                event.add(b);
            updateConditionals(5);
        } else if (input[start].startsWith("string")) {
            event.addAll(compileString(input, start, compiler));
        } else if (input[start].startsWith("storein")) {
            byte value = (byte) Long.parseLong(ScriptUtils.parseSingleParameter(input[start]), 16);
            event.add((byte) 0x7);
            event.add(value);
            updateConditionals(2);
        } else if (input[start].startsWith("call")) {
            byte value = (byte) Long.parseLong(ScriptUtils.parseSingleParameter(input[start]), 16);
            event.add((byte) 0x1);
            event.add(value);
            updateConditionals(2);
        } else if (input[start].startsWith("goto")) {
            compileGoTo(event, input, start, lineNumber);
            updateConditionals(3);
        } else if (input[start].startsWith("followFailure")) { // Special case for A028 Enthusiasm event.

        } else if (input[start].startsWith("omit")) {

        } else if (input[start].startsWith("reduce")) {
            lengthReduction = Integer.parseInt(ScriptUtils.parseSingleParameter(input[start]), 16);
        } else if (input[start].startsWith("}")) {
            compileConditionalEnd(event, input, start);
        } else {
            parseEventLine(event, input, start, compiler);
        }
    }

    private ArrayList<Byte> compileParameters(String[] parameters, ScriptCompiler compiler) throws Exception {
        ArrayList<Byte> output = new ArrayList<>();
        for (int y = 0; y < parameters.length; y++) {
            if (parameters[y].startsWith("string")) {
                output.addAll(compileString(parameters, y, compiler));
            } else if (parameters[y].startsWith("byte")) {
                byte value = (byte) Long.parseLong(ScriptUtils.parseSingleParameter(parameters[y]), 16);
                output.add((byte) 0x19);
                output.add(value);
                updateConditionals(2);
            } else if (parameters[y].startsWith("storein")) {
                byte value = (byte) Long.parseLong(ScriptUtils.parseSingleParameter(parameters[y]), 16);
                output.add((byte) 0x7);
                output.add(value);
                updateConditionals(2);
            } else if (parameters[y].startsWith("call")) {
                byte value = (byte) Long.parseLong(ScriptUtils.parseSingleParameter(parameters[y]), 16);
                output.add((byte) 0x1);
                output.add(value);
                updateConditionals(2);
            } else if (parameters[y].startsWith("raw")) {
                String[] byteStrings = ScriptUtils.parseEmbeddedParameters(parameters[y]);
                for (String s : byteStrings) {
                    output.add((byte) Long.parseLong(s, 16));
                }
                updateConditionals(byteStrings.length);
            } else if (parameters[y].startsWith("short")) {
                short value = (short) Long.parseLong(ScriptUtils.parseSingleParameter(parameters[y]), 16);
                byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                        .putShort(value).array();
                output.add((byte) 0x1A);
                for (byte b : bytes)
                    output.add(b);
                updateConditionals(3);
            } else if(parameters[y].startsWith("int")) {
                int value = (int) Long.parseLong(ScriptUtils.parseSingleParameter(parameters[y]), 16);
                output.add((byte) 0x1B);
                byte[] bytes = ByteUtils.toByteArray(value);
                for(byte b : bytes)
                    output.add(b);
                updateConditionals(5);
            } else if (parameters[y].startsWith("coord")) {
                String[] byteStrings = ScriptUtils.parseEmbeddedParameters(parameters[y]);
                output.add((byte) 0x1F);
                for (String s : byteStrings)
                    output.add((byte) Long.parseLong(s, 16));
                updateConditionals(5);
            }
        }
        return output;
    }

    private ArrayList<Byte> compileString(String[] input, int start, ScriptCompiler compiler) throws Exception {
        String label = ScriptUtils.parseSingleParameter(input[start]);
        ArrayList<Byte> output = new ArrayList<>();
        short offset;
        offset = (short) compiler.getLabelOffset(label);
        if (offset <= 0x7F) {
            output.add((byte) 0x1C);
            output.add((byte) offset);
            updateConditionals(2);
        } else {
            output.add((byte) 0x1D);
            byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                    .putShort(offset).array();
            for (byte b : bytes)
                output.add(b);
            updateConditionals(3);
        }
        return output;
    }

    private void parseEventLine(ArrayList<Byte> event, String[] input, int start, ScriptCompiler compiler) throws Exception {
        String[] parameters = ScriptUtils.parseEventParameters(input[start]);
        event.addAll(compileParameters(parameters, compiler));
        String label = ScriptUtils.parseEmbeddedParameters(input[start].substring(0, input[start].indexOf('(')))[0];
        short offset = (short) compiler.getLabelOffset(label);
        byte[] bytes = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN)
                .putShort(offset).array();
        savedLines.replace(start, event.size());
        event.add((byte) 0x47);
        for (byte b : bytes)
            event.add(b);
        if (ScriptSingleton.getInstance().getTags().containsKey(label)) {
            event.addAll(Arrays.asList(ScriptSingleton.getInstance().getTags().get(label)));
            updateConditionals(ScriptSingleton.getInstance().getTags().get(label).length);
        }
        updateConditionals(3);
    }

    private void compileConditional(ArrayList<Byte> event, String[] input, int start) throws Exception {
        conditionals.push(new SimpleEntry<>(event.size(), 2)); // Minimum condition length is 2, so initialize with that value.
        if (input[start].startsWith("pass"))
            event.add((byte) 0x4C);
        else if (input[start].startsWith("fail"))
            event.add((byte) 0x49);
        else if (input[start].startsWith("specialCheck"))
            event.add((byte) 0x4D);
        else if (input[start].startsWith("unknownCheck"))
            event.add((byte) 0x4B);
        ScriptUtils.writeShortToList(event, (short) 0);
    }

    private void compileConditionalEnd(ArrayList<Byte> event, String[] input, int start) {
        SimpleEntry<Integer, Integer> conditional = conditionals.pop();
        boolean hasEndConditional = false;
        if (input[start + 1].startsWith("fail") || input[start - 1].startsWith("followFailure")) {
            conditional.setValue(conditional.getValue() + 3);
            hasEndConditional = true;
        }
        if (lengthReduction != -1)
            conditional.setValue(conditional.getValue() - lengthReduction);
        ScriptUtils.writeShortToList(event, conditional.getValue().shortValue(), conditional.getKey() + 1);
        if (lengthReduction != -1) {
            conditional.setValue(conditional.getValue() + lengthReduction);
            lengthReduction = -1;
        }

        ArrayList<SimpleEntry<Integer, Integer>> temp = new ArrayList<>();
        for (int x = 0; x < conditionals.size(); x++) {
            SimpleEntry<Integer, Integer> previous = conditionals.pop();
            if (x == 0) // Only the next conditional needs to know the length being added to it. The rest will be updated later.
            {
                if (hasEndConditional)
                    previous.setValue(previous.getValue() + conditional.getValue() - 3);
                else
                    previous.setValue(previous.getValue() + conditional.getValue());
            }
            temp.add(previous);
        }
        for (int x = temp.size() - 1; x >= 0; x--)
            conditionals.push(temp.get(x));
    }

    private void compileGoTo(ArrayList<Byte> event, String[] input, int start, int lineNumber) {
        int targetLine = Integer.parseInt(ScriptUtils.parseSingleParameter(input[start])) //Get the target line relative to the array.
                - lineNumber;
        short targetIndex = (short) (((event.size() + 2) - savedLines.get(targetLine).shortValue() + 1) * -1);
        event.add((byte) 0x49);
        ScriptUtils.writeShortToList(event, targetIndex);
    }

    private void updateConditionals(int length) {
        if (conditionals.size() > 0) {
            SimpleEntry<Integer, Integer> conditional = conditionals.pop();
            conditional.setValue(conditional.getValue() + length);
            conditionals.push(conditional);
        }
    }
}