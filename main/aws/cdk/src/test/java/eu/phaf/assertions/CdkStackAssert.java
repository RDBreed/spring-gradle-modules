package eu.phaf.assertions;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.MapAssert;
import software.amazon.awscdk.assertions.Template;

import java.util.Map;

public class CdkStackAssert extends AbstractAssert<CdkStackAssert, Template> {
    private final Template template;

    private CdkStackAssert(Template template) {
        super(template, CdkStackAssert.class);
        this.template = template;
    }

    public static CdkStackAssert assertThat(Template template) {
        return new CdkStackAssert(template);
    }

    public ElasticLoadBalancingAssert containsElasticLoadBalancing() {
        Map<String, Map<String, Object>> resources = this.actual.findResources(CdkResourceType.ELASTIC_LOAD_BALANCING.getValue());
        ((MapAssert) Assertions.assertThat(resources).isNotEmpty()).hasSize(1);
        return ElasticLoadBalancingAssert.assertThat(resources.entrySet().stream().findFirst().get().getValue())
                .withCdkStackAssert(this);
    }

    public SecurityGroupAssert containsSecurityGroup() {
        Map<String, Map<String, Object>> resources = this.actual.findResources(CdkResourceType.SECURITY_GROUP.getValue());
        ((MapAssert) Assertions.assertThat(resources).isNotEmpty()).hasSize(1);
        return SecurityGroupAssert.assertThat(resources.entrySet().stream().findFirst().get().getValue())
                .withCdkStackAssert(this);
    }
}
