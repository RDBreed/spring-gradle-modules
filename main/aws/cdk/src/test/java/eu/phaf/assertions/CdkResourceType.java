package eu.phaf.assertions;

public enum CdkResourceType {
    ELASTIC_LOAD_BALANCING("AWS::ElasticLoadBalancingV2::LoadBalancer"),
    SECURITY_GROUP("AWS::EC2::SecurityGroup");

    private final String value;

    CdkResourceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
