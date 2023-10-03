import { Component, OnDestroy, OnInit } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { FormBuilder } from "@angular/forms";
import { AppService } from "../../../../../../shared/services/app.service";
import { BomService } from "../../../../../../shared/services/bom.service";
import { combineLatest, Subscription } from "rxjs";
import { Project } from "../../../../../../shared/models/project.model";
import { Bom } from "../../../../../../shared/models/bom.model";

@Component({
    selector: "va-project-bom-export-dialog",
    templateUrl: "./export-dialog.component.html",
    styleUrls: ["./export-dialog.component.scss"],
})
export class ExportDialogComponent implements OnInit, OnDestroy {
    form = this.formBuilder.group({
        format: ["spdx"],
        includeVulns: true,
    });

    project: Project | null;
    bom: Bom | null;
    _sub: Subscription;

    constructor(
        private dialog: MatDialogRef<ExportDialogComponent>,
        private formBuilder: FormBuilder,
        private appService: AppService,
        private bomService: BomService,
    ) {}

    ngOnInit(): void {
        this._sub = combineLatest([this.appService.activeProject$, this.appService.activeBom$]).subscribe(([project, bom]) => {
            this.project = project;
            this.bom = bom;

            if (bom && bom.format === "SPDX") {
                this.form.setValue({ format: "spdx", includeVulns: true });
            } else {
                this.form.setValue({ format: "cyclonedx", includeVulns: true });
            }
        });
    }

    ngOnDestroy(): void {
        this._sub.unsubscribe();
    }

    export() {
        const type = this.form.value.format;
        const includeVulns = this.form.value.includeVulns ?? true;

        if (!type || !this.project || !this.bom) return;
        const tag = this.bom.tag;

        this.bomService.export(this.project.slug, tag, type, includeVulns).subscribe((res) => {
            if (!res.body) return;
            this.saveFile(res.body, this.getFilename(res.headers.get("Content-Disposition"), tag, type));
            this.dialog.close();
        });
    }

    private saveFile(content: Blob, filename: string) {
        const link = document.createElement("a");
        link.href = window.URL.createObjectURL(content);
        link.download = filename;
        link.click();
    }

    private getFilename(header: string | null, tag: string, type: string) {
        // try to extract from content disposition header
        if (header) {
            const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/;
            const matches = filenameRegex.exec(header);
            if (matches != null && matches[1]) {
                return matches[1].replace(/['"]/g, "");
            }
        }

        // fallback to type
        if (type === "csv") return `${tag}.csv`;
        return `${tag}.json`;
    }
}
