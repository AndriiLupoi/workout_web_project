import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProgressService, ExerciseProgressItem, PrItem, ProgressResponse } from './progress.service';

@Component({
  selector: 'app-progress',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './progress.html',
  styleUrl: './progress.css'
})
export class ProgressComponent implements OnInit {
  private progressService = inject(ProgressService);

  data         = signal<ProgressResponse | null>(null);
  loading      = signal(true);
  error        = signal<string | null>(null);
  selectedExercise = signal<ExerciseProgressItem | null>(null);

  ngOnInit(): void {
    this.progressService.getProgress().subscribe({
      next: (d) => {
        this.data.set(d);
        if (d.exerciseProgress.length > 0) {
          this.selectedExercise.set(d.exerciseProgress[0]);
        }
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Помилка завантаження прогресу');
        this.loading.set(false);
      }
    });
  }

  selectExercise(ex: ExerciseProgressItem): void {
    this.selectedExercise.set(ex);
  }

  // SVG графік — повертає path d= для лінії
  buildLinePath(entries: { weight: number }[], width: number, height: number): string {
    if (entries.length < 2) return '';
    const weights = entries.map(e => e.weight);
    const minW = Math.min(...weights);
    const maxW = Math.max(...weights);
    const range = maxW - minW || 1;
    const padX = 40;
    const padY = 20;
    const w = width - padX * 2;
    const h = height - padY * 2;

    return entries.map((e, i) => {
      const x = padX + (i / (entries.length - 1)) * w;
      const y = padY + h - ((e.weight - minW) / range) * h;
      return `${i === 0 ? 'M' : 'L'} ${x.toFixed(1)} ${y.toFixed(1)}`;
    }).join(' ');
  }

  buildAreaPath(entries: { weight: number }[], width: number, height: number): string {
    if (entries.length < 2) return '';
    const weights = entries.map(e => e.weight);
    const minW = Math.min(...weights);
    const maxW = Math.max(...weights);
    const range = maxW - minW || 1;
    const padX = 40;
    const padY = 20;
    const w = width - padX * 2;
    const h = height - padY * 2;
    const bottom = padY + h;

    const points = entries.map((e, i) => {
      const x = padX + (i / (entries.length - 1)) * w;
      const y = padY + h - ((e.weight - minW) / range) * h;
      return { x, y };
    });

    const line = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x.toFixed(1)} ${p.y.toFixed(1)}`).join(' ');
    const close = ` L ${points[points.length - 1].x.toFixed(1)} ${bottom} L ${points[0].x.toFixed(1)} ${bottom} Z`;
    return line + close;
  }

  getYLabels(entries: { weight: number }[], count = 4): number[] {
    const weights = entries.map(e => e.weight);
    const minW = Math.min(...weights);
    const maxW = Math.max(...weights);
    const step = (maxW - minW) / (count - 1) || 5;
    return Array.from({ length: count }, (_, i) => Math.round(minW + step * i));
  }

  getYPos(val: number, entries: { weight: number }[], height: number): number {
    const weights = entries.map(e => e.weight);
    const minW = Math.min(...weights);
    const maxW = Math.max(...weights);
    const range = maxW - minW || 1;
    const padY = 20;
    const h = height - padY * 2;
    return padY + h - ((val - minW) / range) * h;
  }

  getXPos(i: number, total: number, width: number): number {
    const padX = 40;
    return padX + (i / Math.max(total - 1, 1)) * (width - padX * 2);
  }

  getTrend(entries: { weight: number }[]): 'up' | 'down' | 'same' {
    if (entries.length < 2) return 'same';
    const first = entries[0].weight;
    const last  = entries[entries.length - 1].weight;
    if (last > first) return 'up';
    if (last < first) return 'down';
    return 'same';
  }

  getDiff(entries: { weight: number }[]): string {
    if (entries.length < 2) return '0';
    const diff = entries[entries.length - 1].weight - entries[0].weight;
    return (diff >= 0 ? '+' : '') + diff.toFixed(1);
  }

  isMaxWeight(entry: { weight: number }, entries: { weight: number }[]): boolean {
    return entry.weight === Math.max(...entries.map(e => e.weight));
  }

  protected readonly Math = Math;

  getBodyTrend(entries: { weight: number }[]): 'up' | 'down' | 'same' {
    if (entries.length < 2) return 'same';
    const first = entries[0].weight;
    const last  = entries[entries.length - 1].weight;
    // для ваги тіла: схуднення = 'down' (червоний) або 'up' (зелений) залежно від цілі
    // але в нас немає цілі тут, тому просто показуємо напрямок
    if (last < first) return 'down';
    if (last > first) return 'up';
    return 'same';
  }

  getBodyDiff(entries: { weight: number }[]): string {
    if (entries.length < 2) return '0';
    const diff = entries[entries.length - 1].weight - entries[0].weight;
    return (diff >= 0 ? '+' : '') + diff.toFixed(1);
  }
}
