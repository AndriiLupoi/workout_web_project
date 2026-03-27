import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SaveProfileRequest {
  goal: string;
  level: string;
  workoutsPerWeek: number;
  currentWeight: number | null;
  targetWeight: number | null;
  height: number | null;
  age: number | null;
}

export interface ProfileResponse {
  id: string;
  userId: string;
  goal: string;
  level: string;
  workoutsPerWeek: number;
  currentWeight: number;
  targetWeight: number;
  height: number;
  age: number;
}

@Injectable({ providedIn: 'root' })
export class UserProfileService {
  private readonly apiUrl = '/api/v1/profile';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(this.apiUrl);
  }

  saveProfile(request: SaveProfileRequest): Observable<ProfileResponse> {
    return this.http.put<ProfileResponse>(this.apiUrl, request);
  }
}
