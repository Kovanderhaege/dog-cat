import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ImageService } from '../services/image';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './upload.html'
})
export class UploadComponent {

  selectedFile?: File;
  message = '';

  constructor(private imageService: ImageService) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  upload() {
    if (!this.selectedFile) return;

    this.imageService.upload(this.selectedFile)
      .subscribe(id => {
        this.message = `Uploaded with id: ${id}`;
      });
  }
}