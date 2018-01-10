package com.tumbleweed.io.util;


public class OmpressData {

	//private static OmpressData singleton = new OmpressData();

	private OmpressData() {
	}

	/**
	* @Deprecated
	* 推荐使用newInstance()
	**/
	public static OmpressData getInstance() {
		return new OmpressData();
	}
	
	public static OmpressData newInstance() {
        return new OmpressData();
    }

	private static final int MAX_BUFFER_SIZE = 8192;

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	// assisted buffer
	private short[] dataBuf = new short[4200];

	private long[] charSym = new long[320];
	private long[] symChar = new long[320];

	private long[] symFreq = new long[320];
	private long[] symCum = new long[320];
	private long[] posCum = new long[4100];

	private long matchPos;
	private long matchLen;
	private long[] lNode = new long[4100];
	private long[] rNode = new long[4360];
	private long[] fNode = new long[4100];

	public static char[] encodeHex(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		int i = 0;
		for (int j = 0; i < l; ++i) {
			out[(j++)] = DIGITS[((0xF0 & data[i]) >>> 4)];
			out[(j++)] = DIGITS[(0xF & data[i])];
		}
		return out;
	}

	// assisted method
	private long getSym(long x) {
		long i, j, k;

		i = 1;
		j = 314;
		while (i < j) {
			k = (i + j) / 2;
			if (symCum[(int) k] > x)
				i = k + 1;
			else
				j = k;
		}
		return i;
	}

	private long getPos(long x) {
		long i, j, k;

		i = 1;
		j = 4096;
		while (i < j) {
			k = (i + j) / 2;
			if (posCum[(int) k] > x)
				i = k + 1;
			else
				j = k;
		}
		return (i - 1);
	}

	private void addNode(long n) {
		long i, j, temp;
		int key;
		long cmp;

		cmp = 1;
		key = (int) n;
		j = 4097 + dataBuf[key];
		rNode[(int) n] = lNode[(int) n] = 4096;
		matchLen = 0;

		while (true) {
			if (cmp >= 0) {
				if (rNode[(int) j] != 4096)
					j = rNode[(int) j];
				else {
					rNode[(int) j] = n;
					fNode[(int) n] = j;
					return;
				}
			} else {
				if (lNode[(int) j] != 4096)
					j = lNode[(int) j];
				else {
					lNode[(int) j] = n;
					fNode[(int) n] = j;
					return;
				}
			}
			for (i = 1; i < 60; i++)
				if ((cmp = dataBuf[(int) (key + i)] - dataBuf[(int) (j + i)]) != 0)
					break;
			if (i > 2) {
				if (i > matchLen) {
					matchPos = (n - j) & 4095;
					if ((matchLen = i) >= 60)
						break;
				} else if (i == matchLen) {
					if ((temp = (n - j) & 4095) < matchPos)
						matchPos = temp;
				}
			}
		}
		fNode[(int) n] = fNode[(int) j];
		lNode[(int) n] = lNode[(int) j];
		rNode[(int) n] = rNode[(int) j];
		fNode[(int) lNode[(int) j]] = n;
		fNode[(int) rNode[(int) j]] = n;
		if (rNode[(int) fNode[(int) j]] == j)
			rNode[(int) fNode[(int) j]] = n;
		else
			lNode[(int) fNode[(int) j]] = n;
		fNode[(int) j] = 4096;
	}

