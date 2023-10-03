import { Component, Input } from "@angular/core";
import { NgClass } from "@angular/common";

@Component({
    standalone: true,
    selector: "va-badge",
    templateUrl: "./badge.component.html",
    styleUrls: ["./badge.component.scss"],
    imports: [NgClass],
})
export class BadgeComponent {
    @Input() text: string = "";
    @Input() type: string = "";
}
