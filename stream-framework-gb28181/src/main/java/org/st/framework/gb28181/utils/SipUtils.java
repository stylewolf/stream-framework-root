package org.st.framework.gb28181.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SipUtils {

    public static Pattern domainPattern = Pattern.compile("(?<=\\@)[^\\>]+");

    public static  String domain(String input){
        Matcher m = domainPattern.matcher(input);
        List<String> domains = new ArrayList<String>();
        while (m.find()) {
            domains.add(m.group());
        }
        if(!domains.isEmpty()){
            return domains.get(0);
        }
        return "";
    }

    public static String domainForAddress(String input){
        int pos = input.indexOf("@");
        if(pos!=-1){
            return input.substring(pos);
        }
        return "";
    }

    public static void main(String[] args) {
        String s = "From: <sip:34020000001320000001@3402000000>;tag=783750658";
        System.out.println(domainForAddress(s));
    }
}
