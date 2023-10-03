import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";

import { MatButtonModule } from "@angular/material/button";
import { MatIconModule } from "@angular/material/icon";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatTableModule } from "@angular/material/table";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatStepperModule } from "@angular/material/stepper";
import { MatDialogModule } from "@angular/material/dialog";
import { MatFormFieldModule } from "@angular/material/form-field";
import { ReactiveFormsModule } from "@angular/forms";
import { MatInputModule } from "@angular/material/input";

import { ProjectModule } from "../../project.module";
import { BomTableComponent } from "./components/bom-table/bom-table.component";
import { BomTreeComponent } from "./components/bom-tree/bom-tree.component";
import { BomPieComponent } from "./components/bom-pie/bom-pie.component";
import { ExportDialogComponent } from "./components/export-dialog/export-dialog.component";
import { UploadDialogComponent } from "./components/upload-dialog/upload-dialog.component";
import { BomHistoryComponent } from "./components/bom-history/bom-history.component";
import { BomSelectComponent } from "../../components/bom-select/bom-select.component";
import { MatButtonToggleModule } from "@angular/material/button-toggle";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { BomComponent } from "./pages/bom.component";
import { BomRoutingModule } from "./bom-routing.module";
import { MatDividerModule } from "@angular/material/divider";
import { MatCardModule } from "@angular/material/card";
import { MatPaginatorModule } from "@angular/material/paginator";
import { NgChartsModule } from "ng2-charts";
import { BadgeComponent } from "../../../../components/badge/badge.component";
import { MetricsComponent } from "./components/metrics/metrics.component";

@NgModule({
    imports: [
        BomRoutingModule,
        CommonModule,
        MatButtonModule,
        MatIconModule,
        MatProgressSpinnerModule,
        MatTableModule,
        MatTooltipModule,
        BomSelectComponent,
        ProjectModule,
        MatStepperModule,
        MatDialogModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatInputModule,
        MatButtonToggleModule,
        MatCheckboxModule,
        MatDividerModule,
        MatCardModule,
        MatPaginatorModule,
        NgChartsModule,
        BadgeComponent,
    ],
    declarations: [
        BomHistoryComponent,
        BomTableComponent,
        BomPieComponent,
        BomTreeComponent,
        ExportDialogComponent,
        UploadDialogComponent,
        BomComponent,
        MetricsComponent,
    ],
})
export class BomModule {}
