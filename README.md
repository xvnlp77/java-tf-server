# java调用tf-serving

## 1. 功能介绍
java实现

1.tf-server调用：grpc方式和restful方式；

2.bert tokenizer和input构造方法


## 2. 文件目录结构

```
├── java
│   ├── objects
│   │   ├── ApiConfigure.java     //tf-serving参数
│   │   ├── InputExample.java     //input中间形式
│   │   ├── InputFeatures.java    //input最终形式
│   │   ├── Parameters.java       //tokenizer参数
│   │   └── TwoTuple.java         
│   ├── service
│   │   └── ApiServer.java        // 调用入口
│   └── utils
│       ├── FileOperator.java
│       ├── FullTokenizer.java    //bert tokenizer实现
│       ├── HttpPostClient.java     
│       └── TFInputCreator.java   //input构造
└── resources
    ├── apiconfiguration.json     //tf-serving配置文件
    ├── parameters.json           //tokenizer配置文件
    └── vocab.txt                 //bert词表

```

## 3。配置文件说明

tf-serving配置文件:apiconfiguration.json
```json
{
  "host":"",
  "grpcPort":8512,
  "restfulPort":8513,
  "modelName":"chinese_roberta_wwm_ext",
  "signatureName":"predict",
  "outputName":"result",
  "dimSize":50
}

```

tokenizer配置文件:parameters.json
```json
{
  "do_lower_case": true,
  "vocab_file": "src/main/resources/vocab.txt",
  "random_seed": 10,
  "max_seq_length": 50,
  "dupe_factor": 1,
  "short_seq_prob": 1,
  "masked_lm_prob": 1,
  "max_predictions_per_seq": 1,
  "output_file": ""
}

```

## 使用说明

```java
    // 参数配置文件 json格式
    String apiConfigureFile = "src/main/resources/apiconfiguration.json";
    String parametersFile = "src/main/resources/parameters.json";

    ApiServer apiServer = new ApiServer(apiConfigureFile,parametersFile);

    // 例子
    String text = "A股一日游还是持续回暖?机构激辩后市行情";
    
    //grpc 调用
    apiServer.inference(text);

    //restful 调用
    apiServer.inference_by_restful(text);


```