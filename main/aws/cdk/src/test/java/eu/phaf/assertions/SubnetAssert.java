package eu.phaf.assertions;

import java.util.Map;

public class SubnetAssert extends CdkResourcesAbstractAssert<SubnetAssert, Map<String, Map<String, Object>>> {

    protected SubnetAssert(Map<String, Map<String, Object>> map) {
        super(map, SubnetAssert.class);
    }

    public static SubnetAssert assertThat(Map<String, Map<String, Object>> resources) {
        return new SubnetAssert(resources);
    }
}
