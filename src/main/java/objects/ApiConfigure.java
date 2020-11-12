package objects;

/**
 * @Author chouxiaohui
 * @Date 2020/11/11 2:47 PM
 * @Version 1.0
 */
public class ApiConfigure {
    private String host;
    private int grpcPort;
    private int restfulPort;
    private String modelName;
    private String signatureName;
    private String outputName;
    private int dimSize;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getGrpcPort() {
        return grpcPort;
    }

    public void setGrpcPort(int grpcPort) {
        this.grpcPort = grpcPort;
    }

    public int getRestfulPort() {
        return restfulPort;
    }

    public void setRestfulPort(int restfulPort) {
        this.restfulPort = restfulPort;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getSignatureName() {
        return signatureName;
    }

    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }

    public String getOutputName() {
        return outputName;
    }

    public void setOutputName(String outputName) {
        this.outputName = outputName;
    }

    public int getDimSize() {
        return dimSize;
    }

    public void setDimSize(int dimSize) {
        this.dimSize = dimSize;
    }
}
