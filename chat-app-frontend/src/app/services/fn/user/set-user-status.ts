import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

import { ApiResponseUnit } from '../../models/api-response-unit';

export interface SetUserStatus$Params {
    user_id: string;
    is_online: boolean;
}

export function setUserStatus(http: HttpClient, rootUrl: string, params?: SetUserStatus$Params, context?: HttpContext): Observable<StrictHttpResponse<ApiResponseUnit>> {
  const rb = new RequestBuilder(rootUrl, setUserStatus.PATH, 'patch');
  if (params) {
    rb.query('is_online', params.is_online, {});
    rb.path('user_id', params.user_id, {});
  }
 
  return http.request(
    rb.build({ responseType: 'json', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<ApiResponseUnit>;
    })
  );
}

setUserStatus.PATH = '/users/status/{user_id}';
