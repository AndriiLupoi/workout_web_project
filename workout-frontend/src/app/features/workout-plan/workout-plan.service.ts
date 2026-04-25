import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface WorkoutExercise {
  exerciseId:    string;
  exerciseName:  string;
  sets:          number;
  reps:          string;
  restSeconds:   number;
  plannedWeight: number | null;
}

export interface WorkoutDay {
  weekNumber:    number;
  dayNumber:     number;
  focus:         string;
  intensityType: string;
  exercises:     WorkoutExercise[];
}

export interface WorkoutPlan {
  id:            string;
  title:         string;
  goal:          string;
  planType:      string;
  durationWeeks: number;
  status:        string;
  days:          WorkoutDay[];
  createdAt:     string;
}

@Injectable({ providedIn: 'root' })
export class WorkoutPlanService {
  private readonly api = '/api/v1/plans';

  constructor(private http: HttpClient) {}

  generate(): Observable<WorkoutPlan> {
    return this.http.post<WorkoutPlan>(`${this.api}/generate`, {});
  }

  getAll(): Observable<WorkoutPlan[]> {
    return this.http.get<WorkoutPlan[]>(this.api);
  }
}
