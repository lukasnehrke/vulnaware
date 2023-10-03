package dev.lukasnehrke.vulnaware.advisory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.lukasnehrke.vulnaware.bom.model.Component;
import java.util.List;
import org.junit.jupiter.api.Test;

public class NvdMatcherTest {

    @Test
    public void matchExample() throws Exception {
        final var matcher = new NvdMatcher(
            "{\"cve\":{\"id\":\"CVE-2021-44228\",\"sourceIdentifier\":\"security@apache.org\",\"published\":\"2021-12-10T10:15:09.143\",\"lastModified\":\"2023-04-03T20:15:07.173\",\"vulnStatus\":\"Modified\",\"descriptions\":[],\"metrics\":{},\"weaknesses\":[],\"configurations\":[{\"nodes\":[{\"operator\":\"OR\",\"negate\":false,\"cpeMatch\":[{\"vulnerable\":true,\"criteria\":\"cpe:2.3:a:apache:log4j:*:*:*:*:*:*:*:*\",\"versionStartIncluding\":\"2.0.1\",\"versionEndExcluding\":\"2.3.1\",\"matchCriteriaId\":\"03FA5E81-F9C0-403E-8A4B-E4284E4E7B72\"},{\"vulnerable\":true,\"criteria\":\"cpe:2.3:a:apache:log4j:*:*:*:*:*:*:*:*\",\"versionStartIncluding\":\"2.4.0\",\"versionEndExcluding\":\"2.12.2\",\"matchCriteriaId\":\"AED3D5EC-DAD5-4E5F-8BBD-B4E3349D84FC\"},{\"vulnerable\":true,\"criteria\":\"cpe:2.3:a:apache:log4j:*:*:*:*:*:*:*:*\",\"versionStartIncluding\":\"2.13.0\",\"versionEndExcluding\":\"2.15.0\",\"matchCriteriaId\":\"D31D423D-FC4D-428A-B863-55AF472B80DC\"},{\"vulnerable\":true,\"criteria\":\"cpe:2.3:a:apache:log4j:2.0:-:*:*:*:*:*:*\",\"matchCriteriaId\":\"17854E42-7063-4A55-BF2A-4C7074CC2D60\"},{\"vulnerable\":true,\"criteria\":\"cpe:2.3:a:apache:log4j:2.0:beta9:*:*:*:*:*:*\",\"matchCriteriaId\":\"53F32FB2-6970-4975-8BD0-EAE12E9AD03A\"},{\"vulnerable\":true,\"criteria\":\"cpe:2.3:a:apache:log4j:2.0:rc1:*:*:*:*:*:*\",\"matchCriteriaId\":\"B773ED91-1D39-42E6-9C52-D02210DE1A94\"},{\"vulnerable\":true,\"criteria\":\"cpe:2.3:a:apache:log4j:2.0:rc2:*:*:*:*:*:*\",\"matchCriteriaId\":\"EF24312D-1A62-482E-8078-7EC24758B710\"}]}]},{\"operator\":\"AND\",\"nodes\":[{\"operator\":\"OR\",\"negate\":false,\"cpeMatch\":[{\"vulnerable\":true,\"criteria\":\"cpe:2.3:o:siemens:sppa-t3000_ses3000_firmware:*:*:*:*:*:*:*:*\",\"matchCriteriaId\":\"E8320869-CBF4-4C92-885C-560C09855BFA\"}]},{\"operator\":\"OR\",\"negate\":false,\"cpeMatch\":[{\"vulnerable\":false,\"criteria\":\"cpe:2.3:h:siemens:sppa-t3000_ses3000:-:*:*:*:*:*:*:*\",\"matchCriteriaId\":\"755BA221-33DD-40A2-A517-8574D042C261\"}]}]}],\"references\":[]}}"
        );

        final Component c1 = new Component();
        c1.setCpe("cpe:2.3:a:apache:log4j:2.0:-:*:*:*:*:*:*");

        final Component c2 = new Component();
        c2.setCpe("cpe:2.3:a:apache:log4j:2.0:beta9:*:*:*:*:*:*");

        final Component c3 = new Component();
        c3.setCpe("cpe:2.3:a:apache:log4j:2.0:beta10:*:*:*:*:*:*"); // not affected

        final var result = matcher.matchAgainst(List.of(c1, c2));
        for (var r : result) {
            System.out.println(r.getCpe());
        }

        assertEquals(2, result.size());
        assertEquals("cpe:2.3:a:apache:log4j:2.0:-:*:*:*:*:*:*", result.get(0).getCpe());
    }

    @Test
    public void testAnd() throws Exception {
        final var matcher = new NvdMatcher(
            "{\"cve\":{\"id\":\"CVE-2022-40982\",\"sourceIdentifier\":\"secure@intel.com\",\"published\":\"2023-08-11T03:15:14.823\",\"lastModified\":\"2023-08-27T03:15:07.597\",\"vulnStatus\":\"Modified\",\"descriptions\":[],\"metrics\":{},\"weaknesses\":[],\"configurations\":[{\"operator\":\"AND\",\"nodes\":[{\"operator\":\"OR\",\"negate\":false,\"cpeMatch\":[{\"vulnerable\":true,\"criteria\":\"cpe:2.3:o:intel:microcode:*:*:*:*:*:*:*:*\",\"versionEndExcluding\":\"20230808\",\"matchCriteriaId\":\"59DDC2D1-D21B-4CD3-87A0-3AF07336E504\"}]},{\"operator\":\"OR\",\"negate\":false,\"cpeMatch\":[{\"vulnerable\":false,\"criteria\":\"cpe:2.3:h:intel:core_i7-8550u:-:*:*:*:*:*:*:*\",\"matchCriteriaId\":\"1395788D-E23B-433A-B111-745C55018C68\"}]}]}],\"references\":[]}}"
        );

        final Component c1 = new Component();
        c1.setCpe("cpe:2.3:h:intel:core_i7-8550u:-:*:*:*:*:*:*:*");

        final Component c2 = new Component();
        c2.setCpe("cpe:2.3:o:intel:microcode:20230807:*:*:*:*:*:*:*");

        final var result = matcher.matchAgainst(List.of(c1, c2));
        assertEquals(1, result.size());
        assertEquals("cpe:2.3:o:intel:microcode:20230807:*:*:*:*:*:*:*", result.get(0).getCpe());
    }
}