	private void delNode(long n) {
		long i;

		if (fNode[(int) n] == 4096)
			return;
		if (rNode[(int) n] == 4096)
			i = lNode[(int) n];
		else if (lNode[(int) n] == 4096)
			i = rNode[(int) n];
		else {
			i = lNode[(int) n];
			if (rNode[(int) i] != 4096) {
				do {
					i = rNode[(int) i];
				} while (rNode[(int) i] != 4096);
				rNode[(int) fNode[(int) i]] = lNode[(int) i];
				fNode[(int) lNode[(int) i]] = fNode[(int) i];
				lNode[(int) i] = lNode[(int) n];
				fNode[(int) lNode[(int) n]] = i;
			}
			rNode[(int) i] = rNode[(int) n];
			fNode[(int) rNode[(int) n]] = i;
		}
		fNode[(int) i] = fNode[(int) n];
		if (rNode[(int) fNode[(int) n]] == n)
			rNode[(int) fNode[(int) n]] = i;
		else
			lNode[(int) fNode[(int) n]] = i;
		fNode[(int) n] = 4096;
	}

	private void updateNode(long sym) {
		long i, j, k, ch;

		if (symCum[0] >= 0x7FFF) {
			j = 0;
			for (i = 314; i > 0; i--) {
				symCum[(int) i] = j;
				j += (symFreq[(int) i] = (symFreq[(int) i] + 1) >> 1);
			}
			symCum[0] = j;
		}
		for (i = sym; symFreq[(int) i] == symFreq[(int) (i - 1)]; i--)
			continue;
		if (i < sym) {
			k = symChar[(int) i];
			ch = symChar[(int) sym];
			symChar[(int) i] = ch;
			symChar[(int) sym] = k;
			charSym[(int) k] = sym;
			charSym[(int) ch] = i;
		}
		symFreq[(int) i]++;
		while (--i > 0)
			symCum[(int) i]++;
		symCum[0]++;
	}

	private static short[] bytesToShorts(byte[] byteArray) {
		short[] shortArray = new short[byteArray.length];

		for (int i = 0; i < shortArray.length; i++) {
			shortArray[i] = byteArray[i];
			shortArray[i] &= 0x00FF; // get rid of signed!!!
		}

		return shortArray;
	}

	private static byte[] shortsTobytes(short[] shortArray) {
		byte[] byteArray = new byte[shortArray.length];

		for (int i = 0; i < byteArray.length; i++) {
			byteArray[i] = (byte) shortArray[i];
		}

		return byteArray;
	}

