package eu.phaf.environments;

import software.amazon.awscdk.StackProps;

public record Environment(String account, String region, String name) {

    public static Environment makeEnv() {
        return makeEnv(null, null, "");
    }

    public static Environment makeEnv(String account, String region, String name) {
        return new Environment(getAccount(account), getRegion(region), getName(name));
    }

    private static String getName(String name) {
        return name != null && !name.isEmpty() ? name : System.getenv("CDK_DEPLOY_NAME");
    }

    private static String getAccount(String account) {
        return account != null ? account : (System.getenv("CDK_DEPLOY_ACCOUNT") != null ? System.getenv("CDK_DEPLOY_ACCOUNT") : System.getenv("CDK_DEFAULT_ACCOUNT"));
    }

    private static String getRegion(String region) {
        return region != null ? region : (System.getenv("CDK_DEPLOY_REGION") != null ? System.getenv("CDK_DEPLOY_REGION") : System.getenv("CDK_DEFAULT_REGION"));
    }

    public StackProps getStackProps() {
        return StackProps.builder().env(software.amazon.awscdk.Environment.builder()
                        .account(account)
                        .region(region)
                        .build())
                .build();
    }
}
