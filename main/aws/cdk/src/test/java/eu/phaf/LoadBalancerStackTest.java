package eu.phaf;

import eu.phaf.assertions.CdkStackAssert;
import eu.phaf.assertions.SecurityGroupAssert.SecurityGroupIngressProps;
import eu.phaf.environments.Environment;
import eu.phaf.stacks.infrastructure.CommonStack;
import eu.phaf.stacks.infrastructure.LoadbalancerStack;
import eu.phaf.stacks.resource.StackResourcesProvider;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.assertions.Template;

import java.util.AbstractMap;
import java.util.List;

public class LoadBalancerStackTest {

    @Test
    public void testStack() {
        App app = new App();
        Environment environment = Environment.makeEnv("123", "eu", "dev");
        StackResourcesProvider stackResourcesProvider = new StackResourcesProvider();
        // simulate tagging here
        Tags.of(app).add("environment", environment.name());
        LoadbalancerStack stack = new LoadbalancerStack(app, "test-lb", environment, stackResourcesProvider);
        Template template = TemplateUtils.loadTemplates(stack);
        CdkStackAssert.assertThat(template)
                .containsElasticLoadBalancing()
                .isDeletionProtection(false)
                .isScheme("internet-facing")
                .containsTagsExactly(List.of(new AbstractMap.SimpleEntry<>("environment", "dev")))
                .and()
                .containsSecurityGroup()
                .containsSecurityGroupIngress(List.of(new SecurityGroupIngressProps("0.0.0.0/0", "tcp", 80, 80)))
                .hasVpcId("vpc-12345")
                .containsTagsExactly(List.of(new AbstractMap.SimpleEntry<>("environment", "dev")));
    }

    @Test
    public void testStackWithCommonInfra() {
        App app = new App();
        Environment environment = Environment.makeEnv("123", "eu", "dev");
        StackResourcesProvider stackResourcesProvider = new StackResourcesProvider();
        // simulate tagging here
        Tags.of(app).add("environment", environment.name());
        CommonStack commonStack = new CommonStack(app, "test-infra", environment, stackResourcesProvider);
        LoadbalancerStack stack = new LoadbalancerStack(app, "test-lb", environment, stackResourcesProvider);
        Template template = TemplateUtils.loadTemplates(stack, commonStack);
        CdkStackAssert.assertThat(template)
                .containsElasticLoadBalancing()
                .isDeletionProtection(false)
                .isScheme("internet-facing")
                .containsTagsExactly(List.of(new AbstractMap.SimpleEntry<>("environment", "dev")))
                .and()
                .containsSecurityGroup()
                .containsSecurityGroupIngress(List.of(new SecurityGroupIngressProps("0.0.0.0/0", "tcp", 80, 80)))
                .hasVpcIdImportValue("test-infra:ExportsOutputRefVpc8378EB38272D6E3A")
                .containsTagsExactly(List.of(new AbstractMap.SimpleEntry<>("environment", "dev")));
    }
}
