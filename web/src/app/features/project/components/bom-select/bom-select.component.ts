import { Component, Input, OnDestroy, OnInit, ViewEncapsulation } from "@angular/core";
import { MatButtonModule } from "@angular/material/button";
import { MatMenuModule } from "@angular/material/menu";
import { MatIconModule } from "@angular/material/icon";
import { NgForOf, NgIf } from "@angular/common";
import { MatSelectModule } from "@angular/material/select";
import { ReactiveFormsModule } from "@angular/forms";
import { MatInputModule } from "@angular/material/input";
import { AppService } from "../../../../shared/services/app.service";
import { Bom } from "../../../../shared/models/bom.model";
import { combineLatest, filter, of, Subscription, switchMap } from "rxjs";
import { ActivatedRoute, Router } from "@angular/router";
import { BomService } from "../../../../shared/services/bom.service";
import { MatDialog } from "@angular/material/dialog";
import { BomSelectDialogComponent } from "../bom-select-dialog/bom-select-dialog.component";
import { MatListModule } from "@angular/material/list";

@Component({
    standalone: true,
    encapsulation: ViewEncapsulation.None,
    selector: "va-bom-select",
    templateUrl: "./bom-select.component.html",
    styleUrls: ["./bom-select.component.scss"],
    imports: [MatIconModule, MatButtonModule, MatMenuModule, NgIf, NgForOf, MatSelectModule, ReactiveFormsModule, MatInputModule, MatListModule],
})
export class BomSelectComponent implements OnInit, OnDestroy {
    @Input() force = false;
    bom: Bom | null;

    private sub = new Subscription();

    constructor(
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private router: Router,
        private appService: AppService,
        private bomService: BomService,
    ) {}

    onClick(event: MouseEvent) {
        event.preventDefault();
        event.stopPropagation();
        this.dialog.open(BomSelectDialogComponent);
    }

    ngOnInit(): void {
        this.sub.add(
            combineLatest([this.route.queryParamMap, this.appService.activeProject$])
                .pipe(
                    filter(([params, project]) => !!project),
                    switchMap(([params, project]) => {
                        const tag = params.get("tag");

                        if (!project!.main) {
                            this.router.navigate(["/projects", project!.slug, "intro"]);
                            return of(null);
                        }

                        if (!tag && this.force) {
                            // append default tag to url
                            this.router.navigate([], {
                                relativeTo: this.route,
                                queryParamsHandling: "merge",
                                queryParams: {
                                    tag: project!.main.tag,
                                },
                            });
                            return of(null);
                        }

                        if (!tag) {
                            return of(null);
                        }

                        return this.bomService.find(project!.slug, tag);
                    }),
                )
                .subscribe((bom) => {
                    this.bom = bom;
                    this.appService.setActiveBom(bom);
                }),
        );
    }

    ngOnDestroy(): void {
        this.appService.setActiveBom(null);
        this.sub.unsubscribe();
    }
}
