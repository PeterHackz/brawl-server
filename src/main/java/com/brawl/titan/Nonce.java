package com.brawl.titan;

public class Nonce {
    private byte[] nonce;

    public Nonce(byte[] cpk, byte[] spk) {
        Blake2b b2b = new Blake2b(192);
        b2b.update(cpk, 0, 32);
        b2b.update(spk, 0, 32);
        nonce = new byte[24];
        b2b.doFinal(nonce, 0);
    }

    public Nonce(byte[] nonce, byte[] cpk, byte[] spk) {
        Blake2b b2b = new Blake2b(192);
        b2b.update(nonce, 0, 24);
        b2b.update(cpk, 0, 32);
        b2b.update(spk, 0, 32);
        this.nonce = new byte[24];
        b2b.doFinal(this.nonce, 0);
    }

    public Nonce() {
        nonce = new byte[24];
        TweetNacl.randombytes(nonce, 24);
    }

    public Nonce(byte[] nonce) {
        this.nonce = nonce;
    }

    public byte[] bytes() {
        return nonce;
    }

    public void nextNonce() {
        int v8 = 4;
        int v10;
        for (int idx = 0; idx < 24; idx++) {
            v10 = v8 + (int) (nonce[idx] & 0xFF);
            nonce[idx] = (byte) (v10 & 0xFF);
            v8 = v10 / 0x100;
            if (v8 == 0)
                break;
        }
    }
}
