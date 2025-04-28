import { Injectable } from '@angular/core';
import {AuthenticationRequest} from '../models/authentication-request';
import {Observable, Observer} from 'rxjs';
import {AuthenticationResponse} from '../models/authentication-response';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor() {}

  authenticate(param: { body: AuthenticationRequest }): Observable<AuthenticationResponse> | null {
    return null;
  }
}
