import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Image, ImageService} from '../services/image';

@Component({
  selector: 'app-gallery',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './gallery.html'
})
export class GalleryComponent implements OnInit {

  images: Image[] = [];

  constructor(private imageService: ImageService) {}

  ngOnInit(): void {
    this.loadImages();
  }

  loadImages(): void {
    this.imageService.list().subscribe(data => {
      this.images = data;
    });
  }

  predict(id: string): void {
    this.imageService.predict(id).subscribe(() => {
      this.loadImages();
    });
  }

  imageUrl(id: string): string {
  return this.imageService.imageUrl(id);
}
}
