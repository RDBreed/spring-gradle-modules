package eu.phaf.stacks.infrastructure;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.elasticloadbalancingv2.IApplicationLoadBalancer;
import software.amazon.awscdk.services.route53.CnameRecord;
import software.amazon.awscdk.services.route53.CnameRecordProps;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.constructs.Construct;

public class ApplicationLoadBalancedServiceRecordStack extends Stack {

    public ApplicationLoadBalancedServiceRecordStack(final Construct scope, final String id, final Environment environment,
                                                     final IHostedZone iHostedZone,
                                                     final IApplicationLoadBalancer applicationLoadBalancer) {
        super(scope, id, environment.getStackProps());
        new CnameRecord(this, "LoadbalancerCnameRecord",
                CnameRecordProps.builder()
                        .zone(iHostedZone)
                        // The subdomain name for this record. This should be relative to the zone root name.
                        .recordName(".")
                        .domainName(applicationLoadBalancer.getLoadBalancerDnsName())
                        .build());
    }

    public ApplicationLoadBalancedServiceRecordStack(Construct scope, String id, Environment environment, StackResourcesProvider stackResourcesProvider) {
        super(scope, id, environment.getStackProps());
        new CnameRecord(this, "LoadbalancerCnameRecord",
                CnameRecordProps.builder()
                        .zone(stackResourcesProvider.getHostedZoneResource().hostedZone(this, environment))
                        // The subdomain name for this record. This should be relative to the zone root name.
                        .recordName(".")
                        .domainName(stackResourcesProvider.getLoadbalancerResource().applicationLoadBalancer().getLoadBalancerDnsName())
                        .build());
    }
}
