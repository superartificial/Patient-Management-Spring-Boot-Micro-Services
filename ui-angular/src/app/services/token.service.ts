import { Injectable } from '@angular/core';
import { JwtModule, JwtHelperService } from "@auth0/angular-jwt";

@Injectable({
  providedIn: 'root'
})
export class TokenService {

  set token(token: string) {
    localStorage.setItem('token', token);
  }

  get token() {
    return localStorage.getItem('token') as string;
  }

  isTokenNotValid() {
    return !this.isTokenValid();
  }

  isTokenValid() {
    const token = this.token;
    if (!token) return false;
    const jwtHelper: JwtHelperService = new JwtHelperService();
    if (jwtHelper.isTokenExpired(token)) {
      localStorage.clear();
      return false;
    }
    return true;
  }

}
