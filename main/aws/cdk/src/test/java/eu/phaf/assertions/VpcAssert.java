package eu.phaf.assertions;

import org.assertj.core.api.Assertions;

import java.util.Map;

public class VpcAssert extends CdkResourceAbstractAssert<VpcAssert, Map<String, Object>> {

    private VpcAssert(Map<String, Object> properties) {
        super(properties, VpcAssert.class);
    }

    public static VpcAssert assertThat(Map<String, Object> actual) {
        return new VpcAssert(actual);
    }

    public VpcAssert containsCidrBlock(String value) {
        Assertions.assertThat(properties.get("CidrBlock")).asString().isEqualTo(value);
        return this;
    }

    public VpcAssert isDnsHostnamesEnabled(boolean value) {
        Assertions.assertThat(properties.get("EnableDnsHostnames")).isEqualTo(value);
        return this;
    }


}
