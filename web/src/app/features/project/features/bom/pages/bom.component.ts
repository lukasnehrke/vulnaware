import { Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { UploadDialogComponent } from "../components/upload-dialog/upload-dialog.component";
import { ExportDialogComponent } from "../components/export-dialog/export-dialog.component";
import { Bom } from "../../../../../shared/models/bom.model";
import { AppService } from "../../../../../shared/services/app.service";

@Component({
    selector: "va-project-bom",
    templateUrl: "./bom.component.html",
    styleUrls: ["./bom.component.scss"],
})
export class BomComponent implements OnInit {
    bom: Bom | null;

    constructor(
        private dialog: MatDialog,
        private appService: AppService,
    ) {}

    ngOnInit(): void {
        this.appService.activeBom$.subscribe((bom) => {
            this.bom = bom;
        });
    }

    openUploadDialog() {
        this.dialog.open(UploadDialogComponent);
    }

    openExportDialog() {
        this.dialog.open(ExportDialogComponent);
    }
}
