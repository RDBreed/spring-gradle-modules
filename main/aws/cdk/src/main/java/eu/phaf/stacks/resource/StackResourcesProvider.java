package eu.phaf.stacks.resource;

import eu.phaf.environments.Environment;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.ICluster;
import software.amazon.awscdk.services.elasticloadbalancingv2.*;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.constructs.Construct;

/**
 * Provider resources class to provide resources used in other stacks.
 * The resources' information (like an ARN or ID) are stored in SSM parameter store.
 */
public class StackResourcesProvider {
    private VpcResource vpcResource = VpcResource.nullResource();
    private ClusterResource clusterResource = ClusterResource.nullResource();
    private HostedZoneResource hostedZoneResource = HostedZoneResource.nullResource();
    private LoadbalancerResource loadbalancerResource = LoadbalancerResource.nullResource();
    private EcrResource ecrResource = EcrResource.nullResource();

    public void setVpcResource(final Construct scope, final IVpc vpc, final Environment props) {
        vpcResource = VpcResource.of(scope, vpc, props);
    }

    public void setClusterResource(final Construct scope, final ICluster cluster, final Environment environment) {
        clusterResource = ClusterResource.of(scope, cluster, environment);
    }

    public VpcResource getVpcResource() {
        return vpcResource;
    }

    public ClusterResource getClusterResource() {
        return clusterResource;
    }

    public void setHostedZoneResource(final Construct scope, IHostedZone hostedZone, Environment environment) {
        hostedZoneResource = HostedZoneResource.of(scope, hostedZone, environment);
    }

    public HostedZoneResource getHostedZoneResource() {
        return hostedZoneResource;
    }

    public void setLoadbalancerResource(final Construct scope,
                                        final IApplicationLoadBalancer applicationLoadBalancer,
                                        IApplicationListener applicationListener,
                                        ISecurityGroup firstSecurityGroup,
                                        Environment environment) {
        loadbalancerResource = LoadbalancerResource.of(scope, applicationLoadBalancer, applicationListener, firstSecurityGroup, environment);
    }

    public LoadbalancerResource getLoadbalancerResource() {
        return loadbalancerResource;
    }

    public void setEcrResource(final Construct scope, Repository repository, Environment props) {
        ecrResource = EcrResource.of(scope, repository, props);
    }

    public EcrResource getEcrResource() {
        return ecrResource;
    }
}
