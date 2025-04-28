import { Component } from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import { TokenService } from '../../services/token.service';
import {AuthenticationRequest} from '../../models/authentication-request';
import {AuthenticationResponse} from '../../models/authentication-response';

@Component({
  selector: 'app-login',
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  authRequest: AuthenticationRequest = { email: '', password: '' };
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private tokenService: TokenService

  ) {}

  login(): void {
    // this.errorMsg = [];
    // this.authService.authenticate({
    //   body: this.authRequest
    // }).subscribe({
    //   next: (res: AuthenticationResponse) => {
    //     // todo save token
    //     this.tokenService.token = res.token as string;
    //     this.router.navigate(['books']);
    //   },
    //   error: (err) => {
    //     console.log(err);
    //     if (err.error.validationErrors) {
    //       this.errorMsg = err.error.validationErrors;
    //     } else {
    //       this.errorMsg.push(err.error.error);
    //     }
    //   }
    // })
  }

}
