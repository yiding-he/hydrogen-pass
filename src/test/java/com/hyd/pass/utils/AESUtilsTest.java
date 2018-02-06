package com.hyd.pass.utils;

import org.junit.Test;

/**
 * (description)
 * created at 2018/2/6
 *
 * @author yidin
 */
public class AESUtilsTest {

    @Test
    public void testEncodeDecode() throws Exception {
        String content = "1932年1月28日，日軍向閘北進攻，國軍堅決抵抗，第一次淞滬戰爭爆發，" +
                "正當蔣介石準備大量調集國軍與日作戰時，2月4日彭德懷率紅三和紅四軍攻打江西贛州，" +
                "歷時33天，拖住蔣五萬大軍無法增援上海，3月3日中日停戰。 3月7日在國軍守城部隊" +
                "的抵抗和援軍的反擊下，紅軍攻城失敗。";

        System.out.println(content);
        String key = "12345678";

        String encoded = AESUtils.encode128(content, key);
        System.out.println(encoded);

        String decoded = AESUtils.decode128(encoded, key);
        System.out.println(decoded);
    }
}