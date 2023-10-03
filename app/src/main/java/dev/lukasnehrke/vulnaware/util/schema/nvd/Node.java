package dev.lukasnehrke.vulnaware.util.schema.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node {

    @JsonProperty("operator")
    String operator = "OR";

    @JsonProperty("negate")
    boolean negate = false;

    @JsonProperty("cpeMatch")
    List<Match> cpeMatch;
}
