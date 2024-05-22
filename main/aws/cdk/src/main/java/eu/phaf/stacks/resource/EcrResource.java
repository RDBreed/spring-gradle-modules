package eu.phaf.stacks.resource;

import eu.phaf.environments.Environment;
import eu.phaf.utils.SSMUtils;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ecr.IRepository;
import software.amazon.awscdk.services.ecr.Repository;
import software.constructs.Construct;

public record EcrResource(IRepository repository) {
    public static final String REPOSITORY_SSM_VALUE = "containers/repository-arn";

    public static EcrResource of(final Construct scope, final IRepository repository, final Environment props) {
        SSMUtils.createSSMValue(
                scope,
                "RepositorySSM",
                SSMUtils.createParameterName(props.name(), REPOSITORY_SSM_VALUE),
                repository.getRepositoryArn());
        return new EcrResource(repository);
    }

    public static EcrResource nullResource() {
        return new EcrResource(null);
    }

    public IRepository repository(final Stack stack, final Environment environment) {
        return repository != null ?
                repository :
                Repository.fromRepositoryArn(stack, "RepositoryFromSSM",
                        SSMUtils.getSSMValue(stack,
                                SSMUtils.createParameterName(environment.name(), REPOSITORY_SSM_VALUE)));

    }
}
