package utils;

import com.google.common.base.Joiner;

import java.io.*;
import java.text.Normalizer;
import java.util.*;

/**
 * @Author chouxiaohui
 * @Date 2020/10/28 7:21 PM
 * @Version 1.0
 */


public class FullTokenizer {
    private Map<String, Integer> vocab;
    private Map<Integer, String> inv_vocab;
    private BasicTokenizer basicTokenizer;
    private WordpieceTokenize wordpieceTokenize;

    /**
     * vocab_file : vocab文件
     * do_lower_case : 是否字母都小写
     * */
    public FullTokenizer(String vocab_file, boolean do_lower_case) {
        vocab = TokenizerUtils.load_vocab(vocab_file);
        inv_vocab = TokenizerUtils.exchangeMapKV(vocab);
        basicTokenizer = new BasicTokenizer(do_lower_case);
        wordpieceTokenize = new WordpieceTokenize(vocab);
    }

    /**
     * vocab_file : vocab文件
     * do_lower_case : 是否字母都小写
     * unk_token : 写法
     * max_input_chars_per_word : 限定每个词的最大字符长度
     * */
    public FullTokenizer(String vocab_file, boolean do_lower_case, String unk_token, int max_input_chars_per_word) {
        vocab = TokenizerUtils.load_vocab(vocab_file);
        inv_vocab = TokenizerUtils.exchangeMapKV(vocab);
        basicTokenizer = new BasicTokenizer(do_lower_case);
        wordpieceTokenize = new WordpieceTokenize(vocab, unk_token, max_input_chars_per_word);
    }

    public List<String> tokenize(String text) {
        List<String> split_tokens = new ArrayList<>();
        for (String token : basicTokenizer.tokenize(text)) {
            for (String sub_token : wordpieceTokenize.tokenize(token)) {
                split_tokens.add(sub_token);
            }
        }
        return split_tokens;

    }

    public List<Integer> convert_tokens_to_ids(List<String> tokens) {
        return TokenizerUtils.convert_by_vocab(vocab, tokens);
    }

    public List<String> convert_ids_to_tokens(List<Integer> ids) {
        return TokenizerUtils.convert_by_vocab(inv_vocab, ids);
    }

    public static void main(String[] args) {
        String filePath = "src/main/resources/vocab.txt";

        FullTokenizer fullTokenizer = new FullTokenizer(filePath, true);
        System.out.println(fullTokenizer.tokenize("配合 Jenkins 帮您自动完成从代码提交到应用部署的 DevOps 完整流程，确保只有通过自动测试的代码才能交付和部署，高效替代业内部署复杂、迭代缓慢的传统方式。"));

//        System.out.println(fullTokenizer.tokenize("UNwant\u00E9d,running"));
//        System.out.println(fullTokenizer.convert_tokens_to_ids(fullTokenizer.tokenize("UNwant\u00E9d,running")));
//        System.out.println(fullTokenizer.tokenize(" \tHeLLo!how  \n Are yoU?  "));
//        System.out.println(fullTokenizer.tokenize("unwantedX running"));
//        System.out.println(fullTokenizer.tokenize("ah\u535A\u63A8zz"));
//        System.out.println(TokenizerUtils.convert_to_unicode("你好，我的朋友aaa"));


    }
}

/**
 * 处理公共方法
 */
class TokenizerUtils {

    /**
     * 获取字符的unicode编码（16进制）
     */
    public static int ord(char c) {
        String unicode = Integer.toHexString(c);
        if (unicode.length() <= 2) {
            unicode = "00" + unicode;
        }
        return Integer.parseInt(unicode.toUpperCase(), 16);
    }

