import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Відповідає LogWorkoutRequest.LoggedExerciseRequest на бекенді
export interface LoggedExerciseRequest {
  exerciseId:    string;
  exerciseName:  string;
  plannedSets:   number;        // int — НЕ може бути null
  plannedReps:   string;
  plannedWeight: number | null; // Double — може бути null
  actualSets:    number;        // int — НЕ може бути null
  actualReps:    string;
  actualWeight:  number | null; // Double — може бути null
  feltEasy:      boolean;
  notes:         string;
}

// Відповідає LogWorkoutRequest на бекенді
export interface LogWorkoutRequest {
  planId:     string;
  weekNumber: number; // int — НЕ може бути null
  dayNumber:  number; // int — НЕ може бути null
  exercises:  LoggedExerciseRequest[];
  notes:      string;
}

// Відповідає WorkoutLogResponse на бекенді
export interface WorkoutLogResponse {
  id:          string;
  planId:      string;
  weekNumber:  number;
  dayNumber:   number;
  exercises:   LoggedExerciseResponse[];
  notes:       string;
  completedAt: string;
}

export interface LoggedExerciseResponse {
  exerciseId:    string;
  exerciseName:  string;
  plannedSets:   number;
  plannedReps:   string;
  plannedWeight: number | null;
  actualSets:    number;
  actualReps:    string;
  actualWeight:  number | null;
  feltEasy:      boolean;
  notes:         string;
}

@Injectable({ providedIn: 'root' })
export class WorkoutLogService {
  private readonly api = '/api/v1/logs';

  constructor(private http: HttpClient) {}

  saveLog(request: LogWorkoutRequest): Observable<WorkoutLogResponse> {
    return this.http.post<WorkoutLogResponse>(this.api, request);
  }

  // GET /api/v1/logs?planId=xxx
  getLogsByPlan(planId: string): Observable<WorkoutLogResponse[]> {
    return this.http.get<WorkoutLogResponse[]>(this.api, {
      params: { planId }
    });
  }

  // GET /api/v1/logs/day?planId=xxx&week=1&day=2
  getLogForDay(planId: string, week: number, day: number): Observable<WorkoutLogResponse> {
    return this.http.get<WorkoutLogResponse>(`${this.api}/day`, {
      params: { planId, week, day }
    });
  }
}

