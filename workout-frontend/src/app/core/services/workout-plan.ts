import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WorkoutExercise {
  exerciseId: string;
  exerciseName: string;
  sets: number;
  reps: string;
  restSeconds: number;
}

export interface WorkoutDay {
  dayNumber: number;
  focus: string;
  exercises: WorkoutExercise[];
}

export interface WorkoutPlan {
  id: string;
  title: string;
  goal: string;
  status: string;
  durationWeeks: number;
  days: WorkoutDay[];
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class WorkoutPlanService {
  private readonly apiUrl = '/api/v1/plans';

  constructor(private http: HttpClient) {}

  generate(): Observable<WorkoutPlan> {
    return this.http.post<WorkoutPlan>(`${this.apiUrl}/generate`, {});
  }

  getMyPlans(): Observable<WorkoutPlan[]> {
    return this.http.get<WorkoutPlan[]>(this.apiUrl);
  }
}
