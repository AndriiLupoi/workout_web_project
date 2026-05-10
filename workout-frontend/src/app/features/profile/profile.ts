import { Component, inject, OnInit, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService, WeightProgressResponse } from './profile.service';
import { UserService } from './user.service';
import { WorkoutLogService } from '../../core/services/workout-log/workout-log.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css'
})
export class ProfileComponent implements OnInit {

  private fb             = inject(FormBuilder);
  private profileService = inject(ProfileService);
  private userService    = inject(UserService);
  private logService     = inject(WorkoutLogService);

  isEditMode     = signal(false);
  isLoading      = signal(false);
  successMessage = signal('');
  errorMessage   = signal('');
  weightProgress = signal<WeightProgressResponse | null>(null);
  trainedDays    = signal<number[]>([]); // 0=Пн, 1=Вт, ..., 6=Нд

  profileForm: FormGroup;

  goals = [
    { value: 'MASS',              label: 'Набір м\'язової маси' },
    { value: 'LOSS',              label: 'Схуднення / спалення жиру' },
    { value: 'ENDURANCE',         label: 'Розвиток витривалості' },
    { value: 'STRENGTH',          label: 'Розвиток сили' },
    { value: 'STRENGTH_AND_MASS', label: 'Сила + Маса' },
  ];

  experienceLevels = [
    { value: 'BEGINNER',     label: 'Початківець' },
    { value: 'RETURNING',    label: 'Повертаюсь після паузи' },
    { value: 'INTERMEDIATE', label: 'Середній' },
    { value: 'ADVANCED',     label: 'Просунутий' },
  ];

  planTypes = [
    { value: 'HYPERTROPHY',          label: 'Гіпертрофія — 8 тижнів' },
    { value: 'STRENGTH',             label: 'Сила — 10 тижнів' },
    { value: 'STRENGTH_HYPERTROPHY', label: 'Сила + Маса — 9 тижнів' },
    { value: 'FAT_LOSS',             label: 'Спалення жиру — 8 тижнів' },
    { value: 'ENDURANCE',            label: 'Витривалість — 8 тижнів' },
  ];

  equipmentOptions = [
    { value: 'BARBELL',    label: 'Штанга' },
    { value: 'DUMBBELL',   label: 'Гантелі' },
    { value: 'MACHINE',    label: 'Тренажери' },
    { value: 'BODYWEIGHT', label: 'Вага тіла' },
    { value: 'PULL_UP',    label: 'Турнік' },
    { value: 'CABLE',      label: 'Блоки / кабелі' },
    { value: 'BENCH',      label: 'Лава для жиму' },
  ];

  weekDays = [
    { key: 'mon', label: 'Пн', idx: 0 },
    { key: 'tue', label: 'Вт', idx: 1 },
    { key: 'wed', label: 'Ср', idx: 2 },
    { key: 'thu', label: 'Чт', idx: 3 },
    { key: 'fri', label: 'Пт', idx: 4 },
    { key: 'sat', label: 'Сб', idx: 5 },
    { key: 'sun', label: 'Нд', idx: 6 },
  ];

  constructor() {
    this.profileForm = this.fb.group({
      firstName:           ['', [Validators.required, Validators.minLength(2)]],
      lastName:            ['', [Validators.required, Validators.minLength(2)]],
      age:                 [null],
      height:              [null],
      currentWeight:       [null],
      targetWeight:        [null],
      goal:                ['MASS', Validators.required],
      experienceLevel:     ['BEGINNER', Validators.required],
      planType:            ['HYPERTROPHY', Validators.required],
      trainingDaysPerWeek: [3, [Validators.required, Validators.min(1), Validators.max(7)]],
      availableEquipment:  [[] as string[]]
    });
  }

  ngOnInit(): void {
    this.loadProfile();
    this.loadTrainedDays();
  }

  loadProfile(): void {
    this.isLoading.set(true);
    forkJoin({
      user:           this.userService.getUser(),
      profile:        this.profileService.getProfile(),
      weightProgress: this.profileService.getWeightProgress()
    }).subscribe({
      next: ({ user, profile, weightProgress }: any) => {
        this.profileForm.patchValue({
          firstName:           user.firstName || '',
          lastName:            user.lastName  || '',
          age:                 profile.age,
          height:              profile.height,
          currentWeight:       profile.currentWeight,
          targetWeight:        profile.targetWeight,
          goal:                profile.goal          || 'MASS',
          experienceLevel:     profile.level         || 'BEGINNER',
          planType:            profile.planType       || 'HYPERTROPHY',
          trainingDaysPerWeek: profile.workoutsPerWeek || 3,
        });
        this.weightProgress.set(weightProgress);
        this.profileForm.get('availableEquipment')?.setValue(profile.availableEquipment || []);
        this.isLoading.set(false);
      },
      error: () => {
        this.errorMessage.set('Не вдалося завантажити профіль');
        this.isLoading.set(false);
      }
    });
  }

