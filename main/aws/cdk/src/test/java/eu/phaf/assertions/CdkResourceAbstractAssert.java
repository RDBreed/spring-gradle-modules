package eu.phaf.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;

public abstract class CdkResourceAbstractAssert<SELF extends AbstractAssert<SELF, Map<String, Object>>, ACTUAL> extends AbstractAssert<SELF, Map<String, Object>>{
    final Map<String, Object> properties;
    CdkStackAssert cdkStackAssert;

    protected CdkResourceAbstractAssert(Map<String, Object> actual, Class<?> selfType) {
        super(actual, selfType);
        properties = (Map<String, Object>) this.actual.get("Properties");
    }

    protected  <T extends CdkResourceAbstractAssert<SELF, ACTUAL>> T withCdkStackAssert(CdkStackAssert cdkStackAssert) {
        this.cdkStackAssert = cdkStackAssert;
        return (T) this;
    }

    public <T extends CdkResourceAbstractAssert<SELF, ACTUAL>> T containsTagsExactly(List<Map.Entry<Object, Object>> keyValues) {
        Assertions.assertThat((List<Map<String, String>>) properties.get("Tags"))
                .zipSatisfy(keyValues, (tags, keyValue) -> {
                    String key = tags.get("Key");
                    String value = tags.get("Value");
                    Assertions.assertThat(key).isEqualTo(keyValue.getKey());
                    Assertions.assertThat(value).isEqualTo(keyValue.getValue());
                });
        return (T) this;
    }

    public CdkStackAssert and() {
        return cdkStackAssert;
    }
}
