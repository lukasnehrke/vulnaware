package dev.lukasnehrke.vulnaware.bom.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComponentRisk implements Comparable<ComponentRisk> {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long vulnerabilityId;

    private double risk;

    @Override
    public int compareTo(final ComponentRisk other) {
        return Double.compare(this.risk, other.risk);
    }
}
