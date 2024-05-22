package eu.phaf.stacks.infrastructure;

import eu.phaf.environments.Environment;
import eu.phaf.stacks.resource.StackResourcesProvider;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.route53.HostedZone;
import software.amazon.awscdk.services.route53.HostedZoneProps;
import software.constructs.Construct;

public class HostedZoneStack extends Stack {


    public HostedZoneStack(final Construct scope, final String id, final Environment props, StackResourcesProvider stackResourcesProvider) {
        super(scope, id, props.getStackProps());
        // https://docs.aws.amazon.com/Route53/latest/DeveloperGuide/MigratingSubdomain.html
        HostedZone hostedZone = new HostedZone(this, "HostedZone",
                HostedZoneProps.builder()
                        // relative from root domain
                        .zoneName(props.name() + ".aws")
                        .build());
        stackResourcesProvider.setHostedZoneResource(this, hostedZone, props);
    }
}
