package eu.phaf.stacks.containers;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ecs.ICluster;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationTargetGroup;
import software.constructs.Construct;

/**
 * Example how to deploy new application versions to ecr & ecs directly by IaC/CDK.
 * This will create a docker image from our application and push it to a ecr repository created by cdk
 */
public class ServiceStack extends Stack {
//    private final ApplicationTargetGroup ecsTargetGroup;
    public ServiceStack(final Construct scope, final String id, final Environment environment, ICluster cluster, IVpc vpc, IApplicationTargetGroup targetGroup) {
        super(scope, id, environment.getStackProps());
//        ecsTargetGroup = httpListener.addTargets("ECSTargetHttp", AddApplicationTargetsProps.builder()
//                .protocol(ApplicationProtocol.HTTP)
//                .build());
//         TODO!!!
//        targetGroup.addTarget(new FargateService(null, null, FargateServiceProps.builder().build()));
    }

    public ServiceStack(final Construct scope, final String id, Environment environment, StackResourcesProvider stackResourcesProvider) {
        super(scope, id, environment.getStackProps());

    }
}
