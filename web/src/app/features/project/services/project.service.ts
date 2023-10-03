import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../../environments/environment";
import { ProjectMember } from "../models/member.model";

@Injectable()
export class ProjectService {
    constructor(private http: HttpClient) {}

    findMembers(slug: string) {
        return this.http.get<ProjectMember[]>(`${environment.baseUrl}/projects/${slug}/members`);
    }

    addMember(slug: string, email: string) {
        return this.http.post<ProjectMember[]>(`${environment.baseUrl}/projects/${slug}/members/${email}`, { email });
    }

    removeMember(slug: string, email: string) {
        return this.http.delete<ProjectMember[]>(`${environment.baseUrl}/projects/${slug}/members/${email}`, {});
    }
}
