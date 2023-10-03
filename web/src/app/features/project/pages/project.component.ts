import { Component, OnDestroy, OnInit } from "@angular/core";
import { NavItem } from "../../../shared/models/app.model";
import { AppService } from "../../../shared/services/app.service";
import { Project } from "../../../shared/models/project.model";
import { of, Subscription, switchMap } from "rxjs";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { ProjectService } from "../../../shared/services/projects.service";

const buildNavItems = (project: Project): NavItem[] => {
    const items: NavItem[] = [];

    if (!project.main) {
        items.push({
            icon: "home",
            title: "Getting Started",
            url: `/projects/${project.slug}/intro`,
        });
    } else {
        items.push(
            {
                icon: "report",
                title: "Issues",
                url: `/projects/${project.slug}/issues`,
            },
            {
                icon: "grid_view",
                title: "BOM",
                url: `/projects/${project.slug}/bom`,
                query: { tag: project.main?.tag },
            },
        );
    }

    return [
        ...items,
        { title: "Activity", url: `/projects/${project.slug}/activity`, icon: "reorder" },
        { title: "Settings", url: `/projects/${project.slug}/settings`, icon: "settings" },
    ];
};

@Component({
    selector: "va-project",
    templateUrl: "./project.component.html",
})
export class ProjectComponent implements OnInit, OnDestroy {
    private sub: Subscription;

    constructor(
        private route: ActivatedRoute,
        private appService: AppService,
        private projectService: ProjectService,
    ) {}

    ngOnInit(): void {
        this.sub = this.route.paramMap
            .pipe(
                switchMap((params) => {
                    const slug = params.get("slug");
                    if (!slug) return of(null);
                    return this.projectService.find(slug);
                }),
            )
            .subscribe((project) => {
                this.appService.setActiveProject(project);
                this.appService.setNavItems(project ? buildNavItems(project) : []);
            });
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
        this.appService.setNavItems([]);
        this.appService.setActiveProject(null);
    }
}
