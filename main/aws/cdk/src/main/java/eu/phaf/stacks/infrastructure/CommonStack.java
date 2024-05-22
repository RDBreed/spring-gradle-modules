package eu.phaf.stacks.infrastructure;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

public class CommonStack extends Stack {

    /**
     * Instantiates a new Shared infra stack.
     *
     * @param scope                  the scope
     * @param id                     the id
     * @param props                  the props
     * @param stackResourcesProvider
     */
    public CommonStack(final Construct scope, final String id, final Environment props, StackResourcesProvider stackResourcesProvider) {
        super(scope, id, props.getStackProps());
        IVpc vpc = Vpc.Builder.create(this, "Vpc")
                // will create a private & public subnet per az...
                .maxAzs(1)
                .build();
        stackResourcesProvider.setVpcResource(this, vpc, props);
    }
}
