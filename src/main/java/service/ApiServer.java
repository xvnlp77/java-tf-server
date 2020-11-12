package service;


import com.alibaba.fastjson.JSON;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import objects.ApiConfigure;
import objects.InputExample;
import objects.InputFeatures;
import objects.TwoTuple;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONObject;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import tensorflow.serving.Model;
import tensorflow.serving.Predict;
import tensorflow.serving.PredictionServiceGrpc;
import utils.FileOperator;
import utils.HttpPostClient;
import utils.TFInputCreator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author chouxiaohui
 * @Date 2020/10/21 7:30 AM
 * @Version 1.0
 */
public class ApiServer {
    private String host;
    private int grpcPort;
    private int restfulPort;
    private String modelName;
    private String signatureName;
    private String outputName;
    private int dimSize;
    private TFInputCreator tfInputCreator;

    public ApiServer(String apiConfigureFile,String parametersFile){
        init(apiConfigureFile);
        tfInputCreator = new TFInputCreator(parametersFile);
    }

    public void init(String filePath){
        String jsonStr = FileOperator.readJsonFile(filePath);
        ApiConfigure apiConfigure = JSON.parseObject(jsonStr, ApiConfigure.class);
        host = apiConfigure.getHost();
        grpcPort = apiConfigure.getGrpcPort();
        restfulPort = apiConfigure.getRestfulPort();
        modelName = apiConfigure.getModelName();
        signatureName = apiConfigure.getSignatureName();
        outputName = apiConfigure.getOutputName();
        dimSize = apiConfigure.getDimSize();
    }


    /**
     * create grpc data
     * */
    public TwoTuple createGRpcData(String text){
        InputExample inputExample = tfInputCreator._create_examples(text);
        InputFeatures inputFeatures = tfInputCreator.convert_single_example(inputExample);
        System.out.println(inputFeatures.toString());
        List<Integer> input_ids = inputFeatures.getInput_ids();
        float[] floats = ArrayUtils.toPrimitive(input_ids.stream().map(Float::valueOf).toArray(Float[]::new));
        float[][] input_ids_array = new float[][]{floats};

        List<Integer> segment_ids = inputFeatures.getSegment_ids();
        float[] segment_ids_floats = ArrayUtils.toPrimitive(segment_ids.stream().map(Float::valueOf).toArray(Float[]::new));
        float[][] segment_ids_array = new float[][]{segment_ids_floats};

        return new TwoTuple(input_ids_array,segment_ids_array);

    }

    /**
     * grpc调用
     * */
    public void inference(String text){
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, grpcPort).usePlaintext(true).build();

        //这里还是先用block模式
        PredictionServiceGrpc.PredictionServiceBlockingStub stub = PredictionServiceGrpc.newBlockingStub(channel);

        //创建请求
        Predict.PredictRequest.Builder predictRequestBuilder = Predict.PredictRequest.newBuilder();

        //模型名称和模型方法名预设
        Model.ModelSpec.Builder modelSpecBuilder = Model.ModelSpec.newBuilder();
        modelSpecBuilder.setName(modelName);
        modelSpecBuilder.setSignatureName(signatureName);

        predictRequestBuilder.setModelSpec(modelSpecBuilder);

        //设置入参,访问默认是最新版本，如果需要特定版本可以使用tensorProtoBuilder.setVersionNumber方法
        TensorProto.Builder tpbInput0 = TensorProto.newBuilder();
        tpbInput0.setDtype(DataType.DT_FLOAT);

        TensorProto.Builder tpbInput1 = TensorProto.newBuilder();
        tpbInput1.setDtype(DataType.DT_FLOAT);

        TwoTuple<float[][],float[][]> featuresTensorDatas = createGRpcData(text);
        float[][] featuresTensorData = featuresTensorDatas.first;
        float[][] featuresTensorData2 = featuresTensorDatas.second;


        for (int i = 0; i < featuresTensorData.length; ++i) {
            for (int j = 0; j < featuresTensorData[i].length; ++j) {
                tpbInput0.addFloatVal(featuresTensorData[i][j]);
            }
        }

        for (int i = 0; i < featuresTensorData2.length; ++i) {
            for (int j = 0; j < featuresTensorData2[i].length; ++j) {
                tpbInput1.addFloatVal(featuresTensorData2[i][j]);
            }
        }

        TensorShapeProto.Builder tensorShapeBuilder = TensorShapeProto.newBuilder();
        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(1));
        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(dimSize));
        tpbInput0.setTensorShape(tensorShapeBuilder.build());


        TensorShapeProto.Builder tensorShapeBuilder2 = TensorShapeProto.newBuilder();
        tensorShapeBuilder2.addDim(TensorShapeProto.Dim.newBuilder().setSize(1));
        tensorShapeBuilder2.addDim(TensorShapeProto.Dim.newBuilder().setSize(dimSize));
        tpbInput1.setTensorShape(tensorShapeBuilder2.build());



        Map<String,TensorProto> inputs = new HashMap<>();
        inputs.put("input_0",tpbInput0.build());
        inputs.put("input_1",tpbInput1.build());

        predictRequestBuilder.putAllInputs(inputs);

        //访问并获取结果
        Predict.PredictResponse predictResponse = stub.predict(predictRequestBuilder.build());
        TensorProto result=predictResponse.toBuilder().getOutputsOrThrow(outputName);
        System.out.println("{\"predictions\":"+result.getFloatValList()+"}");
    }


    /**
     * create restful data
     * */
    public String createRestfulData(String text){
        InputExample inputExample = tfInputCreator._create_examples(text);
        InputFeatures inputFeatures = tfInputCreator.convert_single_example(inputExample);

        List<Integer> input_ids = inputFeatures.getInput_ids();
        List<Integer> segment_ids = inputFeatures.getSegment_ids();

        String predictRequest =String.format("{\"signature_name\": \"%s\", \"instances\":[{\"input_0\":%s,\"input_1\":%s}] }" ,signatureName,input_ids, segment_ids);

        return predictRequest;

    }

    /**
     * restful调用
     * */
    public void inference_by_restful(String text){

        String SERVER_URL = String.format("http://%s:%d/v1/models/%s:%s",host,restfulPort,modelName,signatureName);

        String predictRequest = createRestfulData(text);

        ResponseHandler<String> responseHandler = new BasicResponseHandler() {
            @Override
            public String handleResponse(
                    final HttpResponse response) throws HttpResponseException, IOException {
                if (response.getStatusLine().getStatusCode() == 500) {
                    return "{\"statusCode\":0}";
                }
                return super.handleResponse(response);
            }
        };

        JSONObject response = null;
        HttpPostClient postClient = new HttpPostClient();

        try {
            response = new JSONObject(postClient.execute(SERVER_URL, predictRequest, responseHandler));
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static void main(String[] args) {
        // 参数配置文件 json格式
        String apiConfigureFile = "src/main/resources/apiconfiguration.json";
        String parametersFile = "src/main/resources/parameters.json";


        ApiServer apiServer = new ApiServer(apiConfigureFile,parametersFile);

        // 例子
        String text = "应用于情感分析";
        //grpc 调用
        apiServer.inference(text);

        //restful 调用
        apiServer.inference_by_restful(text);

        apiServer.inference(text);
        apiServer.inference(text);

        apiServer.inference_by_restful(text);
        apiServer.inference_by_restful(text);



    }

}
