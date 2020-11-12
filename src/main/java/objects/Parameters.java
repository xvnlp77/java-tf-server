package objects;

/**
 * @Author chouxiaohui
 * @Date 2020/11/5 4:37 PM
 * @Version 1.0
 */
public class Parameters {

    private String vocab_file;
    private boolean do_lower_case;
    private int random_seed;
    private int max_seq_length;
    private String dupe_factor;
    private double short_seq_prob;
    private double masked_lm_prob;
    private double max_predictions_per_seq;
    private String output_file;

    public String getVocab_file() {
        return vocab_file;
    }

    public void setVocab_file(String vocab_file) {
        this.vocab_file = vocab_file;
    }

    public boolean isDo_lower_case() {
        return do_lower_case;
    }

    public void setDo_lower_case(boolean do_lower_case) {
        this.do_lower_case = do_lower_case;
    }

    public int getRandom_seed() {
        return random_seed;
    }

    public void setRandom_seed(int random_seed) {
        this.random_seed = random_seed;
    }

    public int getMax_seq_length() {
        return max_seq_length;
    }

    public void setMax_seq_length(int max_seq_length) {
        this.max_seq_length = max_seq_length;
    }

    public String getDupe_factor() {
        return dupe_factor;
    }

    public void setDupe_factor(String dupe_factor) {
        this.dupe_factor = dupe_factor;
    }

    public double getShort_seq_prob() {
        return short_seq_prob;
    }

    public void setShort_seq_prob(double short_seq_prob) {
        this.short_seq_prob = short_seq_prob;
    }

    public double getMasked_lm_prob() {
        return masked_lm_prob;
    }

    public void setMasked_lm_prob(double masked_lm_prob) {
        this.masked_lm_prob = masked_lm_prob;
    }

    public double getMax_predictions_per_seq() {
        return max_predictions_per_seq;
    }

    public void setMax_predictions_per_seq(double max_predictions_per_seq) {
        this.max_predictions_per_seq = max_predictions_per_seq;
    }

    public String getOutput_file() {
        return output_file;
    }

    public void setOutput_file(String output_file) {
        this.output_file = output_file;
    }
}
