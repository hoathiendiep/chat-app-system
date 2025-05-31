#!/bin/bash
# Substitute environment variables into app-config.json
envsubst '${FRONT_END_BASE_URL},${API_URL},${KEYCLOAK_URI}' < /usr/share/nginx/html/config/app-config.json > /usr/share/nginx/html/app-config.json.tmp
mv /usr/share/nginx/html/app-config.json.tmp /usr/share/nginx/html/app-config.json

# Start nginx
nginx -g 'daemon off;' 