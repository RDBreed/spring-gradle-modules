package eu.phaf.constructs;

import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateServiceProps;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.constructs.Construct;

import java.util.Optional;

public class SpringContainerService extends Construct {
    private final FargateService applicationService;

    public SpringContainerService(Construct scope, String id, SpringContainerServiceProperties springContainerServiceProperties) {
        super(scope, id);
        //this will push the image to a by cdk determined repo, not in our hands...
        AssetImage assetImage = ContainerImage.fromAsset(".",
                AssetImageProps.builder()
                        .file("src/main/docker/Dockerfile")
                        .build());

        FargateServiceProps.Builder builder = FargateServiceProps.builder()
                .cluster(springContainerServiceProperties.iCluster());
//                .taskDefinition();
//                .taskImageOptions(
//                        ApplicationLoadBalancedTaskImageOptions.builder()
//                                .image(assetImage)
//                                .containerPort(springContainerServiceProperties.containerPort())
//                                .build());
        // all other options
        if (springContainerServiceProperties.assignPublicIp()) builder.assignPublicIp(true);
//        if (springContainerServiceProperties.publicLoadBalancer()) builder.publicLoadBalancer(true);
//        springContainerServiceProperties.cpu().ifPresent(builder::cpu);
//        springContainerServiceProperties.memoryLimitMiB().ifPresent(builder::memoryLimitMiB);
        springContainerServiceProperties.desiredCount().ifPresent(builder::desiredCount);
//        springContainerServiceProperties.listenerPort().ifPresent(builder::listenerPort);

        this.applicationService = new FargateService(this, "LoadBalancedFargateService",
                builder.build());

    }

    public record SpringContainerServiceProperties(ICluster iCluster,
                                                   Integer containerPort,
                                                   boolean assignPublicIp,
                                                   boolean publicLoadBalancer,
                                                   Optional<Integer> cpu,
                                                   Optional<Integer> desiredCount,
                                                   Optional<Integer> memoryLimitMiB

    ) {

    }
}
