package eu.phaf;

import eu.phaf.assertions.CdkStackAssert;
import eu.phaf.environments.Environment;
import eu.phaf.stacks.infrastructure.CommonStack;
import eu.phaf.stacks.resource.StackResourcesProvider;
import org.junit.jupiter.api.Test;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Tags;
import software.amazon.awscdk.assertions.Template;

import java.util.AbstractMap;
import java.util.List;

public class CommonStackTest {

    @Test
    public void testStack(){
        App app = new App();
        Environment environment = Environment.makeEnv("123", "eu", "dev");
        StackResourcesProvider stackResourcesProvider = new StackResourcesProvider();
        // simulate tagging here
        Tags.of(app).add("environment", environment.name());
        CommonStack stack = new CommonStack(app, "test-infra", environment, stackResourcesProvider);
        Template template = TemplateUtils.loadTemplates(stack);
        CdkStackAssert.assertThat(template)
                .containsVpc()
                .containsCidrBlock("10.0.0.0/16")
                .isDnsHostnamesEnabled(true)
                .containsTagsExactly(List.of(
                        new AbstractMap.SimpleEntry<>("environment", "dev"),
                        new AbstractMap.SimpleEntry<>("Name", "test-infra/Vpc")))
                .and();

    }
}
