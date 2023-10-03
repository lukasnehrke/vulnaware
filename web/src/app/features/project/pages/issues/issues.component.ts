import { Component, OnDestroy, OnInit, signal, ViewEncapsulation } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { Project } from "../../../../shared/models/project.model";
import { Vulnerability } from "../../../../shared/models/vulnerability.model";
import { VulnerabilityService } from "../../../../shared/services/vulnerability.service";
import { catchError, combineLatest, debounceTime, filter, of, Subscription, switchMap } from "rxjs";
import { PageEvent } from "@angular/material/paginator";
import { FormControl, FormGroup } from "@angular/forms";
import { AppService } from "../../../../shared/services/app.service";

@Component({
    selector: "va-project-issues",
    encapsulation: ViewEncapsulation.None,
    templateUrl: "./issues.component.html",
    styleUrls: ["./issues.component.scss"],
})
export class IssuesComponent implements OnInit, OnDestroy {
    project: Project | null;
    tag?: string;

    form = new FormGroup({
        keyword: new FormControl(""),
        open: new FormControl(false),
        resolved: new FormControl(false),
        ignored: new FormControl(false),
        critical: new FormControl(false),
        high: new FormControl(false),
        medium: new FormControl(false),
        low: new FormControl(false),
    });

    loading = true;
    vulns: Vulnerability[] = [];
    totalVulns = 0;
    page = signal(0);
    pageSize = signal(5);

    _subForm: Subscription;
    _subRoute: Subscription;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private appService: AppService,
        private vulnService: VulnerabilityService,
    ) {}

    ngOnInit(): void {
        /* append form data to query params */
        this._subForm = this.form.valueChanges.pipe(debounceTime(300)).subscribe((value) => {
            const status = [];
            if (value.open) status.push("open");
            if (value.resolved) status.push("resolved");
            if (value.ignored) status.push("ignored");

            const severity = [];
            if (value.critical) severity.push("critical");
            if (value.high) severity.push("high");
            if (value.medium) severity.push("medium");
            if (value.low) severity.push("low");

            return this.router.navigate([], {
                relativeTo: this.route,
                queryParamsHandling: "merge",
                queryParams: {
                    keyword: value.keyword ? value.keyword : undefined,
                    status: status.length === 0 ? undefined : status.join(","),
                    severity: severity.length === 0 ? undefined : severity.join(","),
                },
            });
        });

        this._subRoute = combineLatest([this.route.queryParamMap, this.appService.activeProject$, this.appService.activeBom$])
            .pipe(
                filter(([params, project]) => !!project),
                switchMap(([params, project, bom]) => {
                    this.page.set(params.get("page") ? parseInt(params.get("page")!) : 0);
                    this.pageSize.set(params.get("pageSize") ? parseInt(params.get("pageSize")!) : 5);

                    const keyword = params.get("keyword") ?? "";
                    const status = params.get("status")?.split(",") ?? [];
                    const severity = params.get("severity")?.split(",") ?? [];

                    return this.vulnService
                        .findAll({
                            project: project!.slug,
                            tag: bom?.tag,
                            page: this.page(),
                            pageSize: this.pageSize(),
                            keyword: keyword,
                            status: status,
                            severity: severity,
                        })
                        .pipe(catchError(() => of(null)));
                }),
            )
            .subscribe((page) => {
                if (!page) return;
                this.totalVulns = page.totalItems;
                this.vulns = page.vulnerabilities;
                this.loading = false;
            });
    }

    ngOnDestroy(): void {
        this._subForm.unsubscribe();
        this._subRoute.unsubscribe();
    }

    async handlePageEvent(e: PageEvent) {
        await this.router.navigate([], {
            relativeTo: this.route,
            queryParams: { page: e.pageIndex, pageSize: e.pageSize },
            queryParamsHandling: "merge",
        });
    }
}
