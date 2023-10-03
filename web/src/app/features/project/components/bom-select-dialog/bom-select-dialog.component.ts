import { Component, OnDestroy, OnInit } from "@angular/core";
import { MatDialogModule, MatDialogRef } from "@angular/material/dialog";
import { MatButtonModule } from "@angular/material/button";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatIconModule } from "@angular/material/icon";
import { MatInputModule } from "@angular/material/input";
import { MatListModule } from "@angular/material/list";
import { NgForOf } from "@angular/common";
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from "@angular/forms";
import { BomService } from "../../../../shared/services/bom.service";
import { AppService } from "../../../../shared/services/app.service";
import { combineLatest, filter, startWith, Subscription, switchMap } from "rxjs";
import { Bom } from "../../../../shared/models/bom.model";
import { ActivatedRoute, Router } from "@angular/router";

@Component({
    standalone: true,
    selector: "va-bom-select-dialog",
    templateUrl: "bom-select-dialog.component.html",
    styleUrls: ["bom-select-dialog.component.scss"],
    imports: [
        MatDialogModule,
        MatButtonModule,
        MatFormFieldModule,
        MatIconModule,
        MatInputModule,
        MatListModule,
        NgForOf,
        FormsModule,
        ReactiveFormsModule,
    ],
})
export class BomSelectDialogComponent implements OnInit, OnDestroy {
    boms: Bom[];
    filter = new FormControl("");
    bom = new FormControl("", [Validators.required]);

    private sub: Subscription;

    constructor(
        private dialog: MatDialogRef<BomSelectDialogComponent>,
        private route: ActivatedRoute,
        private router: Router,
        private appService: AppService,
        private bomService: BomService,
    ) {}

    ngOnInit(): void {
        this.sub = combineLatest([this.appService.activeProject$, this.appService.activeBom$, this.filter.valueChanges.pipe(startWith(null))])
            .pipe(
                switchMap(([project, bom, filter]) => {
                    return this.bomService.findAll(project!.slug, filter);
                }),
            )
            .subscribe((boms: any) => {
                this.boms = boms.content;
            });
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }

    onTagSelect() {
        if (this.bom.invalid) return;

        this.router.navigate([], {
            relativeTo: this.route,
            queryParamsHandling: "merge",
            queryParams: {
                tag: this.bom.value,
            },
        });

        this.dialog.close();
    }
}
