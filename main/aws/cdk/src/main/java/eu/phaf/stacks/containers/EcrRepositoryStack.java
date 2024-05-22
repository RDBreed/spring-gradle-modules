package eu.phaf.stacks.containers;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ecr.Repository;
import software.constructs.Construct;

public class EcrRepositoryStack extends Stack {

    public EcrRepositoryStack(final Construct scope, final String id, final Environment props, StackResourcesProvider stackResourcesProvider) {
        super(scope, id, props.getStackProps());
        Repository repository = new Repository(this, "Repository");
        stackResourcesProvider.setEcrResource(this, repository, props);
    }
}
