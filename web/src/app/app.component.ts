import { Component, OnInit, ViewChild } from "@angular/core";
import { MatIconRegistry } from "@angular/material/icon";
import { NavItem } from "./shared/models/app.model";
import { AppService } from "./shared/services/app.service";
import { Project } from "./shared/models/project.model";
import { MatSidenav } from "@angular/material/sidenav";

@Component({
    selector: "app-root",
    templateUrl: "./app.component.html",
    styleUrls: ["./app.component.scss"],
})
export class AppComponent implements OnInit {
    project: Project | null;
    navItems: NavItem[] = [];

    showMenu = false;
    showSidebar = true;

    @ViewChild(MatSidenav, { static: true })
    sidenav: MatSidenav;

    constructor(
        private appService: AppService,
        private iconRegistry: MatIconRegistry,
    ) {}

    ngOnInit(): void {
        this.iconRegistry.setDefaultFontSetClass("material-icons-outlined");
        this.appService.showSideBar$.subscribe((showSidebar) => (this.showSidebar = showSidebar));
        this.appService.getNavItems().subscribe((items) => (this.navItems = items));
        this.appService.activeProject$.subscribe((project) => (this.project = project));
    }

    get isSideNavOpen(): boolean {
        return this.navItems.length > 0 && this.showSidebar;
    }
}
