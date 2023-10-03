import { Component, Input, OnInit } from "@angular/core";
import { Vulnerability } from "../../shared/models/vulnerability.model";
import { MatCardModule } from "@angular/material/card";
import { MatDividerModule } from "@angular/material/divider";
import { NgClass, NgForOf, NgIf } from "@angular/common";
import { RouterLink } from "@angular/router";
import { MatIconModule } from "@angular/material/icon";
import { MatTooltipModule } from "@angular/material/tooltip";
import { formatRelative } from "date-fns";

@Component({
    standalone: true,
    selector: "va-vuln-card",
    templateUrl: "./vuln-card.component.html",
    styleUrls: ["./vuln-card.component.scss"],
    imports: [MatCardModule, MatDividerModule, NgIf, RouterLink, NgForOf, MatIconModule, MatTooltipModule, NgClass],
})
export class VulnCardComponent implements OnInit {
    @Input() vuln: Vulnerability;

    advisoryLink?: string;
    package: string;
    cpe?: string;

    get severity() {
        if (!this.vuln.advisory.risk) return "unknown";
        if (this.vuln.advisory.risk > 9.0) return "critical";
        if (this.vuln.advisory.risk > 7.0) return "high";
        if (this.vuln.advisory.risk > 4.0) return "medium";
        if (this.vuln.advisory.risk > 0.0) return "low";
        return "none";
    }

    get cve() {
        if (this.vuln.advisory.type === "OSV") {
            return this.vuln.advisory.osv!.aliases?.find((a) => a.startsWith("CVE-"));
        }
        return undefined;
    }

    get published() {
        if (this.vuln.advisory.type === "OSV") {
            return this.formatRelative(this.vuln.advisory.osv!.published);
        }
        if (this.vuln.advisory.type === "NVD") {
            return this.formatRelative(this.vuln.advisory.nvd!.cve.published);
        }
        return "";
    }

    get affected() {
        if (this.vuln.components.length === 0) {
            return "No packages affected";
        }
        const names = this.vuln.components
            .slice(0, 2)
            .map((c) => (c.version ? `${c.name}@${c.version}` : c.name))
            .join(", ");
        if (this.vuln.components.length > 2) return `${names} and ${this.vuln.components.length - 2} more`;
        return names;
    }

    get fixes() {
        const fixes = this.vuln.components.filter((c) => !!c.fixedVersion);
        if (fixes.length === 0) return null;
        if (fixes.length > 2) {
            return (
                fixes
                    .slice(0, 2)
                    .map((c) => `${c.name}@${c.fixedVersion}`)
                    .join(", ") + ` and ${fixes.length - 2} more`
            );
        }
        return fixes.map((c) => `${c.name}@${c.fixedVersion}`).join(", ");
    }

    ngOnInit(): void {
        if (this.vuln.advisory.type === "OSV") {
            this.advisoryLink = `https://osv.dev/vulnerability/${this.vuln.advisory.id}`;

            const affected = this.vuln.advisory.osv!.affected;
            if (affected && affected.length > 0) {
                this.package = affected[0].package.name;
            }
        }
        if (this.vuln.advisory.type === "NVD") {
            this.advisoryLink = `https://nvd.nist.gov/vuln/detail/${this.vuln.advisory.id}`;
            this.package = this.vuln.advisory.id;
        }
    }

    formatRelative(date?: string) {
        if (!date) return "";
        const str = formatRelative(new Date(date), new Date());
        return str.charAt(0).toUpperCase() + str.slice(1);
    }
}
