package dev.lukasnehrke.vulnaware.advisory.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lukasnehrke.vulnaware.advisory.service.NvdMatcher;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@DiscriminatorValue("NVD")
@EqualsAndHashCode(callSuper = true)
public class NvdAdvisory extends Advisory {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Column(name = "raw_nvd_data", columnDefinition = "TEXT")
    @JsonIgnore
    private String nvdData;

    @JsonProperty("nvd")
    public JsonNode asJSON() throws JsonProcessingException {
        return mapper.readTree(nvdData);
    }

    @Override
    public List<Component> isAffected(final List<Component> components) {
        try {
            return new NvdMatcher(this).matchAgainst(components);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
