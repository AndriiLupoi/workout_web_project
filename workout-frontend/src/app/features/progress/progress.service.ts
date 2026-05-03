import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WeightEntry {
  date: string;
  weight: number;
  weekNumber: number;
  dayNumber: number;
}

export interface ExerciseProgressItem {
  exerciseId: string;
  exerciseName: string;
  entries: WeightEntry[];
}

export interface BodyWeightItem {
  date: string;
  weight: number;
}

export interface PrItem {
  exerciseId: string;
  exerciseName: string;
  maxWeight: number;
  date: string;
}

export interface ProgressResponse {
  exerciseProgress: ExerciseProgressItem[];
  bodyWeightHistory: BodyWeightItem[];
  personalRecords: PrItem[];
}

@Injectable({ providedIn: 'root' })
export class ProgressService {
  private readonly api = '/api/v1/progress';

  constructor(private http: HttpClient) {}

  getProgress(): Observable<ProgressResponse> {
    return this.http.get<ProgressResponse>(this.api);
  }
}
