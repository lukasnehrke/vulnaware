import { HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { AuthService } from "../../shared/services/auth.service";
import { Injectable } from "@angular/core";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private auth: AuthService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler) {
        const authToken = this.auth.getToken();
        if (authToken) {
            return next.handle(req.clone({ setHeaders: { Authorization: `Bearer ${authToken}` } }));
        }
        return next.handle(req);
    }
}
