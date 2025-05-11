import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js'
import { UserService } from '../../services/services';
import { environment } from '../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;

  constructor() { }

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: environment.keycloakConfig.auth.url,
        realm: environment.keycloakConfig.auth.realm,
        clientId: environment.keycloakConfig.auth.clientId
      });
    }
    return this._keycloak;
  }
  async init() {
    await this.keycloak.init({
      onLoad: 'login-required',
    });
  }

  
  async login() {
    await this.keycloak.login() 

  }

  get userId(): string {
    return this.keycloak?.tokenParsed?.sub as string;
  }

  get isTokenValid(): boolean {
    return !this.keycloak.isTokenExpired();
  }

  get fullName(): string {
    return this.keycloak.tokenParsed?.['name'] as string;
  }

  get token(): string {
    return this.keycloak.token as string;
  }

  logout() {
    // this.userService.setUserStatus({
    //   user_id: this.userId,
    //   is_online: false,
    // }).subscribe(() => {
      this.keycloak.logout({ redirectUri: 'http://localhost:4200' });
    // });
  }
  accountManagement() {
    return this.keycloak.accountManagement();
  }
}
