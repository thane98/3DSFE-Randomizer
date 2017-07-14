package randomizer.common.fs.model;

import randomizer.common.fs.data.ScriptSingleton;
import randomizer.common.utils.ByteUtils;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

class EventDecompiler {
    private final byte[] INTEGER_VALS = {0x01, 0x07, 0x19, 0x1A, 0x1B, 0x1F};
    private final byte[] CONDITIONAL_VALS = {0x49, 0x4C, 0x4D, 0x4B};
    private final byte[] STRING_VALS = {0x1C, 0x1D};
    private final byte[] RAW_VALS = {0x50, 0x46, 0x03};
    private final byte EVENT_BYTE = 0x47;

    private ArrayList<String> decompiled = new ArrayList<>();
    private ArrayList<SimpleEntry<String, Integer>> parameters = new ArrayList<>();
    private Hashtable<Integer, Integer> savedPoints = new Hashtable<>();
    private Stack<Integer> conditionalIndexes = new Stack<>();
    private Stack<Integer> conditionalLengths = new Stack<>();
    private Stack<Boolean> specialCaseStack = new Stack<>();
    private int nests = 0;
    private int lineNumber;
    private boolean omitFlag = false;
    private int reductionNumber = -1; // Special case for B027.

    EventDecompiler() {
    }

    ArrayList<String> parseEventString(byte[] input, Decompiler decompiler, int line) {
        lineNumber = line;
        for (int x = 0; x < input.length; x++) {
            SimpleEntry<String, Integer> command = null;
            processConditionals(input, x);
            if (omitFlag) {
                decompiled.add(getWhiteSpace() + "omit");
                omitFlag = false;
            }
            if (input[x] == EVENT_BYTE) {
                command = parseEventCommand(input, x, decompiler);
                parameters.clear();
                savedPoints.put(x, decompiled.size());
                decompiled.add(getWhiteSpace() + command.getKey());
            }
            for (byte b : RAW_VALS) {
                if (command != null)
                    break;
                if (input[x] == b) {
                    command = parseKnownRaw(input, x, decompiler);
                    parameters.add(new SimpleEntry<>(command.getKey(), x));
                    savedPoints.put(x, decompiled.size());
                    break;
                }
            }
            for (byte b : INTEGER_VALS) {
                if (command != null)
                    break;
                if (input[x] == b) {
                    command = parseIntegerValue(input, x);
                    parameters.add(new SimpleEntry<>(command.getKey(), x));
                    savedPoints.put(x, decompiled.size());
                    break;
                }
            }
            for (byte b : STRING_VALS) {
                if (command != null)
                    break;
                if (input[x] == b) {
                    command = parseString(input, x, decompiler);
                    parameters.add(new SimpleEntry<>(command.getKey(), x));
                    savedPoints.put(x, decompiled.size());
                    break;
                }
            }
            for (byte b : CONDITIONAL_VALS) {
                if (command != null)
                    break;
                if (input[x] == b) {
                    if (x + 2 < input.length) {
                        short val = ScriptUtils.shortFromByteArray(input, x + 1);
                        if (val < 0) {
                            dumpParameters();
                            savedPoints.put(x, decompiled.size());
                            command = parseReturn(input, x);
                            decompiled.add(getWhiteSpace() + command.getKey());
                            break;
                        }
                    }
                    dumpParameters();
                    savedPoints.put(x, decompiled.size());
                    command = parseCondition(input, x);
                    decompiled.add(getWhiteSpace() + command.getKey());
                    nests++;
                    break;
                }
            }
            if (command == null) // If no known byte sequence is found, pool unknown bytes into their own parameter.
            {
                command = parseRaw(input, x);
                parameters.add(new SimpleEntry<>(command.getKey(), x));
                savedPoints.put(x, decompiled.size());
            }
            x += command.getValue();
        }

        processConditionals(input, input.length);
        nests = 0;
        conditionalLengths.clear();
        conditionalIndexes.clear();
        savedPoints.clear();
        dumpParameters();
        lineNumber = 0;
        return decompiled;
    }

