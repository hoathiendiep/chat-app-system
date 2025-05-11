export const environment = {
  production: false,
  keycloakConfig: {
    auth: {
      url: 'http://localhost:8080',
      realm: 'chatapp-realm',
      clientId: 'chatapp'
    },
  },
  apiConfig: {
    base: 'http://localhost:8081/chat-app-api/rs/v1'
  },
};