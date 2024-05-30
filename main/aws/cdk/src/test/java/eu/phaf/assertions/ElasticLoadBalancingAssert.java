package eu.phaf.assertions;

import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;

public class ElasticLoadBalancingAssert extends CdkResourceAbstractAssert<ElasticLoadBalancingAssert, Map<String, Object>> {


    private ElasticLoadBalancingAssert(Map<String, Object> resource) {
        super(resource, ElasticLoadBalancingAssert.class);
    }

    public static ElasticLoadBalancingAssert assertThat(Map<String, Object> actual) {
        return new ElasticLoadBalancingAssert(actual);
    }

    public ElasticLoadBalancingAssert isDeletionProtection(boolean deletionProtection) {
        List<?> loadBalancerAttributes = (List<?>) properties.get("LoadBalancerAttributes");
        Assertions.assertThat(loadBalancerAttributes).anySatisfy(attrs -> {
            Object key = ((Map<?, ?>) attrs).get("Key");
            Object value = ((Map<?, ?>) attrs).get("Value");
            Assertions.assertThat(key).isEqualTo("deletion_protection.enabled");
            Assertions.assertThat(value).isEqualTo(String.valueOf(deletionProtection));
        });
        return this;
    }

    public ElasticLoadBalancingAssert isScheme(String value) {
        Assertions.assertThat(properties.get("Scheme")).asString().isEqualTo(value);
        return this;
    }
}
