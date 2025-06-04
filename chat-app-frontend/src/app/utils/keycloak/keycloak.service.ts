import { Injectable } from '@angular/core';
import Keycloak from 'keycloak-js'
import { ConfigService } from '../../services/config.service';
@Injectable({
  providedIn: 'root'
})
export class KeycloakService {
  private _keycloak: Keycloak | undefined;

  constructor(private configService: ConfigService) { }

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
        url: this.configService.readConfig().KEYCLOAK_URI,
        realm: this.configService.readConfig().KEYCLOAK_REALM,
        clientId: this.configService.readConfig().KEYCLOAK_CLIENT_ID
      });
    }
    return this._keycloak;
  }
  async init() {
    await this.keycloak.init({
      onLoad: 'login-required',
      pkceMethod: 'S256',
      checkLoginIframe: false
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
    this.keycloak.logout({ redirectUri: this.configService.readConfig().CALLBACK_URL });
  }
  accountManagement() {
    return this.keycloak.accountManagement();
  }
}
