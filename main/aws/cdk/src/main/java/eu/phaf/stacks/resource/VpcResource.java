package eu.phaf.stacks.resource;

import eu.phaf.environments.Environment;
import eu.phaf.utils.SSMUtils;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.IVpc;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ec2.VpcLookupOptions;
import software.constructs.Construct;

public record VpcResource(IVpc vpc) {
    public static final String VPC_ID_SSM_VALUE = "containers/vpc-id";

    public static VpcResource of(final Construct scope, final IVpc vpc, final Environment props) {
        SSMUtils.createSSMValue(scope, "VpcIdSSM", SSMUtils.createParameterName(props.name(), VPC_ID_SSM_VALUE), vpc.getVpcId());
        return new VpcResource(vpc);
    }

    public static VpcResource nullResource() {
        return new VpcResource(null);
    }

    public IVpc vpc(final Stack stack, final Environment props) {
        return vpc == null ? Vpc.fromLookup(
                stack,
                "VpcFromSSM",
                VpcLookupOptions.builder()
                        .vpcId(SSMUtils.getSSMValue(stack, SSMUtils.createParameterName(props.name(), VPC_ID_SSM_VALUE)))
                        .build()) : vpc;
    }
}
