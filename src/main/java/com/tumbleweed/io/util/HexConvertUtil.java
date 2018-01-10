package com.tumbleweed.io.util;



/**
 * 
 * @Title: HexConvertUtil.java
 * @Package: com.lemon.io.utils.j8583
 * @Description: hex tools
 * @author: jiangke
 * @date: 2013-10-19
 * @version V1.0
 */
public class HexConvertUtil {
	private final static String hexBase = "0123456789ABCDEF";
	
	/**
	 * 
	 * @Title: str2HexStr
	 * @Description: str to hex
	 * @param: @param str
	 * @param: @return 每个Byte之间空格分隔
	 * @return: String
	 * @throws
	 */
	public static String str2HexStr(String str) {

		char[] chars = hexBase.toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;
		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 
	 * @Title: hexStr2Str
	 * @Description: hex to str
	 * @param: @param hexStr Byte之间无分隔符
	 * @param: @return
	 * @return: String
	 * @throws
	 */
	public static String hexStr2Str(String hexStr) {
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = hexBase.indexOf(hexs[2 * i]) * 16;
			n += hexBase.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return new String(bytes);
	}
	
	public static byte[] hexStr2byte(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		return bytes;
	}
	
	/**
	 * 
	 * @Title: byte2HexStr
	 * @Description: byte to hex
	 * @param: @param b
	 * @param: @return
	 * @return: String (byte split by black)
	 * @throws
	 */
    public static String byte2HexStr(byte[] b)  
    {  
    	return byte2HexStr(b, false);  
    }
	
    /**
     * 
     * @Title: byte2HexStr
     * @Description: byte to hex
     * @param: @param b
     * @param: @param blank
     * @param: @return (byte split by black)
     * @return: String
     * @throws
     */
    public static String byte2HexStr(byte[] b, boolean blank)  
    {  
        String stmp="";  
        StringBuilder sb = new StringBuilder("");  
        for (int n=0;n<b.length;n++)  
        {  
            stmp = Integer.toHexString(b[n] & 0xFF);  
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            if(blank){
	            sb.append(" "); 
            }
        }  
        return sb.toString().toUpperCase().trim();  
    }
	
	public static void main(String[] args) {
//		String str = "Message length error!";
//		System.out.println(str.length());
//		System.out.println(str2HexStr(str));
//		String len = "0021";
//		System.out.println(((len.charAt(0) - 48) << 12) | ((len.charAt(1) - 48) << 8)
//		| ((len.charAt(2) - 48) << 4) | (len.charAt(3) - 48));
		
//		System.out.println(Integer.parseInt("0056",16));
	}
}