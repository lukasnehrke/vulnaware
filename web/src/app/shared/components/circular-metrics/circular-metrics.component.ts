import { Component, Input } from "@angular/core";
import { Metrics } from "../../models/bom.model";
import { MatTooltipModule } from "@angular/material/tooltip";

@Component({
    standalone: true,
    selector: "va-circular-metrics",
    templateUrl: "./circular-metrics.component.html",
    styleUrls: ["./circular-metrics.component.scss"],
    imports: [MatTooltipModule],
})
export class CircularMetricsComponent {
    @Input() metrics: Metrics | null;
}
