import { Component, Input } from "@angular/core";
import { IsActiveMatchOptions } from "@angular/router";

@Component({
    selector: "nav-item",
    templateUrl: "./nav-item.component.html",
    styleUrls: ["./nav-item.component.scss"],
})
export class NavItemComponent {
    @Input() icon: string = "";
    @Input() title: string = "";
    @Input() link: string = "";
    @Input() query?: object = {};

    activeOptions: IsActiveMatchOptions = {
        matrixParams: "ignored",
        queryParams: "ignored",
        fragment: "ignored",
        paths: "subset",
    };
}
