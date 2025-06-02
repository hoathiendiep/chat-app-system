import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { HttpClient, HttpBackend } from '@angular/common/http';

//Move this type to a shared folder
export interface AppConfig {
  API_URL: string;
  KEYCLOAK_URI: string;
  KEYCLOAK_REALM: string;
  KEYCLOAK_CLIENT_ID: string;
  CALLBACK_URL: string;
}

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  //We create the configuration with default values in case anything fails
  private configuration: AppConfig = {
    API_URL: 'http://localhost:8081/chat-app-api/rs/v1',
    KEYCLOAK_URI: 'http://localhosssst:8080',
    KEYCLOAK_REALM:'chatapp-realm',
    KEYCLOAK_CLIENT_ID: 'chatapp',
    CALLBACK_URL: 'http://localhost:4200'
  };

  private http: HttpClient;
  constructor(private readonly httpHandler: HttpBackend) {
    this.http = new HttpClient(this.httpHandler);
  }

  //This function will get the current config for the environment
  setConfig(): Promise<void | AppConfig> {
    return firstValueFrom(this.http.get<AppConfig>('/app-config.json',{
      headers: { 'Cache-Control': 'no-cache, no-store, must-revalidate' }
    }))
      .then((config: AppConfig) => {
      console.log('Config loaded:', config);
      this.configuration = config;
      return config;
    })
      .catch((error) => {
        console.error(error);
      });
  }

  //We're going to use this function to read the config.
  readConfig(): AppConfig {
    return this.configuration;
  }
}