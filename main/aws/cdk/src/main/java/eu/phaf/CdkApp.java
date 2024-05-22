package eu.phaf;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import eu.phaf.stacks.containers.ClusterStack;
import eu.phaf.stacks.containers.EcrRepositoryStack;
import eu.phaf.stacks.containers.ServiceStack;
import eu.phaf.stacks.infrastructure.CommonStack;
import eu.phaf.stacks.infrastructure.LoadbalancerStack;
import software.amazon.awscdk.App;
import software.amazon.awscdk.Tags;

public class CdkApp {
    public static void main(final String[] args) {
        App app = new App();

        Environment environment = Environment.makeEnv();
        StackResourcesProvider stackResourcesProvider = new StackResourcesProvider();
        CommonStack infraCommon = new CommonStack(app, "InfraCommonStack", environment, stackResourcesProvider);
        // wait a bit...
//        HostedZoneStack hostedZoneStack = new HostedZoneStack(app, "HostedZoneStack", environment, stackResourcesProvider);
        ClusterStack clusterStack = new ClusterStack(app, "ClusterStack", environment, stackResourcesProvider);
        EcrRepositoryStack ecrRepositoryStack = new EcrRepositoryStack(app, "EcrRepositoryStack", environment, stackResourcesProvider);
        LoadbalancerStack loadbalancerStack = new LoadbalancerStack(app, "LoadBalancerStack", environment, stackResourcesProvider);
//        ApplicationLoadBalancedServiceRecordStack applicationLoadBalancedServiceRecordStack = new ApplicationLoadBalancedServiceRecordStack(app, "ApplicationLoadBalancedServiceRecordStack", environment, stackResourcesProvider);
        ServiceStack serviceStack = new ServiceStack(
                app,
                "ServiceStack",
                environment,
                stackResourcesProvider
        );
        Tags.of(app).add("environment", environment.name());
        app.synth();
    }
}

