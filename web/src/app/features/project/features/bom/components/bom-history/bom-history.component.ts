import { Component, Input, OnChanges, OnDestroy, OnInit, SimpleChanges } from "@angular/core";
import { ChartConfiguration, ChartType } from "chart.js";
import { subDays } from "date-fns";
import "chartjs-adapter-date-fns";
import { History } from "../../../../../../shared/models/bom.model";
import { AppService } from "../../../../../../shared/services/app.service";
import { Subscription } from "rxjs";

@Component({
    selector: "va-project-bom-history",
    templateUrl: "./bom-history.component.html",
    styleUrls: ["./bom-history.component.scss"],
})
export class BomHistoryComponent implements OnInit, OnDestroy {
    history: History[];
    sub: Subscription;

    lineChartType: ChartType = "line";
    lineChartOptions: ChartConfiguration["options"] = {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
            x: {
                type: "timeseries",
                time: {
                    round: "minute",
                },
            },
        },
        plugins: {
            title: { text: "# of components", display: true },
            legend: { display: false },
        },
    };
    lineChartData: ChartConfiguration["data"] = { datasets: [] };

    constructor(private appService: AppService) {}

    ngOnInit(): void {
        this.sub = this.appService.activeBom$.subscribe((bom) => {
            if (bom) {
                this.history = bom.history;
                this.updateChart();
            }
        });
    }

    ngOnDestroy(): void {
        this.sub.unsubscribe();
    }

    private updateChart() {
        this.lineChartData = {
            datasets: [
                {
                    data: this.history.map((h) => ({ x: h.date, y: h.components })) as any,
                    fill: "origin",
                },
            ],
        };
    }
}
