package dev.lukasnehrke.vulnaware.util.schema.nvd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CVE {

    @JsonProperty("id")
    String id;

    @JsonProperty("configurations")
    List<Configuration> configurations;
}
