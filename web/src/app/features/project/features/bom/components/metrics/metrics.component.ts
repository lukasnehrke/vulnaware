import { Component, Input } from "@angular/core";
import { History, Metrics } from "../../../../../../shared/models/bom.model";

@Component({
    selector: "va-project-bom-metrics",
    templateUrl: "./metrics.component.html",
    styleUrls: ["./metrics.component.scss"],
})
export class MetricsComponent {
    @Input() public metrics: Metrics;
}
