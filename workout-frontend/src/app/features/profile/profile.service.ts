import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id?: string;
  firstName: string;
  lastName: string;
  age?: number;
  height?: number;
  currentWeight?: number;
  targetWeight?: number;
  goal: 'muscle_gain' | 'fat_loss' | 'strength';
  experienceLevel: 'beginner' | 'intermediate' | 'advanced';
  trainingDaysPerWeek: number;
  availableEquipment: string[];
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private readonly apiUrl = '/api/v1/profile';

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(this.apiUrl);
  }

  updateProfile(profile: {
    goal: any;
    currentWeight: null;
    level: any;
    workoutsPerWeek: number;
    targetWeight: null;
    age: null;
    height: null
  }): Observable<UserProfile> {
    return this.http.put<UserProfile>(this.apiUrl, profile);
  }
}
