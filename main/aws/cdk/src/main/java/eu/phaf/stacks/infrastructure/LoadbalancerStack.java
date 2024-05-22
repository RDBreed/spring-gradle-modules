package eu.phaf.stacks.infrastructure;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.ISecurityGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.*;
import software.constructs.Construct;

public class LoadbalancerStack extends Stack {

    public LoadbalancerStack(final Construct scope, final String id, final Environment environment, final StackResourcesProvider stackResourcesProvider) {
        super(scope, id, environment.getStackProps());
        // public load balancer
        ApplicationLoadBalancer applicationLoadBalancer = new ApplicationLoadBalancer(this, "ApplicationLoadBalancer",
                ApplicationLoadBalancerProps.builder()
                        .vpc(stackResourcesProvider.getVpcResource().vpc(this, environment))
                        // public load balancer
                        .internetFacing(true)
                        .build());
        // get the automatically created security group
        ISecurityGroup firstSecurityGroup = applicationLoadBalancer.getConnections().getSecurityGroups().getFirst();
        ApplicationListener httpListener = applicationLoadBalancer.addListener("HttpListener", BaseApplicationListenerProps.builder()
                .protocol(ApplicationProtocol.HTTP)
                .port(80)
                .open(true)
                .build());
        httpListener.addAction("DefaultAction",
                AddApplicationActionProps.builder()
                        .action(ListenerAction.fixedResponse(404,
                                FixedResponseOptions.builder()
                                        .contentType("text/plain")
                                        .messageBody("Cannot route your request; no matching project found.")
                                        .build()))
                        .build());
        stackResourcesProvider.setLoadbalancerResource(this, applicationLoadBalancer, httpListener, firstSecurityGroup, environment);
    }
}
