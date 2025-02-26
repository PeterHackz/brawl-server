package com.brawl.logic.math;

import com.brawl.logic.datastream.ByteStream;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogicBitList {

    private int[] data;

    public LogicBitList(byte[] data) {
        if (data == null) {
            this.data = new int[4];
            return;
        }
        int dataSize = (data.length >> 2) + ((data.length & 0x3) != 0 ? 1 : 0);
        this.data = new int[dataSize];
        for (int i = 0; i < data.length; i++) {
            int index = i >> 2;
            int shift = (i & 0x3) << 3;
            this.data[index] |= (data[i] & 0xFF) << shift;
        }
    }

    public LogicBitList(int[] data) {
        this.data = data;
    }

    public LogicBitList(int size) {
        int dataSize = (size >> 5) + ((size & 0x1F) != 0 ? 1 : 0);
        this.data = new int[dataSize];
    }

    public void setBitValue(int bitIndex, boolean value) {
        int dataIndex = bitIndex >> 5;
        int mask = 1 << (bitIndex & 0x1F);
        if (value)
            this.data[dataIndex] |= mask;
        else
            this.data[dataIndex] &= ~mask;
    }

    public boolean isBitSet(int bitIndex) {
        int dataIndex = bitIndex >> 5;
        int mask = 1 << (bitIndex & 0x1F);
        return (this.data[dataIndex] & mask) != 0;
    }

    public boolean isTrue(int bitIndex) {
        return isBitSet(bitIndex);
    }

    public void setTrue(int bitIndex) {
        setBitValue(bitIndex, true);
    }

    public void unsetBit(int bitIndex) {
        int dataIndex = bitIndex >> 5;
        int mask = 1 << (bitIndex & 0x1F);
        if ((this.data[dataIndex] & mask) != 0)
            this.data[dataIndex] &= ~mask;
    }

    public void encode(ByteStream stream) {
        for (int i : data)
            stream.writeInt(i);
    }

    public int[] getList() {
        return data;
    }

    public byte[] toBytes() {
        byte[] bytes = new byte[data.length * 4];
        for (int i = 0; i < data.length; i++) {
            int a1 = data[i];
            int index = i * 4;
            bytes[index + 3] = (byte) ((a1 >> 24) & 0xFF);
            bytes[index + 2] = (byte) ((a1 >> 16) & 0xFF);
            bytes[index + 1] = (byte) ((a1 >> 8) & 0xFF);
            bytes[index] = (byte) (a1 & 0xFF);
        }
        return bytes;
    }

}