    private SimpleEntry<String, Integer> parseCondition(byte[] input, int index) {
        String result = "";
        int advance = 2;
        int length = (int) ScriptUtils.shortFromByteArray(input, index + 1);
        if (input[index] == 0x49)
            result += "fail {";
        else if (input[index] == 0x4C)
            result += "pass {";
        else if (input[index] == 0x4D)
            result += "specialCheck {";
        else if (input[index] == 0x4B)
            result += "unknownCheck {";
        SimpleEntry<Boolean, Integer> endData = hasEndConditional(input);
        conditionalLengths.push(length);
        conditionalIndexes.push(index + 1);
        SimpleEntry<Boolean, Integer> newEndData = hasEndConditional(input);
        if (endData.getKey() && newEndData.getKey()) // Special case scenario for A028 and B028.
        {
            if (endData.getValue().intValue() == newEndData.getValue().intValue() && length == 5) {
                conditionalLengths.pop();
                conditionalIndexes.pop();
                conditionalLengths.push(length - 3);
                conditionalIndexes.push(index + 1);
                specialCaseStack.push(true);
                return new SimpleEntry<>(result, advance);
            }
        }
        specialCaseStack.push(false);
        return new SimpleEntry<>(result, advance);
    }

    private SimpleEntry<String, Integer> parseEventCommand(byte[] input, int index, Decompiler decompiler) {
        StringBuilder result = new StringBuilder(decompiler.parseString(ByteUtils.toBigEndianShort(input, index + 1)));
        int advance = 2;
        try {
            if (ScriptSingleton.getInstance().getTags().containsKey(result.toString())) {
                advance += ScriptSingleton.getInstance().getTags().get(result.toString()).length;
                Byte[] tag = ScriptSingleton.getInstance().getTags().get(result.toString());
                for (int x = 0; x < tag.length; x++) {
                    if (input[index + 3 + x] != tag[x])
                        System.out.println("Mismatched tag on " + result.toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Build event line from parsed information.
        result.append("(");
        for (int x = 0; x < parameters.size(); x++) {
            result.append(parameters.get(x).getKey());
            if (x < parameters.size() - 1)
                result.append(",");
        }
        result.append(")");

        return new SimpleEntry<>(result.toString(), advance);
    }

    private static SimpleEntry<String, Integer> parseIntegerValue(byte[] input, int index) {
        String result = "";
        int advance = 0;
        byte command = input[index];

        if (command == 0x01 || command == 0x07 || command == 0x19)
            advance = 1;
        else if (command == 0x1A)
            advance = 2;
        else if (command == 0x1B)
            advance = 4;
        else if (command == 0x1F)
            advance = 4;
        if (command == 0x01)
            result += "call(" + Long.toHexString(input[index + 1] & 0xFF) + ")";
        else if (command == 0x07)
            result += "storein(" + Long.toHexString(input[index + 1] & 0xFF) + ")";
        else if (command == 0x19)
            result += "byte(" + Long.toHexString(input[index + 1] & 0xFF) + ")";
        else if (command == 0x1A)
            result += "short(" + Long.toHexString(ScriptUtils.shortFromByteArray(input, index + 1)) + ")";
        else if (command == 0x1B)
            result += "int(" + Long.toHexString(Integer.toUnsignedLong(ByteUtils.toInt(input, index + 1))) + ")";
        else if (command == 0x1F)
            result += "coord(" + ScriptUtils.readCoordBytes(input, index + 1) + ")";
        return new SimpleEntry<>(result, advance);
    }

    private static SimpleEntry<String, Integer> parseString(byte[] input, int index, Decompiler decompiler) {
        String result = "string(\"";
        int advance = 0;
        byte command = input[index];
        if (command == 0x1C) {
            result += decompiler.parseString(input[index + 1]);
            advance = 1;
        } else if (command == 0x1D) {
            result += decompiler.parseString(ByteUtils.toBigEndianShort(input, index + 1));
            advance += 2;
        }
        result += "\")";
        return new SimpleEntry<>(result, advance);
    }

    private SimpleEntry<String, Integer> parseKnownRaw(byte[] input, int index, Decompiler decompiler) {
        String result = "";
        int advance = 0;
        byte command = input[index];
        if (command == 0x46) {
            if (decompiler.getFileName().equals("Command.cmb")) {
                result += parseRaw(input, index, 3).getKey();
                advance += 2;
            } else {
                result += parseRaw(input, index, 2).getKey();
                advance = 1;
            }
        } else if (command == 0x03) {
            if (input[index + 1] != 0x47 && !(decompiler.getFileName().equals("007.cmb")
                    || decompiler.getFileName().equals("012.cmb"))) {
                result += parseRaw(input, index, 2).getKey();
                advance = 1;
            } else {
                result += parseRaw(input, index, 1).getKey();
                advance = 0;
            }
        } else if (command == 0x50) {
            result += parseRaw(input, index, 2).getKey();
            advance = 1;
        }
        return new SimpleEntry<>(result, advance);
    }

    private SimpleEntry<String, Integer> parseRaw(byte[] input, int index) {
        StringBuilder result = new StringBuilder();
        int advance = 0;
        ArrayList<Byte> raw = new ArrayList<>();
        while (!isKnownByte(input[index + advance])) {
            raw.add(input[index + advance]);
            advance++;
            if (index + advance >= input.length)
                break;
            if (conditionalLengths.size() > 0 && conditionalIndexes.size() > 0) {
                if (index + advance >= conditionalLengths.peek() + conditionalIndexes.peek())
                    break;
            }
        }
        advance--;

        result.append("raw(");
        for (int x = 0; x < raw.size(); x++) {
            result.append(Integer.toHexString(raw.get(x) & 0xFF));
            if (x < raw.size() - 1)
                result.append(",");
            else
                result.append(")");
        }
        return new SimpleEntry<>(result.toString(), advance);
    }

    private SimpleEntry<String, Integer> parseRaw(byte[] input, int index, int length) {
        StringBuilder result = new StringBuilder();
        int advance = length - 1;
        ArrayList<Byte> raw = new ArrayList<>();
        for (int x = 0; x < length; x++)
            raw.add(input[index + x]);
        result.append("raw(");
        for (int x = 0; x < raw.size(); x++) {
            result.append(Integer.toHexString(raw.get(x) & 0xFF));
            if (x < raw.size() - 1)
                result.append(",");
            else
                result.append(")");
        }

        return new SimpleEntry<>(result.toString(), advance);
    }

    private boolean isKnownByte(byte input) {
        boolean isKnownByte = false;
        if (input == EVENT_BYTE)
            return true;
        for (byte b : INTEGER_VALS) {
            if (input == b)
                isKnownByte = true;
        }
        for (byte b : CONDITIONAL_VALS) {
            if (input == b)
                isKnownByte = true;
        }
        for (byte b : STRING_VALS) {
            if (input == b)
                isKnownByte = true;
        }
        for (byte b : RAW_VALS) {
            if (input == b)
                isKnownByte = true;
        }
        return isKnownByte;
    }

    private void dumpParameters() {
        String result;
        for (SimpleEntry<String, Integer> s : parameters) {
            int index = s.getValue();
            savedPoints.replace(index, decompiled.size());
            result = getWhiteSpace() + s.getKey();
            decompiled.add(result);
        }
        parameters.clear();
    }

    private SimpleEntry<String, Integer> parseReturn(byte[] input, int index) {
        String result;
        int advance = 2;
        int returnPoint = (ScriptUtils.shortFromByteArray(input, index + 1) + 3) * -1; // Return markers will always use a negative value.
        if (savedPoints.containsKey(index - returnPoint)) {
            int point = savedPoints.get(index - returnPoint);
            int line = point + lineNumber;

            //Special case fix for A002_OP1 bev file.
            if (input[index - returnPoint] != 0x47 && (decompiled.get(point).startsWith("ev::") || decompiled.get(point).startsWith("bev::"))) {
                return this.parseRaw(input, index, 3);
            }

            result = "goto(" + line + ")";
            return new SimpleEntry<>(result, advance);
        } else {
            return this.parseRaw(input, index, 3);
        }
    }

    private void processConditionals(byte[] input, int currentIndex) {
        if (conditionalLengths.size() > 0 && conditionalIndexes.size() > 0) // If the stack size is 0 we don't need to worry about adding in spaces.
        {
            int currentConditionalLength = conditionalLengths.peek();
            int currentConditionalIndex = conditionalIndexes.peek();
            SimpleEntry<Boolean, Integer> endData = hasEndConditional(input);
            if (endData.getKey() && endData.getValue() == currentIndex) {
                breakNest();
            } else if ((currentConditionalIndex + currentConditionalLength) <= currentIndex) {
                if (currentIndex + 3 < input.length) // Fix for P006.
                {
                    if (input[currentIndex] == 0x49 && input[currentIndex + 0x3] == 0x49) {
                        short value = ScriptUtils.shortFromByteArray(input, currentIndex + 1);
                        if (value > 0)
                            omitFlag = true;
                    }
                }
                breakNest();
                if (conditionalLengths.size() > 0) // Fix for B027 and B028.
                {
                    int nextConditionalLength = conditionalLengths.pop();
                    int nextConditionalIndex = conditionalIndexes.pop();
                    int thirdConditionalLength;
                    int thirdConditionalIndex;
                    if (reductionNumber != -1) {
                        currentConditionalLength += reductionNumber;
                        reductionNumber = -1;
                    }
                    int extraLength = (currentConditionalLength + currentConditionalIndex) - (nextConditionalIndex + nextConditionalLength);
                    if (extraLength > 0) {
                        decompiled.add(getWhiteSpace() + "reduce(" + Long.toHexString(extraLength) + ")");

                        if (conditionalLengths.size() > 0) {
                            thirdConditionalLength = conditionalLengths.peek();
                            thirdConditionalIndex = conditionalIndexes.peek();
                            extraLength = (currentConditionalLength + currentConditionalIndex) - (thirdConditionalIndex + thirdConditionalLength);
                            if (extraLength > 0) {
                                reductionNumber = extraLength;
                            }
                        }
                    }
                    conditionalLengths.push(nextConditionalLength);
                    conditionalIndexes.push(nextConditionalIndex);
                }
                processConditionals(input, currentIndex);
            }
        }
    }

    private SimpleEntry<Boolean, Integer> hasEndConditional(byte[] input) {
        if (conditionalLengths.size() > 0 && conditionalIndexes.size() > 0) {
            int currentConditionalLength = conditionalLengths.peek();
            int currentConditionalIndex = conditionalIndexes.peek();
            int checkIndex = currentConditionalLength + currentConditionalIndex - 3;
            if (checkIndex < input.length) {
                for (byte b : CONDITIONAL_VALS) {
                    if (ScriptUtils.shortFromByteArray(input, checkIndex + 1) < 0)
                        return new SimpleEntry<>(false, checkIndex); // The ending is a goto.
                    if (input[checkIndex] == b)
                        return new SimpleEntry<>(true, checkIndex); // Ends in an else statement.
                }
            }
        }
        return new SimpleEntry<>(false, -1);
    }

    private String getWhiteSpace() {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < nests; x++)
            result.append("    ");
        return result.toString();
    }

    private void breakNest() {
        if (specialCaseStack.peek()) {
            decompiled.add(getWhiteSpace() + "followFailure");
        }
        conditionalLengths.pop();
        conditionalIndexes.pop();
        specialCaseStack.pop();
        dumpParameters();
        nests--;
        decompiled.add(getWhiteSpace() + "}");
    }
}