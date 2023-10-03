import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Notification, Status } from "../models/notification.model";
import { AuthService } from "./auth.service";
import { of, startWith, switchMap } from "rxjs";
import { environment } from "../../../environments/environment";
import { Page } from "../models/pagination.model";

@Injectable({
    providedIn: "root",
})
export class NotificationService {
    constructor(
        private http: HttpClient,
        private _authService: AuthService,
    ) {}

    get status() {
        return this._authService.currentUser.pipe(
            startWith(null),
            switchMap((user) => {
                if (!user) return of({ unread: 0 });
                return this.http.get<Status>(`${environment.baseUrl}/notifications/unread`);
            }),
        );
    }

    find(id: string) {
        return this.http.get<Notification>(`${environment.baseUrl}/notifications/${id}`);
    }

    findAll(page: number, pageSize: number) {
        let params = new HttpParams();
        params = params.set("page", page);
        params = params.set("size", pageSize);
        return this.http.get<Page<Notification>>(`${environment.baseUrl}/notifications`, { params });
    }

    update(id: string, data: any) {
        return this.http.put<Notification>(`${environment.baseUrl}/notifications/${id}`, data);
    }

    formatDate(text: string) {
        return new Date(text).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: "numeric",
            minute: "numeric",
            hour12: false,
        });
    }
}
