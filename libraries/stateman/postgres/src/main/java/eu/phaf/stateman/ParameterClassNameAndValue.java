package eu.phaf.stateman;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public record ParameterClassNameAndValue(String className, @JsonSerialize(using = EscapedJsonSerializer.class) @JsonDeserialize(using = EscapedJsonDeserializer.class) Object value) {
}
