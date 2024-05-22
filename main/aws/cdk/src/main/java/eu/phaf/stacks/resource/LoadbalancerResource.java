package eu.phaf.stacks.resource;

import eu.phaf.environments.Environment;
import eu.phaf.utils.SSMUtils;
import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.ISecurityGroup;
import software.amazon.awscdk.services.ec2.SecurityGroup;
import software.amazon.awscdk.services.elasticloadbalancingv2.*;
import software.constructs.Construct;

public record LoadbalancerResource(IApplicationLoadBalancer applicationLoadBalancer,
                                   IApplicationListener applicationListener, ISecurityGroup firstSecurityGroup) {
    private static final String PUBLIC_LOAD_BALANCER_ARN_SSM_VALUE = "public-load-balancer-arn";
    private static final String PUBLIC_LOAD_BALANCER_DNS_SSM_VALUE = "public-load-balancer-dns";
    private static final String SECURITY_GROUP_LOAD_BALANCER_ID_SSM_VALUE = "security-group-load-balancer-id";
    private static final String ECS_LISTENER_ARN_SSM_VALUE = "ecs-listener-arn";

    public static LoadbalancerResource of(final Construct scope, final IApplicationLoadBalancer applicationLoadBalancer, IApplicationListener applicationListener, ISecurityGroup firstSecurityGroup, Environment environment) {
        SSMUtils.createSSMValue(scope, "LoadBalancerArnSSM", SSMUtils.createParameterName(environment.name(), PUBLIC_LOAD_BALANCER_ARN_SSM_VALUE), applicationLoadBalancer.getLoadBalancerArn());
        SSMUtils.createSSMValue(scope, "LoadBalancerDNSSSM", SSMUtils.createParameterName(environment.name(), PUBLIC_LOAD_BALANCER_DNS_SSM_VALUE), applicationLoadBalancer.getLoadBalancerDnsName());
        new CfnOutput(scope, "LoadBalancerDNSOutput", CfnOutputProps.builder().value(applicationLoadBalancer.getLoadBalancerDnsName()).build());
        SSMUtils.createSSMValue(scope, "LoadBalancerSecurityGroupArnSSM", SSMUtils.createParameterName(environment.name(), SECURITY_GROUP_LOAD_BALANCER_ID_SSM_VALUE), firstSecurityGroup.getSecurityGroupId());
        SSMUtils.createSSMValue(scope, "EcsTargetArnSSM", SSMUtils.createParameterName(environment.name(), ECS_LISTENER_ARN_SSM_VALUE), applicationListener.getListenerArn());
        return new LoadbalancerResource(applicationLoadBalancer, applicationListener, firstSecurityGroup);
    }

    public static LoadbalancerResource nullResource() {
        return new LoadbalancerResource(null, null, null);
    }

    public IApplicationLoadBalancer applicationLoadBalancer(final Stack stack, final Environment environment) {
        return applicationLoadBalancer == null ? ApplicationLoadBalancer.fromApplicationLoadBalancerAttributes(stack, "LoadBalancerFromSSM",
                ApplicationLoadBalancerAttributes.builder()
                        .loadBalancerArn(SSMUtils.getSSMValue(stack, SSMUtils.createParameterName(environment.name(), PUBLIC_LOAD_BALANCER_ARN_SSM_VALUE)))
                        .securityGroupId(SSMUtils.getSSMValue(stack, SSMUtils.createParameterName(environment.name(), SECURITY_GROUP_LOAD_BALANCER_ID_SSM_VALUE)))
                        .build()) : applicationLoadBalancer;
    }

    public IApplicationListener applicationListener(final Stack stack, final Environment environment) {
        return applicationListener == null ? ApplicationListener.fromApplicationListenerAttributes(stack, "ApplicationListenerFromSSM",
                ApplicationListenerAttributes.builder()
                        .listenerArn(SSMUtils.getSSMValue(stack, SSMUtils.createParameterName(environment.name(), ECS_LISTENER_ARN_SSM_VALUE)))
                        .securityGroup(SecurityGroup.fromSecurityGroupId(stack, "SecurityGroupFromSSM", SSMUtils.getSSMValue(stack,
                                SSMUtils.createParameterName(environment.name(), SECURITY_GROUP_LOAD_BALANCER_ID_SSM_VALUE))))
                        .build()) : applicationListener;
    }
}
