import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, ParamMap } from "@angular/router";
import { AppService } from "../../../../shared/services/app.service";
import { Vulnerability } from "../../../../shared/models/vulnerability.model";
import { VulnerabilityService } from "../../../../shared/services/vulnerability.service";
import { marked } from "marked";
import { Project } from "../../../../shared/models/project.model";
import { MatDialog } from "@angular/material/dialog";
import { VulnEditComponent } from "../../components/vuln-edit/vuln-edit.component";
import { formatRelative } from "date-fns";

@Component({
    selector: "va-project-issue-details",
    styleUrls: ["./issue-details.component.scss"],
    templateUrl: "./issue-details.component.html",
})
export class IssueDetailsComponent implements OnInit {
    project: Project | null = null;
    vuln: Vulnerability | null = null;

    constructor(
        private dialog: MatDialog,
        private route: ActivatedRoute,
        private appService: AppService,
        private vulnService: VulnerabilityService,
    ) {}

    get id() {
        if (!this.vuln) return "";
        return this.vuln?.advisory.id;
    }

    get published() {
        if (!this.vuln) return "";
        if (this.isOSV) return this.formatRelative(this.vuln.advisory.osv!.published);
        return this.formatRelative(this.vuln.advisory.nvd!.cve.published);
    }

    get description() {
        if (!this.vuln) return "";
        if (this.isOSV) return this.parseMarkdown(this.vuln.advisory.osv?.details);
        return this.vuln.advisory.nvd?.cve.descriptions.find((d) => d.lang === "en")?.value;
    }

    get isOSV() {
        return this.vuln?.advisory.type === "OSV";
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe((params: ParamMap) => {
            const id = params.get("vulnId");
            if (!id) return;

            this.appService.getActiveProject().subscribe((project) => {
                this.project = project;
                if (!project) return;

                this.vulnService.find(project.slug, id).subscribe((vuln) => {
                    this.vuln = vuln;
                });
            });
        });
    }

    openEditDialog() {
        const sub = this.dialog.open(VulnEditComponent).componentInstance.update.subscribe((update) => {
            this.vulnService.update(this.project!.slug, this.vuln!.id, update.status, update.comment).subscribe((v) => {
                this.vuln = v;
                sub.unsubscribe();
            });
        });
    }

    parseMarkdown(content?: string) {
        if (!content) return "(empty)";
        return marked.parse(content);
    }

    formatRelative(date?: string) {
        if (!date) return "";
        const str = formatRelative(new Date(date), new Date());
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
}