    /**
     * 获取vocab词典
     */
    public static Map load_vocab(String vocab_file) {
        Map vocab = new LinkedHashMap();
        int index = 0;

        BufferedReader br;

        try {
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(new File(vocab_file)), "UTF-8"));
            String token;
            while ((token = br.readLine()) != null) {
//                token = convert_to_unicode(token);
                vocab.put(token, index);
                index += 1;
            }
            br.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return vocab;
    }

    /**
     * 转换编码，java不用
     * Converts `text` to Unicode (if it's not already), assuming utf-8 input.
     */
    public static String convert_to_unicode(String text) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {
            // 取出每一个字符
            char c = text.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
//        return text;
    }

    /**
     * 使用vocab转变[tokens|ids]序列
     */
    public static List convert_by_vocab(Map vocab, List items) {
        List output = new ArrayList();
        for (Object item : items) {
            output.add(vocab.get(item));
        }
        return output;
    }

    /**
     * Checks whether `chars` is a control character.
     */
    public static boolean _is_control(char ch) {
        if (ch == '\t' || ch == '\n' || ch == '\r') {
            return false;
        }
        int cat = Character.getType(ch);
        if (cat == Character.CONTROL || cat == Character.FORMAT) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether `chars` is a whitespace character.
     */
    public static boolean _is_whitespace(char ch) {

        if (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            return true;

        }
        int cat = Character.getType(ch);
        if (cat == Character.SPACE_SEPARATOR) {
            return true;
        }
        return false;
    }


    /**
     * 判断是否是中文字符
     * Checks whether CP is the codepoint of a CJK character.
     */
    public static boolean _is_chinese_char(int cp) {
        if ((cp >= 0x4E00 && cp <= 0x9FFF) || (cp >= 0x3400 && cp <= 0x4DBF) ||
                (cp >= 0x20000 && cp <= 0x2A6DF) ||
                (cp >= 0x2A700 && cp <= 0x2B73F) ||
                (cp >= 0x2B740 && cp <= 0x2B81F) ||
                (cp >= 0x2B820 && cp <= 0x2CEAF) ||
                (cp >= 0xF900 && cp <= 0xFAFF) ||
                (cp >= 0x2F800 && cp <= 0x2FA1F)) {
            return true;
        }

        return false;

    }


    /**
     * Checks whether `chars` is a punctuation character.
     */
    public static boolean _is_punctuation(char ch) {
        int cp = TokenizerUtils.ord(ch);

        if ((cp >= 33 && cp <= 47) || (cp >= 58 && cp <= 64) || (cp >= 91 && cp <= 96) || (cp >= 123 && cp <= 126)) {
            return true;
        }

        int cat = Character.getType(ch);
        if ((cat >= 20 && cat <= 24) | (cat >= 29 && cat <= 30)) {
            return true;
        }
        return false;

    }


    /**
     * tokens to id
     */
    public static List<Integer> convert_tokens_to_ids(Map<String, Integer> vocab, List<String> tokens) {
        return convert_by_vocab(vocab, tokens);
    }

    /**
     * id to tokens
     */
    public static List<Integer> convert_ids_to_tokens(Map<Integer, String> inv_vocab, List<Integer> ids) {
        return convert_by_vocab(inv_vocab, ids);
    }

    /**
     * whitepace_tokenize
     */
    public static List<String> whitespace_tokenize(String text) {

        // start：处理Unicdoe空白字符'\u2000'
        StringBuffer textBuffer = new StringBuffer();
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                textBuffer.append(" ");
            } else {
                textBuffer.append(ch);
            }
        }
        text = textBuffer.toString();
        // end

        text = text.trim();  //jdk11 用strip()方法，可以删除start到end的代码

        List<String> output = new ArrayList<>();
        if (text.length() != 0) {
            String[] words = text.split("\\s+");
            output = Arrays.asList(words);
        }
        return output;
    }


    /**
     * Map key value 互相转换
     */
    public static Map exchangeMapKV(Map<String, Integer> map) {
        Map newMap = new LinkedHashMap();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            newMap.put(value, key);
        }
        return newMap;
    }


}


/**
 * Runs basic tokenization (punctuation splitting, lower casing, etc.).
 */
class BasicTokenizer {

    private boolean do_lower_case;

    public BasicTokenizer(boolean do_lower_case) {
        this.do_lower_case = do_lower_case;
    }

    public BasicTokenizer() {
        this.do_lower_case = true;
    }

