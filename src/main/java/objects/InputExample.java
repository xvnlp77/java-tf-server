package objects;

/**
 * @Author chouxiaohui
 * @Date 2020/11/5 7:22 PM
 * @Version 1.0
 */


/**
 *    Args:
 *       guid: Unique id for the example.
 *       text_a: string. The untokenized text of the first sequence. For single
 *         sequence tasks, only this sequence must be specified.
 *       text_b: (Optional) string. The untokenized text of the second sequence.
 *         Only must be specified for sequence pair tasks.
 *       label: (Optional) string. The label of the example. This should be
 *         specified for train and dev examples, but not for test examples.
 * */
public class InputExample {

    private String guid;
    private String text_a;
    private String text_b;
    private String label;


    public InputExample(String guid, String text_a) {
        this.guid = guid;
        this.text_a = text_a;
    }

    public InputExample(String guid, String text_a, String text_b, String label) {
        this.guid = guid;
        this.text_a = text_a;
        this.text_b = text_b;
        this.label = label;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getText_a() {
        return text_a;
    }

    public void setText_a(String text_a) {
        this.text_a = text_a;
    }

    public String getText_b() {
        return text_b;
    }

    public void setText_b(String text_b) {
        this.text_b = text_b;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
