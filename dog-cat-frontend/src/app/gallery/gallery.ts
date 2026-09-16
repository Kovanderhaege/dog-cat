import {Component, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {finalize, switchMap} from 'rxjs';
import {Image, ImageService} from '../services/image';

@Component({
  selector: 'app-gallery', standalone: true, imports: [CommonModule, RouterLink],
  templateUrl: './gallery.html', styleUrl: './gallery.css'
})
export class GalleryComponent implements OnInit {
  readonly images = signal<Image[]>([]);
  readonly loading = signal(false);
  readonly error = signal('');
  readonly evaluating = signal<string[]>([]);
  constructor(private imageService: ImageService) {}
  ngOnInit(): void { this.loadImages(); }
  loadImages(): void {
    this.loading.set(true);
    this.error.set('');
    this.imageService.list().pipe(finalize(() => this.loading.set(false))).subscribe({
      next: data => this.images.set(data),
      error: () => this.error.set('Impossible de charger les images. Vérifiez que le serveur est démarré, puis réessayez.')
    });
  }
  predict(id: string): void {
    if (this.evaluating().includes(id)) return;
    this.evaluating.update(ids => [...ids, id]);
    this.error.set('');
    this.imageService.predict(id).pipe(
      switchMap(() => this.imageService.list()),
      finalize(() => this.evaluating.update(ids => ids.filter(value => value !== id)))
    ).subscribe({
      next: data => this.images.set(data),
      error: () => this.error.set('Impossible de terminer l’évaluation ou de récupérer son résultat. Réessayez dans un instant.')
    });
  }
  imageUrl(id: string): string { return this.imageService.imageUrl(id); }
}
