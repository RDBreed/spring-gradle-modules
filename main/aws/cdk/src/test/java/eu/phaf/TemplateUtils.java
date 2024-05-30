package eu.phaf;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.assertions.Template;

import java.util.Arrays;

public final class TemplateUtils {

    public static Template loadTemplates(Stack stackUnderTest, Stack... otherStacks) {
        if (otherStacks != null) {
            Arrays.stream(otherStacks).forEach(Template::fromStack);
        }
        return Template.fromStack(stackUnderTest);
    }
}
