import {Component, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ImageService} from '../services/image';
import {forkJoin} from 'rxjs';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './upload.html',
  styleUrls: ['./upload.css']
})
export class UploadComponent {

  files = signal<File[]>([]);
  uploading = signal(false);
  message = signal<string | null>(null);

  constructor(private imageService: ImageService) {}

  onFilesSelected(files: FileList | null) {
    if (!files) return;
    this.files.set(Array.from(files));
  }

  uploadAll() {
    const files = this.files();
    if (!files.length) return;

    this.uploading.set(true);

    forkJoin(
      files.map(f => this.imageService.upload(f))
    ).subscribe({
      next: ids => {
        this.message.set(`${ids.length} images uploaded`);
        this.files.set([]);
        this.uploading.set(false);
      },
      error: () => {
        this.message.set('Upload failed');
        this.uploading.set(false);
      }
    });
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    event.stopPropagation();

    if (!event.dataTransfer?.files?.length) return;
    this.files.set(Array.from(event.dataTransfer.files));
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
  }
}
