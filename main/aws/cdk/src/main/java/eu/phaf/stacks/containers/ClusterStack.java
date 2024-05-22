package eu.phaf.stacks.containers;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ClusterProps;
import software.constructs.Construct;

public class ClusterStack extends Stack {

    public ClusterStack(final Construct scope, final String id, final Environment environment, final StackResourcesProvider stackResourcesProvider) {
        super(scope, id, environment.getStackProps());
        Cluster cluster = new Cluster(this, "Cluster",
                ClusterProps.builder()
                        .vpc(stackResourcesProvider.getVpcResource().vpc(this, environment))
                        .build());
        stackResourcesProvider.setClusterResource(this, cluster, environment);
    }
}