  loadTrainedDays(): void {
    this.logService.getTrainingDaysThisWeek().subscribe({
      next: (res) => this.trainedDays.set(res.trainedDays),
      error: ()   => this.trainedDays.set([]) // не критично — просто порожній тиждень
    });
  }

  toggleEditMode(): void {
    this.isEditMode.update(v => !v);
    if (!this.isEditMode()) this.loadProfile();
  }

  onSubmit(): void {
    if (this.profileForm.invalid) return;
    this.isLoading.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');
    const v = this.profileForm.value;
    forkJoin({
      user:    this.userService.updateUser({ firstName: v.firstName, lastName: v.lastName }),
      profile: this.profileService.updateProfile({
        goal:               v.goal,
        level:              v.experienceLevel,
        planType:           v.planType,
        workoutsPerWeek:    v.trainingDaysPerWeek || 3,
        currentWeight:      v.currentWeight  || null,
        targetWeight:       v.targetWeight   || null,
        height:             v.height         || null,
        age:                v.age            || null,
        availableEquipment: v.availableEquipment || []
      })
    }).subscribe({
      next: () => {
        this.successMessage.set('Профіль успішно збережено!');
        this.isEditMode.set(false);
        this.isLoading.set(false);
        this.loadTrainedDays();
      },
      error: () => {
        this.errorMessage.set('Помилка збереження профілю');
        this.isLoading.set(false);
      }
    });
  }

  onEquipmentChange(event: any): void {
    const value = event.target.value;
    let current: string[] = this.profileForm.get('availableEquipment')?.value || [];
    current = event.target.checked
      ? [...current.filter(i => i !== value), value]
      : current.filter(i => i !== value);
    this.profileForm.get('availableEquipment')?.setValue(current);
  }

  isEquipmentSelected(equipment: string): boolean {
    return (this.profileForm.get('availableEquipment')?.value as string[] || []).includes(equipment);
  }

  isTrainingDay(idx: number): boolean {
    return this.trainedDays().includes(idx);
  }

  // ── view helpers ────────────────────────────────────────────────────────────

  getGoalLabel(value: string): string {
    return this.goals.find(g => g.value === value)?.label ?? value;
  }

  getLevelLabel(value: string): string {
    return this.experienceLevels.find(l => l.value === value)?.label ?? value;
  }

  getPlanTypeLabel(value: string): string {
    return this.planTypes.find(p => p.value === value)?.label ?? value;
  }

  getSelectedEquipment(): { value: string; label: string }[] {
    const selected: string[] = this.profileForm.get('availableEquipment')?.value || [];
    return this.equipmentOptions.filter(eq => selected.includes(eq.value));
  }

  getBmi(): string {
    const weight = this.profileForm.get('currentWeight')?.value;
    const height = this.profileForm.get('height')?.value;
    if (!weight || !height) return '—';
    return (weight / Math.pow(height / 100, 2)).toFixed(1);
  }

  getBmiLabel(): string {
    const bmi = parseFloat(this.getBmi());
    if (isNaN(bmi)) return '';
    if (bmi < 18.5) return 'Недостатня вага';
    if (bmi < 25)   return 'Норма';
    if (bmi < 30)   return 'Надлишкова вага';
    return 'Ожиріння';
  }

  getBmiClass(): string {
    const bmi = parseFloat(this.getBmi());
    if (isNaN(bmi)) return '';
    if (bmi < 18.5) return 'bmi-badge bmi-low';
    if (bmi < 25)   return 'bmi-badge bmi-normal';
    if (bmi < 30)   return 'bmi-badge bmi-high';
    return 'bmi-badge bmi-obese';
  }

  getBmiPercent(): number {
    const bmi = parseFloat(this.getBmi());
    if (isNaN(bmi)) return 0;
    return Math.min(Math.max(((bmi - 15) / 25) * 100, 0), 100);
  }

  getMotivationMessage(): string {
    const goal = this.profileForm.get('goal')?.value;
    const map: Record<string, string> = {
      MASS:              'Кожне тренування — це цеглинка до твоєї кращої версії. Не зупиняйся.',
      LOSS:              'Результат складається з маленьких рішень. Ти вже зробив правильне.',
      ENDURANCE:         'Витривалість — це не те, що ти маєш. Це те, що ти будуєш щодня.',
      STRENGTH:          'Сила — це не лише про м\'язи. Це характер, виплавлений важкою роботою.',
      STRENGTH_AND_MASS: 'Поєднання сили і маси — найамбітніша ціль. Ти готовий до неї.',
    };
    return map[goal] ?? 'Кожне тренування наближає тебе до мети. Тримай темп!';
  }
}
