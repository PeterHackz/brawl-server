package com.brawl.titan;

import java.util.Arrays;

public class PepperCrypto {

    private static final Exception verificationFailed = new Exception("message failed verification");
    private static final byte[] server_public_key = hexStringToByteArray(
            "0C60170E51746626A27683BF1619467A3C8BBCF9785C4899358EF71A3384CD74");
    private Nonce server_nonce;
    private Nonce client_nonce;
    private byte[] client_secret_key;
    private byte[] client_public_key;
    private TweetNacl.Box box;

    public PepperCrypto() {
    }

    // some fancy shit (no)
    // ! do not use this at home
    public static byte[] rand256(int seed1, int seed2) {
        int[] state = {seed1, seed2, seed1 ^ seed2, seed2 ^ seed1};
        byte[] buf = new byte[32];
        for (int i = 0; i < 32; i++) {
            int x = state[0] ^ state[3];
            int y = state[1] ^ state[2];
            int z = (x << 16) | (y >>> 16);
            z ^= state[0] ^ state[1] ^ state[2] ^ state[3];
            z ^= i;
            z ^= (z << 13) | (z >>> 19);
            z ^= (z << 29) | (z >>> 3);
            buf[i] = (byte) (z & 0xff);
            state[0] = y;
            state[1] = state[2];
            state[2] = x;
            state[3] = z;
        }
        return buf;
    }

    public static byte[] concat(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int currentIndex = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentIndex, array.length);
            currentIndex += array.length;
        }
        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public byte[] decrypt(int type, byte[] payload) throws Exception {
        if (type == 10100) {
            return payload;
        } else if (type == 10101) {
            client_public_key = Arrays.copyOfRange(payload, 0, 32);
            Nonce nonce = new Nonce(client_public_key, server_public_key);
            box = new TweetNacl.Box(server_public_key, client_secret_key);
            byte[] decrypted = box.open(Arrays.copyOfRange(payload, 32, payload.length), nonce.bytes());
            if (decrypted == null) {
                throw verificationFailed;
            }
            client_nonce = new Nonce(Arrays.copyOfRange(decrypted, 24, 48));
            /*
             * no need to verify session token
             * since secret key is built from it.
             */
            return Arrays.copyOfRange(decrypted, 48, decrypted.length);
        } else {
            client_nonce.nextNonce();
            byte[] result = box.open_after(payload, client_nonce.bytes());
            if (result == null) {
                throw verificationFailed;
            }
            return result;
        }
    }

    public byte[] encrypt(int type, byte[] payload) {
        if (type == 20100) {
            client_secret_key = rand256(
                    ((payload[4] & 0xFF) ^ (payload[4 + 20] & 0xFF))
                            + (payload[4 + 22] & 0xFF),
                    ((payload[4 + 1] & 0xFF) + (payload[4 + 14] & 0xFF))
                            ^ (payload[4 + 17] & 0xFF));
            return payload;
        } else if (type == 20104 || (type == 20103 && server_nonce == null)) {
            Nonce nonce = new Nonce(client_nonce.bytes(), client_public_key, server_public_key);
            server_nonce = new Nonce();
            byte[] sharedKey = new byte[32];
            TweetNacl.randombytes(sharedKey, 32);
            server_nonce = new Nonce();
            byte[] out = box.after(concat(server_nonce.bytes(), sharedKey, payload), nonce.bytes());
            box = new TweetNacl.Box(sharedKey);
            client_public_key = null;
            client_secret_key = null;
            return out;
        } else {
            server_nonce.nextNonce();
            return box.after(payload, server_nonce.bytes());
        }
    }
}
