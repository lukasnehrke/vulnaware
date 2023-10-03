import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Bom, Component } from "../models/bom.model";
import { combineLatest, of, switchMap } from "rxjs";
import { Page } from "../models/pagination.model";
import { AppService } from "./app.service";
import { environment } from "../../../environments/environment";

@Injectable({
    providedIn: "root",
})
export class BomService {
    constructor(
        private _appService: AppService,
        private _http: HttpClient,
    ) {}

    toFriendlyEcosystem(ecosystem: string) {
        switch (ecosystem) {
            case "GITHUB":
                return "GitHub";
            case "NPM":
                return "npm";
            default:
                return ecosystem[0] + ecosystem.slice(1).toLowerCase();
        }
    }

    findAll(project: string, filter: string | null) {
        let params = new HttpParams();
        if (filter) {
            params = params.set("filter", filter);
        }
        return this._http.get<Bom[]>(`${environment.baseUrl}/projects/${project}/bom`, { params });
    }

    find(project: string, tag: string) {
        return this._http.get<Bom>(`${environment.baseUrl}/projects/${project}/bom/${tag}`);
    }

    getComponent(id: string) {
        return combineLatest([this._appService.activeProject$, this._appService.activeBom$]).pipe(
            switchMap(([project, bom]) => {
                if (!project || !bom) return of(null);
                return this._http.get<Component[]>(`${environment.baseUrl}/projects/${project.slug}/bom/${bom.tag}/components/${id}`);
            }),
        );
    }

    listComponents(filter: string, page: number, pageSize: number, direct: boolean | null = null) {
        let params = new HttpParams().set("name", filter).set("page", page).set("size", pageSize);
        if (direct) params = params.set("direct", direct);

        return combineLatest([this._appService.activeProject$, this._appService.activeBom$]).pipe(
            switchMap(([project, bom]) => {
                if (!project || !bom) return of(null);
                return this._http.get<Page<Component>>(`${environment.baseUrl}/projects/${project.slug}/bom/${bom.tag}/components`, { params });
            }),
        );
    }

    upload(project: string, tag: string, details: string, file: File) {
        const form = new FormData();
        form.append("description", details);
        form.append("file", file);

        return this._http.post<Bom>(`${environment.baseUrl}/projects/${project}/bom/${tag}`, form);
    }

    export(project: string, tag: string, type: string, includeVulns: boolean) {
        return this._http.get(`${environment.baseUrl}/projects/${project}/bom/${tag}/export/${type}`, {
            params: { includeVulns },
            observe: "response",
            responseType: "blob",
        });
    }
}
