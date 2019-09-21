package wolfheros.life.home.tools.Main;

import java.nio.charset.Charset;

public class CharToHex {

    public static String stringToHex(String stringQuery){
        if (stringQuery == null && stringQuery.equals("")){
            return null;
        }else if (stringQuery.length()>1){       // 必须超过3 个字节。
            StringBuffer stringBuffer = new StringBuffer();
            try {
                char c;
                for (int i=0; i<stringQuery.length();i++){
                    c = stringQuery.charAt(i);
                    if (c>=0 && c<= 255){
                        stringBuffer.append(c);
                    }else {
                        byte[] bytes = Character.toString(c).getBytes(Charset.forName("gb2312"));
                        for (int j=0; j<bytes.length;j++){
                            int k = bytes[j];
                            if (k<0){
                                k +=  256;
                            }
                            stringBuffer.append("%"+Integer.toHexString(k).toUpperCase());
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return stringBuffer.toString();
        }
        return null;        // 如果输入的不为三个字节
    }

   /* public static int length(String string){
        String chinese = "[\u0391-\uFFE5]";
        char c ;
        int lengthString = 0;
        for (int i=0; i<string.length();i++){
            c = string.charAt(i);
            if (Character.toString(c).matches(chinese)){
                lengthString +=2;
            }else {
                lengthString +=1;
            }
        }
        return lengthString;
    }*/


   /* public static String toGb2312(String string){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            char ch;
            for (int i =0; i<string.length(); i+=3){
                ch = string.charAt(i);
                if (ch>=0 && ch<= 255){
                    stringBuffer.append(ch);
                }else {
                    stringBuffer.append((char)(Integer.parseInt(string.substring(i+1,i+3) , 16)));
                }
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }*/

}
