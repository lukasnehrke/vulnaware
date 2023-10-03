import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { environment } from "../../../environments/environment";

@Injectable({ providedIn: "root" })
export class UserService {
    constructor(private http: HttpClient) {}

    generateApiKey() {
        return this.http.post<{ apiKey: string }>(`${environment.baseUrl}/users/generate-key`, {});
    }
}
