// exercises.ts — додається лише selectedSortBy і sortExercises()
import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ExercisesService } from './exercises.service';

export interface Exercise {
  id: string;
  name: string;
  muscleGroup: string;
  difficulty: 'beginner' | 'intermediate' | 'advanced';
  equipmentType: string;
  description?: string;
}

const DIFFICULTY_ORDER: Record<string, number> = {
  beginner: 1,
  intermediate: 2,
  advanced: 3
};

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
  selectedSortBy: 'name' | 'difficulty' | 'muscleGroup' = 'name';

  muscleGroups = [
    { label: 'Груди',       value: 'CHEST' },
    { label: 'Спина',       value: 'BACK' },
    { label: 'Ноги',        value: 'LEGS' },
    { label: 'Плечі',       value: 'SHOULDERS' },
    { label: 'Біцепс',      value: 'BICEPS' },
    { label: 'Трицепс',     value: 'TRICEPS' },
    { label: 'Прес',        value: 'ABS' },
    { label: 'Передпліччя', value: 'FOREARMS' },
    { label: 'Трапеція',      value: 'TRAPS' },
    { label: 'Ікри',        value: 'CALVES' },
    { label: 'Кардіо',      value: 'CARDIO' },
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
        this.filteredExercises.set([]);
      }
    });
  }

  applyFilters(): void {
    let result = [...this.exercises()];

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

    result.sort((a, b) => {
      switch (this.selectedSortBy) {
        case 'name':
          return a.name.localeCompare(b.name, 'uk');
        case 'difficulty':
          return (DIFFICULTY_ORDER[a.difficulty] ?? 0)
            - (DIFFICULTY_ORDER[b.difficulty] ?? 0);
        case 'muscleGroup':
          return a.muscleGroup.localeCompare(b.muscleGroup, 'uk');
      }
    });

    this.filteredExercises.set(result);
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedMuscleGroup = '';
    this.selectedDifficulty = '';
    this.selectedSortBy = 'name';
    this.applyFilters();
  }
}
