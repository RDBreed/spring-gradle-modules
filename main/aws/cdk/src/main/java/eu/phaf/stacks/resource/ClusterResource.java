package eu.phaf.stacks.resource;

import eu.phaf.environments.Environment;
import eu.phaf.utils.SSMUtils;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.amazon.awscdk.services.ecs.ClusterAttributes;
import software.amazon.awscdk.services.ecs.ICluster;
import software.constructs.Construct;

public record ClusterResource(ICluster cluster) {
    public static final String CLUSTER_ARN_SSM_VALUE = "containers/cluster-arn";
    public static final String CLUSTER_NAME_SSM_VALUE = "containers/cluster-name";

    public static ClusterResource of(final Construct scope, final ICluster cluster, final Environment environment) {
        SSMUtils
                .createSSMValue(scope, "ClusterArnSSM", SSMUtils.createParameterName(environment.name(), CLUSTER_ARN_SSM_VALUE),
                        cluster.getClusterArn());
        SSMUtils
                .createSSMValue(scope, "ClusterNameSSM", SSMUtils.createParameterName(environment.name(), CLUSTER_NAME_SSM_VALUE),
                        cluster.getClusterName());
        return new ClusterResource(cluster);
    }

    public static ClusterResource nullResource() {
        return new ClusterResource(null);
    }

    public ICluster cluster(final Stack stack, final Environment props, final IVpc vpc) {
        return cluster == null ?
                Cluster.fromClusterAttributes(
                        stack,
                        "ClusterFromSSM",
                        ClusterAttributes.builder()
                                .vpc(vpc)
                                .clusterName(SSMUtils.getSSMValue(stack, SSMUtils.createParameterName(props.name(), CLUSTER_NAME_SSM_VALUE)))
                                .build()
                ) : cluster;
    }
}
