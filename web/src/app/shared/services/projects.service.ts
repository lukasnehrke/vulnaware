import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Project } from "../models/project.model";
import { environment } from "../../../environments/environment";

@Injectable({
    providedIn: "root",
})
export class ProjectService {
    constructor(private http: HttpClient) {}

    findAll(filter: string | null) {
        let params = new HttpParams();
        if (filter) params = params.set("name", filter);
        return this.http.get<Project[]>(`${environment.baseUrl}/projects`, { params });
    }

    find(slug: string) {
        return this.http.get<Project>(`${environment.baseUrl}/projects/${slug}`);
    }

    create(name: string, slug: string) {
        return this.http.post<Project>(`${environment.baseUrl}/projects`, { name, slug });
    }
}
