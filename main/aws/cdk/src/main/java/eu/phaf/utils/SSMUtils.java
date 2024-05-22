package eu.phaf.utils;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ssm.StringParameter;
import software.amazon.awscdk.services.ssm.StringParameterProps;
import software.constructs.Construct;

/**
 * Get system store manager parameters and create them.
 * See also https://lzygo1995.medium.com/how-to-share-information-between-stacks-through-ssm-parameter-store-in-cdk-1a64e4e9d83a
 */
public class SSMUtils {
    private SSMUtils() {
    }

    public static String getSSMToken(final Stack scope, final String id, final String parameterName) {
        return StringParameter.fromStringParameterName(scope, id, parameterName).getStringValue();
    }

    public static String getSSMValue(final Stack scope, final String parameterName) {
        return StringParameter.valueFromLookup(scope, parameterName);
    }

    public static void createSSMValue(final Construct scope, final String id, final String parameterName, final String value) {
        new StringParameter(scope, id, StringParameterProps.builder().parameterName(parameterName).stringValue(value).build());
    }

    public static String createParameterName(final String environmentName, final String name) {
        return "/" + environmentName + "/resources/" + name;
    }
}