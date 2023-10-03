import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Activity } from "../models/activity.model";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: "root" })
export class ActivityService {
    constructor(private http: HttpClient) {}

    findAll(project: string) {
        return this.http.get<Activity[]>(`${environment.baseUrl}/projects/${project}/activities`);
    }
}
