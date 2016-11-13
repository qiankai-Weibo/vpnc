package ArrayVPN;

public class MD4 {
	//initial values for MD registers
	private static final int I0 = 0x67452301;
	private static final int I1 = 0xefcdab89;
	private static final int I2 = 0x98badcfe;
	private static final int I3 = 0x10325476;

	//round 2 constant = sqrt(2)
	private static final int C2 = 0x5a827999;
	//round 3 constant = sqrt(3)
	private static final int C3 = 0x6ed9eba1;

	//round 1 shift amounts
	private static final int fs1 = 3;
	private static final int fs2 = 7;
	private static final int fs3 = 11;
	private static final int fs4 = 19;
	//round 2 shift amounts
	private static final int gs1 = 3;
	private static final int gs2 = 5;
	private static final int gs3 = 9;
	private static final int gs4 = 13;
	//round 3 shift amounts
	private static final int hs1 = 3;
	private static final int hs2 = 9;
	private static final int hs3 = 11;
	private static final int hs4 = 15;

	int A, B, C, D; //our "registers"
	int[] buff;
	int numwords;

	private void mdinit(byte[] in) {
		int newlen, endblklen, pad, i;
		long datalenbits;

		datalenbits = in.length * 8;

		endblklen = in.length % 64;
		if (endblklen < 56) {
			pad = 64 - endblklen;
		} else {
			pad = (64 - endblklen) + 64;
		}
		newlen = in.length + pad;

		byte[] b = new byte[newlen];
		for (i = 0; i < in.length; i++) {
			b[i] = in[i];
		}

		b[in.length] = (byte) 0x80;
		for (i = b.length + 1; i < (newlen - 8); i++) {
			b[i] = 0;
		}
		for (i = 0; i < 8; i++) {
			b[newlen - 8 + i] = (byte) (datalenbits & 0xFF);
			datalenbits >>= 8;
		}

		//initialize our starting "registers"
		A = I0;
		B = I1;
		C = I2;
		D = I3;

		numwords = newlen / 4;
		buff = new int[numwords];

		for (i = 0; i < newlen; i += 4) {
			buff[i / 4] = (b[i + 0] & 0xFF) + ((b[i + 1] & 0xFF) << 8)
					+ ((b[i + 2] & 0xFF) << 16) + ((b[i + 3] & 0xFF) << 24);
		}
	}

	public MD4(String s) {
		byte in[] = new byte[s.length()];
		int i;

		for (i = 0; i < s.length(); i++) {
			in[i] = (byte) (s.charAt(i) & 0xFF);
		}

		mdinit(in);
	}

	public MD4(byte[] in) {
		mdinit(in);
	}

	private static int F(int x, int y, int z) {
		return ((x & y) | (~x & z));
	}

	private static int G(int x, int y, int z) {
		return ((x & y) | (x & z) | (y & z));
	}

	private static int H(int x, int y, int z) {
		return (x ^ y ^ z);
	}

	private static int rotintlft(int val, int numbits) {
		return ((val << numbits) | (val >>> (32 - numbits)));
	}

	private void round1(int blk) {
		A = rotintlft((A + F(B, C, D) + buff[0 + 16 * blk]), fs1);
		D = rotintlft((D + F(A, B, C) + buff[1 + 16 * blk]), fs2);
		C = rotintlft((C + F(D, A, B) + buff[2 + 16 * blk]), fs3);
		B = rotintlft((B + F(C, D, A) + buff[3 + 16 * blk]), fs4);

		A = rotintlft((A + F(B, C, D) + buff[4 + 16 * blk]), fs1);
		D = rotintlft((D + F(A, B, C) + buff[5 + 16 * blk]), fs2);
		C = rotintlft((C + F(D, A, B) + buff[6 + 16 * blk]), fs3);
		B = rotintlft((B + F(C, D, A) + buff[7 + 16 * blk]), fs4);

		A = rotintlft((A + F(B, C, D) + buff[8 + 16 * blk]), fs1);
		D = rotintlft((D + F(A, B, C) + buff[9 + 16 * blk]), fs2);
		C = rotintlft((C + F(D, A, B) + buff[10 + 16 * blk]), fs3);
		B = rotintlft((B + F(C, D, A) + buff[11 + 16 * blk]), fs4);

		A = rotintlft((A + F(B, C, D) + buff[12 + 16 * blk]), fs1);
		D = rotintlft((D + F(A, B, C) + buff[13 + 16 * blk]), fs2);
		C = rotintlft((C + F(D, A, B) + buff[14 + 16 * blk]), fs3);
		B = rotintlft((B + F(C, D, A) + buff[15 + 16 * blk]), fs4);
	}

