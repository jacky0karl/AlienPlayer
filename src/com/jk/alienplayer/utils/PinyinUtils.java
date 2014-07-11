package com.jk.alienplayer.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtils {
    private static HanyuPinyinOutputFormat sFormat = new HanyuPinyinOutputFormat();

    public PinyinUtils() {
        sFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
    }

    public String getPinyinString(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            sb.append(getPinyin(ch));
        }
        return sb.toString();
    }

    public String getPinyin(char ch) {
        String[] pinyin = null;
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(ch, sFormat);
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        if (pinyin != null) {
            return pinyin[0];
        } else {
            return String.valueOf(ch);
        }
    }
}