	public byte[] deCompressData(byte[] byteSrcbuf,
			int srclen) throws Exception {
		short ch, mask;
		int srcend, dstend, sp, dp;
		long i, r, j, k, c, low, high, value, range, sym;

		ch = 0;
		mask = 0;
		low = 0;
		value = 0;
		high = 0x20000;
		sp = 0;
		dp = 0;

		short[] srcbuf = bytesToShorts(byteSrcbuf);

		i = srcbuf[sp++];
		i <<= 8;
		i += srcbuf[sp++];
		i <<= 8;
		i += srcbuf[sp++];
		i <<= 8;
		i += srcbuf[sp++];
		if (i > MAX_BUFFER_SIZE)
			throw new Exception("Decompress Error!");

		short[] dstbuf = new short[(int) i];

		srcend = srclen;
		dstend = (int) i;

		for (i = 0; i < 15 + 2; i++) {
			value *= 2;
			if ((mask >>= 1) == 0) {
				ch = (sp >= srcend) ? 0 : srcbuf[sp++];
				mask = 128;
			}
			value += ((ch & mask) != 0) ? 1 : 0;
		}

		symCum[314] = 0;

		for (k = 314; k >= 1; k--) {
			j = k - 1;
			charSym[(int) j] = k;
			symChar[(int) k] = j;
			symFreq[(int) k] = 1;
			symCum[(int) (k - 1)] = symCum[(int) k] + symFreq[(int) k];
		}

		symFreq[0] = 0;
		posCum[4096] = 0;
		for (i = 4096; i >= 1; i--)
			posCum[(int) (i - 1)] = posCum[(int) i] + 10000 / (i + 200);

		for (i = 0; i < 4096 - 60; i++)
			dataBuf[(int) i] = ' ';

		r = 4096 - 60;

		while (dp < dstend) {
			range = high - low;
			sym = getSym((((value - low + 1) * symCum[0] - 1) / range));
			high = low + (range * symCum[(int) (sym - 1)]) / symCum[0];
			low += (range * symCum[(int) sym]) / symCum[0];
			while (true) {
				if (low >= 0x10000) {
					value -= 0x10000;
					low -= 0x10000;
					high -= 0x10000;
				} else if (low >= 0x8000 && high <= 0x18000) {
					value -= 0x8000;
					low -= 0x8000;
					high -= 0x8000;
				} else if (high > 0x10000)
					break;
				low += low;
				high += high;
				value *= 2;
				if ((mask >>= 1) == 0) {
					ch = (sp >= srcend) ? 0 : srcbuf[sp++];
					mask = 128;
				}
				value += ((ch & mask) != 0) ? 1 : 0;
			}
			c = symChar[(int) sym];
			updateNode(sym);
			if (c < 256) {
				if (dp >= dstend)
					throw new Exception("Decompress Error!");
				dstbuf[(int) dp++] = (short) c;
				dataBuf[(int) r++] = (short) c;
				r &= 4095;
			} else {
				j = c - 255 + 2;
				range = high - low;
				i = getPos((((value - low + 1) * posCum[0] - 1) / range));
				high = low + (range * posCum[(int) i]) / posCum[0];
				low += (range * posCum[(int) (i + 1)]) / posCum[0];
				while (true) {
					if (low >= 0x10000) {
						value -= 0x10000;
						low -= 0x10000;
						high -= 0x10000;
					} else if (low >= 0x8000 && high <= 0x18000) {
						value -= 0x8000;
						low -= 0x8000;
						high -= 0x8000;
					} else if (high > 0x10000)
						break;
					low += low;
					high += high;
					value *= 2;
					if ((mask >>= 1) == 0) {
						ch = (sp >= srcend) ? 0 : srcbuf[sp++];
						mask = 128;
					}
					value += ((ch & mask) != 0) ? 1 : 0;
				}
				i = (r - i - 1) & 4095;
				for (k = 0; k < j; k++) {
					c = dataBuf[(int) ((i + k) & 4095)];
					if (dp >= dstend)
						throw new Exception("Decompress Error!");
					dstbuf[dp++] = (short) c;
					dataBuf[(int) r++] = (short) c;
					r &= 4095;
				}
			}
		}
		return shortsTobytes(dstbuf);
	}

