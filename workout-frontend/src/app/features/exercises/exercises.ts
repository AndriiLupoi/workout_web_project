import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExercisesService } from './exercises.service';

export interface Exercise {
  id: string;
  name: string;
  muscleGroup: string;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  equipment: string;
  description?: string;
}

@Component({
  selector: 'app-exercises',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './exercises.html',
  styleUrl: './exercises.css'
})
export class ExercisesComponent implements OnInit {

  private exercisesService = inject(ExercisesService);

  exercises = signal<Exercise[]>([]);
  filteredExercises = signal<Exercise[]>([]);

  searchTerm = '';
  selectedMuscleGroup = '';
  selectedDifficulty = '';

  muscleGroups = [
    'Груди', 'Спина', 'Ноги', 'Плечі', 'Біцепс', 'Трицепс',
    'Прес', 'Передпліччя', 'Сідниці', 'Стегна', 'Ікри'
  ];

  ngOnInit(): void {
    this.loadExercises();
  }

  loadExercises(): void {
    this.exercisesService.getAllExercises().subscribe({
      next: (data) => {
        this.exercises.set(data);
        this.filteredExercises.set(data);
      },
      error: (err) => {
        console.error('Помилка при завантаженні вправ з сервера:', err);
        this.filteredExercises.set([]);   // очищаємо на випадок помилки
      }
    });
  }

  applyFilters(): void {
    let result = this.exercises();

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      result = result.filter(ex =>
        ex.name.toLowerCase().includes(term) ||
        (ex.description && ex.description.toLowerCase().includes(term))
      );
    }

    if (this.selectedMuscleGroup) {
      result = result.filter(ex => ex.muscleGroup === this.selectedMuscleGroup);
    }

    if (this.selectedDifficulty) {
      result = result.filter(ex => ex.difficulty === this.selectedDifficulty);
    }

    this.filteredExercises.set(result);
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedMuscleGroup = '';
    this.selectedDifficulty = '';
    this.applyFilters();
  }
}
