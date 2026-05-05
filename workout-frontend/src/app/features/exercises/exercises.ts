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
  BEGINNER: 1,
  INTERMEDIATE: 2,
  ADVANCED: 3
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

  // ================= SIGNALS =================
  exercises = signal<Exercise[]>([]);
  filteredExercises = signal<Exercise[]>([]);

  // ================= FILTER STATE =================
  searchTerm = '';
  selectedMuscleGroup = '';
  selectedDifficulty = '';
  selectedSortBy: 'name' | 'difficulty' | 'muscleGroup' = 'name';

  showFilters = false;

  private searchTimeout: any;

  // ================= DATA =================
  muscleGroups = [
    { label: 'Груди',       value: 'CHEST' },
    { label: 'Спина',       value: 'BACK' },
    { label: 'Ноги',        value: 'LEGS' },
    { label: 'Плечі',       value: 'SHOULDERS' },
    { label: 'Біцепс',      value: 'BICEPS' },
    { label: 'Трицепс',     value: 'TRICEPS' },
    { label: 'Прес',        value: 'ABS' },
    { label: 'Передпліччя', value: 'FOREARMS' },
    { label: 'Трапеція',    value: 'TRAPS' },
    { label: 'Ікри',        value: 'CALVES' },
    { label: 'Кардіо',      value: 'CARDIO' },
  ];

  // ================= LIFECYCLE =================
  ngOnInit(): void {
    this.loadExercises();
  }

  // ================= API =================
  loadExercises(): void {
    this.exercisesService.getAllExercises().subscribe({
      next: (data) => {
        this.exercises.set(data);
        this.applyFilters();
      },
      error: (err) => {
        console.error('Помилка при завантаженні вправ:', err);
        this.filteredExercises.set([]);
      }
    });
  }

  // ================= FILTER LOGIC =================
  applyFilters(): void {
    let result = [...this.exercises()];

    // 🔍 пошук
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase().trim();
      result = result.filter(ex =>
        ex.name.toLowerCase().includes(term) ||
        (ex.description && ex.description.toLowerCase().includes(term))
      );
    }

    // 💪 група м'язів
    if (this.selectedMuscleGroup) {
      result = result.filter(ex => ex.muscleGroup === this.selectedMuscleGroup);
    }

    // 📊 складність
    if (this.selectedDifficulty) {
      result = result.filter(ex => ex.difficulty === this.selectedDifficulty);
    }

    // 🔃 сортування
    result.sort((a, b) => {
      switch (this.selectedSortBy) {
        case 'name':
          return a.name.localeCompare(b.name, 'uk');

        case 'difficulty':
          return (DIFFICULTY_ORDER[a.difficulty] ?? 0) -
            (DIFFICULTY_ORDER[b.difficulty] ?? 0);

        case 'muscleGroup':
          return a.muscleGroup.localeCompare(b.muscleGroup, 'uk');

        default:
          return 0;
      }
    });

    this.filteredExercises.set(result);
  }

  onFilterChange(): void {
    clearTimeout(this.searchTimeout);

    this.searchTimeout = setTimeout(() => {
      this.applyFilters();
    }, 200);
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedMuscleGroup = '';
    this.selectedDifficulty = '';
    this.selectedSortBy = 'name';
    this.applyFilters();
  }

  // ================= UI HELPERS =================
  toggleFilters(): void {
    this.showFilters = !this.showFilters;
  }

  getMuscleLabel(value: string): string {
    return this.muscleGroups.find(g => g.value === value)?.label || value;
  }

  getDifficultyLabel(value: string): string {
    switch (value) {
      case 'BEGINNER': return 'Початківець';
      case 'INTERMEDIATE': return 'Середній';
      case 'ADVANCED': return 'Просунутий';
      default: return value;
    }
  }

  getMuscleIcon(group: string): string {
    const icons: Record<string, string> = {
      CHEST: '💪',
      BACK: '🏋️',
      LEGS: '🦵',
      SHOULDERS: '🏋️‍♂️',
      BICEPS: '💪',
      TRICEPS: '🔱',
      ABS: '🔥',
      FOREARMS: '✋',
      TRAPS: '🧱',
      CALVES: '🦶',
      CARDIO: '❤️'
    };

    return icons[group] || '🏋️';
  }

  getMuscleCount(group: string): number {
    return this.exercises().filter(ex => ex.muscleGroup === group).length;
  }
}
