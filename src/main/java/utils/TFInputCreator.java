package utils;

import com.alibaba.fastjson.JSON;
import objects.InputExample;
import objects.InputFeatures;
import objects.Parameters;
import objects.TwoTuple;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author chouxiaohui
 * @Date 2020/11/5 1:47 PM
 * @Version 1.0
 */
public class TFInputCreator {
    private FullTokenizer tokenizer;
    private Parameters parameters;
    private String CLS = "[CLS]";
    private String SEP = "[SEP]";

    public TFInputCreator(String parametersFile){
        loadParameters(parametersFile);
        tokenizer = new FullTokenizer(parameters.getVocab_file(),parameters.isDo_lower_case());
    }

    /**
     * 加载 Parameters
     */
    public void loadParameters(String parametersFile){
        String jsonStr = FileOperator.readJsonFile(parametersFile);
        parameters = JSON.parseObject(jsonStr, Parameters.class);
    }


    /**
     * Creates examples for the training and dev sets.
     * */
    public InputExample _create_examples(String first_text, String second_text){
        InputExample inputExample = new InputExample("1",first_text,second_text,"0");

        return inputExample;
    }

    public InputExample _create_examples(String first_text){
        InputExample inputExample = new InputExample("1",first_text,null,"0");

        return inputExample;
    }

    /**
     * 转变example
     * */
    public InputFeatures convert_single_example(InputExample inputExample){
        // 参数获取
        int max_seq_length = parameters.getMax_seq_length();

        List<String> tokens_a = tokenizer.tokenize(inputExample.getText_a());
        List<String> tokens_b = null;
        if(inputExample.getText_b()!=null){
            tokens_b = tokenizer.tokenize(inputExample.getText_b());
        }

        if(tokens_b!=null){
            TwoTuple twoTuple = _truncate_seq_pair(tokens_a,tokens_b,max_seq_length-3);
            tokens_a = (List<String>) twoTuple.first;
            tokens_b = (List<String>) twoTuple.second;
        }else {
            if(tokens_a.size()>max_seq_length-2){
                List<String> _tokens_a = new ArrayList<>();
                for(int i=0;i<max_seq_length-2;i++){
                    _tokens_a.add(tokens_a.get(i));
                }
                tokens_a = _tokens_a;
            }
        }

        List<String> tokens = new ArrayList<>();
        List<Integer> segment_ids = new ArrayList<>();

        tokens.add(CLS);
        segment_ids.add(0);

        for(String token: tokens_a){
            tokens.add(token);
            segment_ids.add(0);
        }
        tokens.add(SEP);
        segment_ids.add(0);

        if(tokens_b!=null){
           for(String token:tokens_b){
               tokens.add(token);
               segment_ids.add(1);
           }
           tokens.add(SEP);
           segment_ids.add(1);
        }

        List<Integer> input_ids = tokenizer.convert_tokens_to_ids(tokens);

        List<Integer> input_mask = new ArrayList<>();
        for(int i =0;i<input_ids.size();i++){
            input_mask.add(1);
        }

        while(input_ids.size()<max_seq_length){
            input_ids.add(0);
            input_mask.add(0);
            segment_ids.add(0);
        }

        assert input_ids.size() == max_seq_length;
        assert input_mask.size() == max_seq_length;
        assert segment_ids.size() == max_seq_length;

        InputFeatures inputFeatures = new InputFeatures(input_ids,input_mask,segment_ids,0); //label_id 随便写的，暂时无用

        return inputFeatures;


    }

    /**
     * 句子截断
     * */
    public TwoTuple _truncate_seq_pair(List<String> tokens_a, List<String> tokens_b, int max_length){
        while (true){
            int total_length = tokens_a.size() + tokens_b.size();
            if(total_length<=max_length){
                break;
            }
            if(tokens_a.size()>tokens_b.size()){
                tokens_a.remove(tokens_a.size()-1);
            }else {
                tokens_b.remove(tokens_b.size()-1);
            }
        }
        return new TwoTuple(tokens_a,tokens_b);

    }




}
