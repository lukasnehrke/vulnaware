import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { BehaviorSubject, Observable } from "rxjs";
import { jwtDecode } from "jwt-decode";
import { UserDetails } from "../models/auth.model";
import { environment } from "../../../environments/environment";

@Injectable({
    providedIn: "root",
})
export class AuthService {
    token: string | null;
    tokenDetails: BehaviorSubject<any> = new BehaviorSubject<any>(null);

    constructor(private http: HttpClient) {
        this.token = localStorage.getItem("session");
        if (this.token) {
            this.tokenDetails.next(jwtDecode(this.token));
        }
    }

    get currentUser(): Observable<UserDetails> {
        return this.tokenDetails;
    }

    setToken(token: string | null) {
        this.token = token;
        if (token) {
            this.tokenDetails.next(jwtDecode(token));
            localStorage.setItem("session", token);
        } else {
            this.tokenDetails.next(null);
            localStorage.removeItem("session");
        }
    }

    getToken() {
        return this.token;
    }

    getDetails() {
        return this.tokenDetails;
    }

    isAuthenticated(): boolean {
        return !!this.token;
    }

    login(email: string, password: string) {
        return this.http.post<any>(`${environment.baseUrl}/auth/login`, { email, password });
    }

    register(name: string, email: string, password: string) {
        return this.http.post<any>(`${environment.baseUrl}/auth/register`, { name, email, password });
    }
}
