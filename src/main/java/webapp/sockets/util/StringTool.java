package webapp.sockets.util;

/**
 * Created by Administrator on 2015/7/21 0021.
 */
public class StringTool {

    /**
     * 取子字符串
     * @param oriStr 原字符串
     * @param beginIndex 取子串的起始位置
     * @param len 取子串的长度
     * @return 子字符串
     */
    public static String subString(String oriStr,int beginIndex,int len){
        String str = "";
        int strlen = oriStr.length();
        beginIndex = beginIndex -1;
        if(strlen <= beginIndex){
            System.out.println("out of"+ oriStr +"'s length, please recheck!");
        }else if(strlen <= beginIndex+len){
            str = oriStr.substring(beginIndex);
        }else{
            str = oriStr.substring(beginIndex, beginIndex+len);
        }
        return str;
    }

    /**
     * 左补位
     * @param oriStr 原字符串
     * @param len 目标字符串长度
     * @param alexin 补位字符
     * @return 目标字符串
     */
    public static String padLeft(String oriStr,int len,char alexin){
        String str = "";
        int strlen = oriStr.length();
        if(strlen < len){
            for(int i=0;i<len-strlen;i++){
                str = str+alexin;
            }
        }
        str = str + oriStr;
        return str;
    }

    /**
     * 右补位
     * @param oriStr 原字符串
     * @param len 目标字符串长度
     * @param alexin 补位字符
     * @return 目标字符串
     */
    public static String padRight(String oriStr,int len,char alexin){
        String str = "";
        int strlen = oriStr.length();
        if(strlen < len){
            for(int i=0;i<len-strlen;i++){
                str = str+alexin;
            }
        }
        str = oriStr + str;
        return str;
    }


    /**
     * 反转16进制的字符串 以实现高地位顺序倒序
     * @param hexstr
     * @return
     */
    public static String reversalHexString(String hexstr) {
        String resultstr = "";

        if (hexstr.length() % 2 != 0) {
            return "";
        }

        int length = hexstr.length() / 2;

        for (int i = 0; i < length; i++) {
            int start = (length - i - 1) * 2;
            resultstr += hexstr.substring(start, start + 2);
        }

        return resultstr;
    }
}
