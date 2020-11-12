package objects;

/**
 * @Author chouxiaohui
 * @Date 2020/11/5 7:28 PM
 * @Version 1.0
 */

import java.util.List;

/**
 * A single set of features of data.
 * */
public class InputFeatures {
    private List<Integer> input_ids;
    private List<Integer> input_mask;
    private List<Integer> segment_ids;
    private int label_id;
    private boolean is_real_example;

    public InputFeatures(List<Integer> input_ids, List<Integer> input_mask, List<Integer> segment_ids, int label_id, boolean is_real_example) {
        this.input_ids = input_ids;
        this.input_mask = input_mask;
        this.segment_ids = segment_ids;
        this.label_id = label_id;
        this.is_real_example = is_real_example;
    }

    public InputFeatures(List<Integer> input_ids, List<Integer> input_mask, List<Integer> segment_ids, int label_id) {
        this.input_ids = input_ids;
        this.input_mask = input_mask;
        this.segment_ids = segment_ids;
        this.label_id = label_id;
        this.is_real_example = true;
    }

    public List<Integer> getInput_ids() {
        return input_ids;
    }

    public void setInput_ids(List<Integer> input_ids) {
        this.input_ids = input_ids;
    }

    public List<Integer> getInput_mask() {
        return input_mask;
    }

    public void setInput_mask(List<Integer> input_mask) {
        this.input_mask = input_mask;
    }

    public List<Integer> getSegment_ids() {
        return segment_ids;
    }

    public void setSegment_ids(List<Integer> segment_ids) {
        this.segment_ids = segment_ids;
    }

    public int getLabel_id() {
        return label_id;
    }

    public void setLabel_id(int label_id) {
        this.label_id = label_id;
    }

    public boolean isIs_real_example() {
        return is_real_example;
    }

    public void setIs_real_example(boolean is_real_example) {
        this.is_real_example = is_real_example;
    }

    @Override
    public String toString() {
        return "InputFeatures{" +
                "input_ids=" + input_ids +
                ", input_mask=" + input_mask +
                ", segment_ids=" + segment_ids +
                ", label_id=" + label_id +
                ", is_real_example=" + is_real_example +
                '}';
    }
}

