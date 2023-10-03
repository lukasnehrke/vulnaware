import { Component, Input, ViewEncapsulation } from "@angular/core";
import { BadgeComponent } from "../badge/badge.component";
import { NgIf } from "@angular/common";
import { MatTooltipModule } from "@angular/material/tooltip";

@Component({
    standalone: true,
    encapsulation: ViewEncapsulation.None,
    selector: "va-risk-badge",
    templateUrl: "./risk-badge.component.html",
    styleUrls: ["./risk-badge.component.scss"],
    imports: [BadgeComponent, NgIf, MatTooltipModule],
})
export class RiskBadgeComponent {
    @Input() risk: number;

    getText() {
        if (this.risk == null || this.risk < 0) {
            return "Unknown";
        }
        if (this.risk < 4.0) {
            return "Low";
        }
        if (this.risk < 7.0) {
            return "Medium";
        }
        if (this.risk < 9.0) {
            return "High";
        }
        return "Critical";
    }
}
