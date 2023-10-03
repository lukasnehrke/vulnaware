import { Component, Input, OnChanges, OnInit, SimpleChanges } from "@angular/core";
import { ChartConfiguration, ChartData, ChartType } from "chart.js";
import { FormControl } from "@angular/forms";
import { Metrics } from "../../../../../../shared/models/bom.model";

@Component({
    selector: "va-project-bom-pie",
    templateUrl: "./bom-pie.component.html",
    styleUrls: ["./bom-pie.component.scss"],
})
export class BomPieComponent implements OnInit, OnChanges {
    @Input() public metrics: Metrics;

    representation = new FormControl("ecosystem");

    pieChartType: ChartType = "pie";
    pieChartPlugins = [];
    pieChartData: ChartData<"pie", number[], string | string[]> = { datasets: [] };
    pieChartOptions: ChartConfiguration["options"] = {
        responsive: true,
        maintainAspectRatio: true,
        plugins: {
            legend: {
                display: false,
                position: "top",
            },
        },
    };

    ngOnInit(): void {
        this.representation.valueChanges.subscribe((value) => {
            this.updateChart();
        });
    }

    private updateChart() {
        if (this.representation.value === "ecosystem") {
            this.pieChartData = {
                labels: Object.keys(this.metrics.dependencyCount),
                datasets: [
                    {
                        data: Object.values(this.metrics.dependencyCount),
                        backgroundColor: Object.keys(this.metrics.dependencyCount).map(this.mapColor),
                    },
                ],
            };
        } else {
            this.pieChartData = {
                labels: ["Up-to-date", "Outdated"],
                datasets: [
                    {
                        data: [this.metrics.totalDependencies - this.metrics.updatesCount, this.metrics.updatesCount],
                    },
                ],
            };
        }
    }

    private mapColor(ecosystem: string): string {
        switch (ecosystem) {
            case "MAVEN":
                return "#fe5006";
            case "NPM":
                return "#e32e37";
            case "GITHUB":
                return "#101315";
            default:
                return "#9E9E9E";
        }
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.updateChart();
    }
}
