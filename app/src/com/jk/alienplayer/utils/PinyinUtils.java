package com.jk.alienplayer.utils;

import java.util.ArrayList;

import com.jk.alienplayer.utils.HanziToPinyin.Token;

public class PinyinUtils {

    public static String getPinyinString(String input) {
        ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (Token token : tokens) {
                if (Token.PINYIN == token.type) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString().toLowerCase();
    }
}
