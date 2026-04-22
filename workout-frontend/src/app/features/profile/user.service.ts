import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class UserService {

  constructor(private http: HttpClient) {}

  getUser() {
    return this.http.get<any>('/api/v1/user');
  }

  updateUser(payload: { firstName: string; lastName: string }) {
    return this.http.put('/api/v1/user', payload);
  }
}
