package eu.phaf.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractMapAssert;

import java.util.Map;

public class CdkResourcesAbstractAssert<SELF extends AbstractMapAssert<SELF, Map<String, Map<String, Object>>, String, Map<String, Object>>, ACTUAL> extends AbstractMapAssert<SELF, Map<String, Map<String, Object>>, String, Map<String, Object>> {
    CdkStackAssert cdkStackAssert;

    protected CdkResourcesAbstractAssert(Map<String, Map<String, Object>> map, Class selfType) {
        super(map, selfType);
    }

    protected  <T extends CdkResourceAbstractAssert<SELF extends AbstractAssert<?, Map<String, Object>>, ACTUAL>> T withCdkStackAssert(CdkStackAssert cdkStackAssert) {
        this.cdkStackAssert = cdkStackAssert;
        return (T) this;
    }
}
