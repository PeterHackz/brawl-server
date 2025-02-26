package com.brawl.logic.datastream;

import java.util.Arrays;

import com.brawl.logic.csv.LogicDataTables;
import com.brawl.logic.data.LogicData;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ByteStream {

    private byte[] buffer;
    private int offset;

    public ByteStream(int size) {
        buffer = new byte[size];
    }

    public ByteStream(byte[] bytes) {
        buffer = bytes;
    }

    public ByteStream(byte[] bytes, int startOffset) {
        this(bytes);
        offset = startOffset;
    }

    private void ensureCapacity(int requiredCapacity) {
        if (requiredCapacity + offset > buffer.length) {
            int newCapacity = Math.max(buffer.length * 2, requiredCapacity + buffer.length);
            byte[] newData = new byte[newCapacity];
            System.arraycopy(buffer, 0, newData, 0, buffer.length);
            buffer = newData;
        }
    }

    public void skip(int s) {
        offset += s;
    }

    public void writeByte(int a1) {
        ensureCapacity(1);
        buffer[offset] = (byte) (a1 & 0xFF);
        offset += 1;
    }

    public int readByte() {
        return buffer[offset++] & 0xFF;
    }

    public void writeBoolean(boolean a1) {
        writeByte(a1 ? 1 : 0);
    }

    public void writeBooleans(boolean... a1) {
        byte bitoffset = 0;
        for (boolean bool : a1) {
            if (bitoffset == 0)
                writeByte(0);
            if (bool)
                buffer[offset - 1] |= (byte) (0x1 << bitoffset);
            bitoffset++;
            bitoffset &= 7;
        }
    }

    public void writeInt(int a1) {
        writeByte(a1 >> 24);
        writeByte(a1 >> 16);
        writeByte(a1 >> 8);
        writeByte(a1);
    }

    public void writeBytes(byte[] bytes) {
        ensureCapacity(bytes.length);
        System.arraycopy(bytes, 0, buffer, offset, bytes.length);
        offset += bytes.length;
    }

    public void writeString(String a1) {
        if (a1 == null) {
            writeString();
            return;
        }
        byte[] bytes = a1.getBytes();
        writeInt(bytes.length);
        if (bytes.length > 0)
            writeBytes(bytes);
    }

    public void writeString() {
        writeInt(-1);
    }

    public void writeStringReference(String a1) {
        if (a1 == null || a1.isEmpty()) {
            writeByte(0);
            writeByte(0);
            writeVInt(0);
            return;
        }
        writeByte(0);
        writeByte(0);
        byte[] bytes = a1.getBytes();
        writeVInt(bytes.length);
        writeBytes(bytes);
    }

    public void writeVInt(int value) {
        int v1, v2;
        v1 = (((value >> 25) & 0x40) | (value & 0x3F));
        v2 = ((value ^ (value >> 31)) >> 6);
        value >>= 6;
        if (v2 == 0) {
            writeByte(v1);
        } else {
            writeByte(v1 | 0x80);
            while (v2 != 0) {
                v2 >>= 7;
                writeByte((value & 0x7F) | (v2 > 0 ? 0x80 : 0));
                value >>= 7;
            }
        }
    }

    public void writeDataReference(int a1, int a2) {
        writeVInt(a1);
        if (a1 != 0)
            writeVInt(a2);
    }

    public void writeDataReference(LogicData data) {
        if (data == null)
            writeVInt(0);
        else {
            writeVInt(data.getClassId());
            writeVInt(data.getDataId());
        }
    }

    public void writeIntArray(int... list) {
        writeVInt(list.length);
        for (int i : list) {
            writeVInt(i);
        }
    }

    public int readInt() {
        return (readByte() << 24 | readByte() << 16 | readByte() << 8 | readByte());
    }

    public byte[] readBytes(int size) {
        offset += size;
        return Arrays.copyOfRange(buffer, offset - size, offset);
    }

    public boolean readBoolean() {
        return readByte() >= 1;
    }

    public String readString() {
        int len = readInt();
        if (len > 300) // wtf could be such a string (ok maybe club messages sooooo)
            // TODO: update length limit when implementing clubs
            return null;
        return len <= 0 ? (len == -1 ? null : "") : new String(readBytes(len));
    }

    public int readVInt() {
        int b = readByte();
        int sign = b >> 6, ret = b & 0x3F, off = 6;
        for (int x = 0; x < 4; x++) {
            if ((b & 0x80) == 0)
                break;
            b = readByte();
            ret |= (b & 0x7F) << off;
            off += 7;
        }
        return (sign & 1) == 0 ? (ret) : (ret | (-1 << off));
    }

    public <E> E readDataReference() {
        int classId = readVInt();
        if (classId == 0)
            return null;
        return LogicDataTables.getDataById(classId, readVInt());
    }

    public void setBytes(byte[] bytes) {
        buffer = bytes;
        offset = bytes.length;
    }

    public byte[] getBuffer() {
        return Arrays.copyOfRange(buffer, 0, offset);
    }
}
