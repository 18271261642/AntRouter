package com.jkcq.antrouter.utils;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    /**获取一个字节的低七位,再转换为byte*/
    public static byte getByteOfLow7Bit(byte b){
        //将字节拆分成八位二进制字符串
        String bitArray = byteToBit(b);
        /*截取前七位*/
        bitArray = bitArray.substring(0, bitArray.length()-1);
        /*第一位补零*/
        bitArray = "0"+bitArray;
        /*重新将新的八位二进制字符串转为byte字节*/
        return decodeBinaryString(bitArray);
    }

    /**
     * 把byte转为字符串的bit
     */
    public static String byteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * 二进制字符串转byte
     */
    public static byte decodeBinaryString(String byteStr) {
        int re, len;
        if (null == byteStr) {
            return 0;
        }
        len = byteStr.length();
        if (len != 4 && len != 8) {
            return 0;
        }
        if (len == 8) {// 8 bit处理
            if (byteStr.charAt(0) == '0') {// 正数
                re = Integer.parseInt(byteStr, 2);
            } else {// 负数
                re = Integer.parseInt(byteStr, 2) - 256;
            }
        } else {// 4 bit处理
            re = Integer.parseInt(byteStr, 2);
        }
        return (byte) re;
    }

    /**
     * 注释：字节数组到short的转换！
     *
     * @param b
     * @return
     */
    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[1] & 0xff);// 最低位
        short s1 = (short) (b[0] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }

    private static DecimalFormat df2  = new DecimalFormat("0.0");
    /**解析放大十倍的数据*/
    public static String parseBig10Data(byte high, byte low){
        short s = byteToShort(new byte[]{high, low});
        if(high<0){//高位小于0, 表示最高位为1
//            这是笨方法,把最高位1改为0再转换
//            String str = byteToBit(high)+byteToBit(low);
//            str = str.substring(1, str.length());
//            str = "0"+str;
//            int i = Integer.parseInt(str, 2);
//            double d = i/10f;
//            return "-"+df2.format(d);
            /**
             * 这个地方为什么&0x7fff呢, 因为0x7fff转换为二进制为
             * 0111111111111111
             * 如果high为0x80  low为 0x1C 转换为二进制为
             * 1000000000011100
             * 根据按位与计算规则   0x7fff最高位为0 跟变量的最高位做运算 结果一定为 0
             * 而后面的15位数据则不会发生变化
             * */
            int i = s & 0x7fff;
            double d = i/10f;
            return "-"+df2.format(d);
        }
        double d = s/10f;
        return df2.format(d);
    }

    private static DecimalFormat df1  = new DecimalFormat("0.00");
    /**解析放大百倍的数据*/
    public static String parseBig100Data(byte high, byte low){
        short s = byteToShort(new byte[]{high, low});
        double d = s/100f;
        return df1.format(d);
    }

    public static boolean isNull(String str){
        return str == null || str.length()==0 || str.equalsIgnoreCase("null");
    }

    /**
     * バイト配列を16進数の文字列に変換する。
     *
     * @param bytes
     *            バイト配列
     * @return 将byte数组转换成十六进制字符串
     */
    public static String asHex(byte bytes[]) {
        if ((bytes == null) || (bytes.length == 0)) {
            return "";
        }
        // バイト配列の２倍の長さの文字列バッファを生成。
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        // バイト配列の要素数分、処理を繰り返す。
        for (int index = 0; index < bytes.length; index++) {
            // バイト値を自然数に変換。
            int bt = bytes[index] & 0xff;
            // バイト値が0x10以下か判定。
            if (bt < 0x10) {
                // 0x10以下の場合、文字列バッファに0を追加。
                sb.append("0");
            }
            // バイト値を16進数の文字列に変換して、文字列バッファに追加。
            sb.append(Integer.toHexString(bt).toUpperCase());
        }
        /// 16進数の文字列を返す。
        return sb.toString();
    }



    private  static StringBuffer stringBuffer = new StringBuffer();

    /**
     * 格式化hex数据
     * @param array
     * @return
     */
    public static String formatHex(byte[] array){
        stringBuffer.delete(0,stringBuffer.length());

        for(int i = 0;i<array.length;i++){
            stringBuffer.append(String.format("%02x",array[i]));
        }
        return stringBuffer.toString();
    }


    /** 添加数据包到数据缓存
     * @param receiveDataCache 当前缓存的数据
     * @param buffer 正在接收到的数据
     * @param length 正在接收数据的长度
     * */
    public static byte[] addData(byte[] receiveDataCache, byte[] buffer, int length){
        if(length>0){
            if(receiveDataCache == null){
                /* 把接受到的数据放到缓存数据中 */
                receiveDataCache = new byte[length];
                System.arraycopy(buffer, 0, receiveDataCache, 0, length);
            }else{
                /*把缓存数据和新接收的数据一起放到cache中,再把cache赋值给缓存数据*/
                byte[] cache = new byte[length+receiveDataCache.length];
                System.arraycopy(receiveDataCache, 0, cache, 0, receiveDataCache.length);
                System.arraycopy(buffer, 0, cache, receiveDataCache.length, length);
                receiveDataCache = cache;
            }
        }
        return receiveDataCache;
    }

    //检测IP是否合法
    public static boolean checkAddress(String s) {
        Pattern r = Pattern.compile("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
        Matcher matcher = r.matcher(s);
        return matcher.matches();
    }

    //检测端口是否合法
    public static boolean checkPort(String s) {
//        Pattern r = Pattern.compile("^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)");
        Pattern r = Pattern.compile("6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5]");
        Matcher matcher = r.matcher(s);
        return  matcher.matches();
    }

}
