import {Component, signal} from '@angular/core';
import {CommonModule} from '@angular/common';

import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatChipsModule} from '@angular/material/chips';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

import {Image, ImageService} from '../services/image';

@Component({
  selector: 'app-gallery',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatChipsModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './gallery.html',
  styleUrls: ['./gallery.css']
})
export class GalleryComponent {

  images = signal<Image[]>([]);
  predicting = signal<Set<string>>(new Set());

  constructor(private imageService: ImageService) {
    this.loadImages();
  }

  loadImages() {
    this.imageService.list().subscribe(data => {
      this.images.set(data);
    });
  }

  predict(id: string) {
    this.predicting.update(s => new Set(s).add(id));

    this.imageService.predict(id).subscribe(() => {
      this.predicting.update(s => {
        const next = new Set(s);
        next.delete(id);
        return next;
      });
      this.loadImages();
    });
  }

  imageUrl(id: string): string {
    return this.imageService.imageUrl(id);
  }

  normalizeConfidence(img: Image): number | null {
    return img.confidence === null ? null : Math.round(img.confidence * 100);
  }
}
