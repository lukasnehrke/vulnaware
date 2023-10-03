import { AfterViewInit, Component, Input, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { catchError, map, merge, of, startWith, switchMap } from "rxjs";
import { FormControl } from "@angular/forms";
import { Component as BomComponent } from "../../../../../../shared/models/bom.model";
import { BomService } from "../../../../../../shared/services/bom.service";
import { VulnerabilityService } from "../../../../../../shared/services/vulnerability.service";

@Component({
    selector: "va-project-bom-table",
    templateUrl: "./bom-table.component.html",
    styleUrls: ["./bom-table.component.scss"],
})
export class BomTableComponent implements AfterViewInit {
    @Input() project: string | undefined = "";
    @Input() tag?: string | undefined = "";
    @ViewChild(MatPaginator) paginator: MatPaginator;

    view = new FormControl("list");

    displayedColumns: string[] = ["ecosystem", "name", "version", "license", "risk", "homepage"];
    filter = new FormControl("");
    components: BomComponent[] = [];
    length: number = 0;

    constructor(
        private bomService: BomService,
        private vulnService: VulnerabilityService,
    ) {}

    get isTreeView() {
        return this.view.value === "tree";
    }

    ngAfterViewInit(): void {
        merge(this.filter.valueChanges, this.paginator.page)
            .pipe(
                startWith({}),
                switchMap(() => {
                    return this.getComponents(this.filter.value ?? "", this.paginator.pageIndex, this.paginator.pageSize).pipe(
                        catchError(() => of(null)),
                    );
                }),
                map((result) => {
                    if (result === null) return [];
                    this.length = result.totalElements;
                    return result.content;
                }),
            )
            .subscribe((data) => (this.components = data));
    }

    toFriendlyEcosystem(ecosystem: string) {
        return this.bomService.toFriendlyEcosystem(ecosystem);
    }

    getSeverity(risk: number) {
        return this.vulnService.getSeverity(risk);
    }

    getComponents(filter: string, page: number, size: number) {
        return this.bomService.listComponents(filter, page, size);
    }

    getHomepage(component: BomComponent) {
        return component.package?.links.find((link) => link.label === "HOMEPAGE");
    }

    getIssueTracker(component: BomComponent) {
        return component.package?.links.find((link) => link.label === "ISSUE_TRACKER");
    }

    getOrigin(component: BomComponent) {
        return component.package?.links.find((link) => link.label === "ORIGIN");
    }

    getLicenses(component: BomComponent) {
        return component.package?.licenses?.join(", ");
    }
}