	public byte[] Compress(byte[] byteSrcbuf, int srclen) {
		long i, j, k, s, len, LMatchLen;
		long low, high, written, shifts, sym, range;
		short ch, mask;
		int srcend, sp, dp;

		low = 0;
		shifts = 0;
		written = 4;
		ch = 0;
		mask = 128;
		high = 0x20000;

		srcend = srclen;
		sp = 0;
		dp = 0;

		short[] srcbuf = bytesToShorts(byteSrcbuf);

		short[] dstbuf = new short[MAX_BUFFER_SIZE];

		dstbuf[dp++] = (short) (srclen >> 24);
		dstbuf[dp++] = (short) (srclen >> 16);
		dstbuf[dp++] = (short) (srclen >> 8);
		dstbuf[dp++] = (short) (srclen);

		symCum[314] = 0;
		for (k = 314; k >= 1; k--) {
			j = k - 1;
			charSym[(int) j] = k;
			symChar[(int) k] = j;
			symFreq[(int) k] = 1;
			symCum[(int) (k - 1)] = symCum[(int) k] + symFreq[(int) k];
		}
		symFreq[0] = 0;
		posCum[4096] = 0;
		for (i = 4096; i >= 1; i--) {
			posCum[(int) (i - 1)] = posCum[(int) i] + 10000 / (i + 200);
		}
		for (i = 4096 + 1; i <= 4096 + 256; i++)
			rNode[(int) i] = 4096;
		for (i = 0; i < 4096; i++)
			fNode[(int) i] = 4096;

		s = 0;
		k = 4096 - 60;
		for (i = s; i < k; i++)
			dataBuf[(int) i] = ' ';
		for (len = 0; (len < 60) && (sp < srcend); len++) {
			dataBuf[(int) (k + len)] = srcbuf[sp++];
		}
		for (i = 1; i <= 60; i++)
			addNode(k - i);
		addNode(k);
		do {
			if (matchLen > len)
				matchLen = len;
			if (matchLen <= 2) {
				matchLen = 1;
				sym = charSym[dataBuf[(int) k]];
				range = high - low;
				high = low + (range * symCum[(int) (sym - 1)]) / symCum[0];
				low += (range * symCum[(int) sym]) / symCum[0];
				while (true) {
					if (high <= 0x10000) {
						if ((mask >>= 1) == 0) {
							dstbuf[dp++] = ch;
							ch = 0;
							mask = 128;
							written++;
						}
						for (; shifts > 0; shifts--) {
							ch |= mask;
							if ((mask >>= 1) == 0) {
								dstbuf[dp++] = ch;
								ch = 0;
								mask = 128;
								written++;
							}
						}
					} else if (low >= 0x10000) {
						ch |= mask;
						if ((mask >>= 1) == 0) {
							dstbuf[dp++] = ch;
							ch = 0;
							mask = 128;
							written++;
						}
						for (; shifts > 0; shifts--) {
							if ((mask >>= 1) == 0) {
								dstbuf[dp++] = ch;
								ch = 0;
								mask = 128;
								written++;
							}
						}
						low -= 0x10000;
						high -= 0x10000;
					} else if (low >= 0x8000 && high <= 0x18000) {
						shifts++;
						low -= 0x8000;
						high -= 0x8000;
					} else
						break;
					low += low;
					high += high;
				}
				updateNode(sym);
			} else {
				sym = charSym[(int) (255 - 2 + matchLen)];
				range = high - low;
				high = low + (range * symCum[(int) (sym - 1)]) / symCum[0];
				low += (range * symCum[(int) sym]) / symCum[0];
				while (true) {
					if (high <= 0x10000) {
						if ((mask >>= 1) == 0) {
							dstbuf[dp++] = ch;
							ch = 0;
							mask = 128;
							written++;
						}
						for (; shifts > 0; shifts--) {
							ch |= mask;
							if ((mask >>= 1) == 0) {
								dstbuf[dp++] = ch;
								ch = 0;
								mask = 128;
								written++;
							}
						}
					} else if (low >= 0x10000) {
						ch |= mask;
						if ((mask >>= 1) == 0) {
							dstbuf[dp++] = ch;
							ch = 0;
							mask = 128;
							written++;
						}
						for (; shifts > 0; shifts--) {
							if ((mask >>= 1) == 0) {
								dstbuf[dp++] = ch;
								ch = 0;
								mask = 128;
								written++;
							}
						}
						low -= 0x10000;
						high -= 0x10000;
					} else if (low >= 0x8000 && high <= 0x18000) {
						shifts++;
						low -= 0x8000;
						high -= 0x8000;
					} else
						break;
					low += low;
					high += high;
				}
				updateNode(sym);
				range = high - low;
				high = low + (range * posCum[(int) (matchPos - 1)]) / posCum[0];
				low += (range * posCum[(int) matchPos]) / posCum[0];
				while (true) {
					if (high <= 0x10000) {
						if ((mask >>= 1) == 0) {
							dstbuf[dp++] = ch;
							ch = 0;
							mask = 128;
							written++;
						}
						for (; shifts > 0; shifts--) {
							ch |= mask;
							if ((mask >>= 1) == 0) {
								dstbuf[dp++] = ch;
								ch = 0;
								mask = 128;
								written++;
							}
						}
					} else {
						if (low >= 0x10000) {
							ch |= mask;
							if ((mask >>= 1) == 0) {
								dstbuf[dp++] = ch;
								ch = 0;
								mask = 128;
								written++;
							}
							for (; shifts > 0; shifts--) {
								if ((mask >>= 1) == 0) {
									dstbuf[dp++] = ch;
									ch = 0;
									mask = 128;
									written++;
								}
							}
							low -= 0x10000;
							high -= 0x10000;
						} else {
							if ((low >= 0x8000) && (high <= 0x18000)) {
								shifts++;
								low -= 0x8000;
								high -= 0x8000;
							} else {
								break;
							}
						}
					}
					low += low;
					high += high;
				}
			}
			LMatchLen = matchLen;
			for (i = 0; (i < LMatchLen) && (sp < srcend); i++) {
				j = srcbuf[sp++];
				delNode(s);
				dataBuf[(int) s] = (short) j;
				if (s < 60 - 1)
					dataBuf[(int) (s + 4096)] = (short) j;
				s = (s + 1) & 4095;
				k = (k + 1) & 4095;
				addNode(k);
			}
			while (i++ < LMatchLen) {
				delNode(s);
				s = (s + 1) & 4095;
				k = (k + 1) & 4095;
				if (--len != 0)
					addNode(k);
			}
		} while (len > 0);
		shifts++;
		if (low < 0x8000) {
			if ((mask >>= 1) == 0) {
				dstbuf[dp++] = ch;
				ch = 0;
				mask = 128;
				written++;
			}
			for (; shifts > 0; shifts--) {
				ch |= mask;
				if ((mask >>= 1) == 0) {
					dstbuf[dp++] = ch;
					ch = 0;
					mask = 128;
					written++;
				}
			}
		} else {
			ch |= mask;
			if ((mask >>= 1) == 0) {
				dstbuf[dp++] = ch;
				ch = 0;
				mask = 128;
				written++;
			}
			for (; shifts > 0; shifts--) {
				if ((mask >>= 1) == 0) {
					dstbuf[dp++] = ch;
					ch = 0;
					mask = 128;
					written++;
				}
			}
		}
		for (i = 0; i < 7; i++) {
			if ((mask >>= 1) == 0) {
				dstbuf[dp++] = ch;
				ch = 0;
				mask = 128;
				written++;
			}
		}

		return shortsTobytes(copyOf(dstbuf, (int) written));
	}