	private void round2(int blk) {
		A = rotintlft((A + G(B, C, D) + buff[0 + 16 * blk] + C2), gs1);
		D = rotintlft((D + G(A, B, C) + buff[4 + 16 * blk] + C2), gs2);
		C = rotintlft((C + G(D, A, B) + buff[8 + 16 * blk] + C2), gs3);
		B = rotintlft((B + G(C, D, A) + buff[12 + 16 * blk] + C2), gs4);

		A = rotintlft((A + G(B, C, D) + buff[1 + 16 * blk] + C2), gs1);
		D = rotintlft((D + G(A, B, C) + buff[5 + 16 * blk] + C2), gs2);
		C = rotintlft((C + G(D, A, B) + buff[9 + 16 * blk] + C2), gs3);
		B = rotintlft((B + G(C, D, A) + buff[13 + 16 * blk] + C2), gs4);

		A = rotintlft((A + G(B, C, D) + buff[2 + 16 * blk] + C2), gs1);
		D = rotintlft((D + G(A, B, C) + buff[6 + 16 * blk] + C2), gs2);
		C = rotintlft((C + G(D, A, B) + buff[10 + 16 * blk] + C2), gs3);
		B = rotintlft((B + G(C, D, A) + buff[14 + 16 * blk] + C2), gs4);

		A = rotintlft((A + G(B, C, D) + buff[3 + 16 * blk] + C2), gs1);
		D = rotintlft((D + G(A, B, C) + buff[7 + 16 * blk] + C2), gs2);
		C = rotintlft((C + G(D, A, B) + buff[11 + 16 * blk] + C2), gs3);
		B = rotintlft((B + G(C, D, A) + buff[15 + 16 * blk] + C2), gs4);
	}

	private void round3(int blk) {
		A = rotintlft((A + H(B, C, D) + buff[0 + 16 * blk] + C3), hs1);
		D = rotintlft((D + H(A, B, C) + buff[8 + 16 * blk] + C3), hs2);
		C = rotintlft((C + H(D, A, B) + buff[4 + 16 * blk] + C3), hs3);
		B = rotintlft((B + H(C, D, A) + buff[12 + 16 * blk] + C3), hs4);

		A = rotintlft((A + H(B, C, D) + buff[2 + 16 * blk] + C3), hs1);
		D = rotintlft((D + H(A, B, C) + buff[10 + 16 * blk] + C3), hs2);
		C = rotintlft((C + H(D, A, B) + buff[6 + 16 * blk] + C3), hs3);
		B = rotintlft((B + H(C, D, A) + buff[14 + 16 * blk] + C3), hs4);

		A = rotintlft((A + H(B, C, D) + buff[1 + 16 * blk] + C3), hs1);
		D = rotintlft((D + H(A, B, C) + buff[9 + 16 * blk] + C3), hs2);
		C = rotintlft((C + H(D, A, B) + buff[5 + 16 * blk] + C3), hs3);
		B = rotintlft((B + H(C, D, A) + buff[13 + 16 * blk] + C3), hs4);

		A = rotintlft((A + H(B, C, D) + buff[3 + 16 * blk] + C3), hs1);
		D = rotintlft((D + H(A, B, C) + buff[11 + 16 * blk] + C3), hs2);
		C = rotintlft((C + H(D, A, B) + buff[7 + 16 * blk] + C3), hs3);
		B = rotintlft((B + H(C, D, A) + buff[15 + 16 * blk] + C3), hs4);
	}

	public void digest() {
		int AA, BB, CC, DD, i;

		for (i = 0; i < numwords / 16; i++) {
			AA = A;
			BB = B;
			CC = C;
			DD = D;

			round1(i);
			round2(i);
			round3(i);

			A += AA;
			B += BB;
			C += CC;
			D += DD;
		}
	}

	public int[] getRegs() {
		int[] regs = { A, B, C, D };

		return regs;
	}

	public byte[] toBytes() {
		byte[] b = new byte[16];
		int[] regs = getRegs();
		int i, j;

		for (i = 0; i < 4; i++) {
			for (j = 0; j < 4; j++) {
				b[i * 4 + j] = (byte) ((regs[i] >>> j * 8) & 0xFF);
			}
		}

		return b;
	}

	public String toString() {
		return (tohex(A) + tohex(B) + tohex(C) + tohex(D));
	}

	private static String tohex(int i) {
		int j;
		String tmpstr = new String("");

		for (j = 0; j < 4; j++) {
			tmpstr += Integer.toString((i >> 4) & 0xF, 16)
					+ Integer.toString(i & 0xF, 16);
			i >>= 8;
		}
		return tmpstr;
	}
} //end MD4 class