    /**
     * Tokenizes a piece of text.
     */
    public List<String> tokenize(String text) {
//        text = TokenizerUtils.convert_to_unicode(text);
        text = _clean_text(text);
        text = _tokenize_chinese_chars(text);

        List<String> orig_tokens = TokenizerUtils.whitespace_tokenize(text);
        List<String> split_tokens = new ArrayList<>();
        for (String token : orig_tokens) {
            if (do_lower_case) {
                token = token.toLowerCase();
                token = _run_strip_accents(token);
            }
            split_tokens.addAll(_run_split_on_punc(token));
        }
        List<String> output_tokens = TokenizerUtils.whitespace_tokenize(Joiner.on(" ").join(split_tokens));
        return output_tokens;
    }


    /**
     * 去除accent
     * 参考：https://stackoverflow.com/questions/2443325/java-how-to-get-unicode-name-of-a-character-or-its-type-category
     */
    public String _run_strip_accents(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        StringBuilder output = new StringBuilder();
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            int cat = Character.getType(ch);
            if (cat == Character.NON_SPACING_MARK) {
                continue;
            }
            output.append(ch);
        }
        return output.toString();
    }

    /**
     * Splits punctuation on a piece of text.
     */
    public List<String> _run_split_on_punc(String text) {
        char[] chars = text.toCharArray();
        int i = 0;
        List<String> output = new ArrayList<>();
        StringBuffer subStr = new StringBuffer();

        while (i < chars.length) {
            char ch = chars[i];
            if (TokenizerUtils._is_punctuation(ch)) {
                output.add(subStr.toString());
                output.add(ch + "");
                subStr = new StringBuffer();
            } else {
                subStr.append(ch);
            }
            i += 1;
        }
        output.add(subStr.toString());

        return output;
    }


    /**
     * Adds whitespace around any CJK character.
     */
    public String _tokenize_chinese_chars(String text) {
        StringBuilder output = new StringBuilder();
        char[] chars = text.toCharArray();

        for (char ch : chars) {
            int cp = TokenizerUtils.ord(ch);
            if (TokenizerUtils._is_chinese_char(cp)) {
                output.append(" ");
                output.append(ch);
                output.append(" ");
            } else {
                output.append(ch);
            }
        }

        return output.toString();
    }


    /**
     * Performs invalid character removal and whitespace cleanup on text.
     */
    public String _clean_text(String text) {
        StringBuffer output = new StringBuffer();
        char[] chars = text.toCharArray();

        for (char ch : chars) {
            int cp = TokenizerUtils.ord(ch);
            if (cp == 0 || cp == 0xfffd || TokenizerUtils._is_control(ch)) {
                continue;
            }
            if (TokenizerUtils._is_whitespace(ch)) {
                output.append(" ");
            } else {
                output.append(ch);
            }

        }
        return output.toString();
    }

}


/**
 * 细粒度切分词
 */
class WordpieceTokenize {

    private Map vocab;
    private String unk_token;
    private int max_input_chars_per_word;

    public WordpieceTokenize(Map vocab, String unk_token, int max_input_chars_per_word) {
        this.vocab = vocab;
        this.unk_token = unk_token;
        this.max_input_chars_per_word = max_input_chars_per_word;
    }

    public WordpieceTokenize(Map vocab) {
        this.vocab = vocab;
        this.unk_token = "[UNK]";
        this.max_input_chars_per_word = 200;
    }

    public List<String> tokenize(String text) {
//        text = TokenizerUtils.convert_to_unicode(text);
        List<String> output_tokens = new ArrayList<>();

        for (String token : TokenizerUtils.whitespace_tokenize(text)) {

            char[] chars = token.toCharArray();

            if (chars.length > max_input_chars_per_word) {
                output_tokens.add(unk_token);
                continue;
            }

            boolean is_bad = false;
            int start = 0;
            List<String> sub_tokens = new ArrayList<>();

            while (start < chars.length) {
                int end = chars.length;
                String cur_substr = null;

                while (start < end) {

                    String substr = token.substring(start, end);

                    if (start > 0) {
                        substr = "##" + substr;
                    }

                    if (vocab.containsKey(substr)) {
                        cur_substr = substr;
                        break;
                    }
                    end -= 1;
                }

                if (cur_substr == null) {
                    is_bad = true;
                    break;
                }

                sub_tokens.add(cur_substr);
                start = end;
            }

            if (is_bad) {
                output_tokens.add(unk_token);
            } else {
                output_tokens.addAll(sub_tokens);
            }

        }

        return output_tokens;
    }


}