	public static short[] copyOf(short[] original, int newLength) {
		short[] copy = new short[newLength];
		System.arraycopy(original, 0, copy, 0,
				Math.min(original.length, newLength));
		return copy;
	}

	public static void main(String args[]) throws Exception {
		String ss = "000000000000005640020224491202100006376221286640799594D491200043300000000000010499622128173F797694D15615600000000000000000000000000000491200000000000000D000000000000D0000000000F4AF627A3837353531353633383030323232323533313130303335313536C37D803D9989B9B4260000000000000000142200029500050016323139322E3030392E3139302E323133011034303030303030303030322020202020312E35302020202020203336323934413039";

		byte[] src = hexStringToBytes(ss);
		byte[] aa = newInstance().Compress(src, src.length);
		System.out.println(encodeHex(aa));

		byte[] bb = newInstance().deCompressData(src, src.length);
		System.out.println(encodeHex(bb));
	}

	public static byte[] hexStringToBytes(String hexStr) {
		int length = hexStr.length();
		if (length % 2 != 0) {
			throw new IllegalArgumentException();
		}
		hexStr = hexStr.toUpperCase();
		byte[] outArray = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			char li = hexStr.charAt(i);
			char lo = hexStr.charAt(i + 1);
			if (li < '0' || li > 'F' || lo < '0' || lo > 'F') {
				throw new IllegalArgumentException();
			}
			outArray[i / 2] = (byte) Integer.parseInt(
					String.valueOf(new char[] { li, lo }), 16);
		}
		return outArray;
	}
}