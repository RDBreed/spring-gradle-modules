package eu.phaf.stacks.resource;

import eu.phaf.environments.Environment;
import eu.phaf.utils.SSMUtils;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.IHostedZone;
import software.constructs.Construct;

public record HostedZoneResource(IHostedZone hostedZone) {

    public static final String HOSTED_ZONE_ID_SSM_VALUE = "hosted-zone-id";

    public static HostedZoneResource of(final Construct scope, final IHostedZone hostedZone, final Environment environment) {
        SSMUtils.createSSMValue(scope, "HostedZoneIdSSM", SSMUtils.createParameterName(environment.name(), HOSTED_ZONE_ID_SSM_VALUE), hostedZone.getHostedZoneId());
        return new HostedZoneResource(hostedZone);
    }

    public static HostedZoneResource nullResource() {
        return new HostedZoneResource(null);
    }

    public IHostedZone hostedZone(final Stack stack, final Environment environment) {
        return hostedZone == null ?
                HostedZone.fromHostedZoneId(stack, "HostedZoneFromSSM", SSMUtils.getSSMValue(stack, SSMUtils.createParameterName(environment.name(), HOSTED_ZONE_ID_SSM_VALUE))) :
                hostedZone;
    }
}
