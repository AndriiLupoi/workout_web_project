import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  goal: string;
  level: string;
  planType: string;
  workoutsPerWeek: number;
  currentWeight?: number;
  targetWeight?: number;
  height?: number;
  age?: number;
  availableEquipment: string[];
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly apiUrl = '/api/v1/profile';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.apiUrl);
  }

  updateProfile(profile: Partial<UserProfile>): Observable<UserProfile> {
    return this.http.put<UserProfile>(this.apiUrl, profile);
  }
}
