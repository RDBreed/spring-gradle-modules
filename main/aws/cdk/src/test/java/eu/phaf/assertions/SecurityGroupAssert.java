package eu.phaf.assertions;

import org.assertj.core.api.Assertions;

import java.util.List;
import java.util.Map;

public class SecurityGroupAssert extends CdkResourceAbstractAssert<SecurityGroupAssert, Map<String, Object>> {

    private SecurityGroupAssert(Map<String, Object> stringObjectMap) {
        super(stringObjectMap, SecurityGroupAssert.class);
    }

    public static SecurityGroupAssert assertThat(Map<String, Object> value) {
        return new SecurityGroupAssert(value);
    }

    public SecurityGroupAssert containsSecurityGroupIngress(List<SecurityGroupIngressProps> propsList) {
        List<Map<String, Object>> securityGroupIngressList = (List<Map<String, Object>>) properties.get("SecurityGroupIngress");
        Assertions.assertThat(securityGroupIngressList)
                .map(maps -> new SecurityGroupIngressProps(
                        (String) maps.get("CidrIp"),
                        (String) maps.get("IpProtocol"),
                        (Integer) maps.get("FromPort"),
                        (Integer) maps.get("ToPort")))
                .containsAll(propsList);
        return this;
    }

    public SecurityGroupAssert hasVpcId(String vpcId) {
        String vpcIdActual = (String) properties.get("VpcId");
        Assertions.assertThat(vpcIdActual).isEqualTo(vpcId);
        return this;
    }

    public SecurityGroupAssert hasVpcIdImportValue(String vpcId) {
        Map<String, String> vpcMap = (Map<String, String>) properties.get("VpcId");
        Assertions.assertThat(vpcMap.get("Fn::ImportValue")).isEqualTo(vpcId);
        return this;
    }

    public record SecurityGroupIngressProps(String cidrIp, String ipProtocol, Integer fromPort, Integer toPort) {

    }
}
