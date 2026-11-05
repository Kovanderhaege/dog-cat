import {Component, OnDestroy, signal} from '@angular/core';
import {RouterLink} from '@angular/router';
import {HttpErrorResponse} from '@angular/common/http';
import {finalize} from 'rxjs';
import {ImageService} from '../services/image';

const uploadErrors: Partial<Record<number, string>> = {
  400: 'Le fichier est illisible. Choisissez une image JPG ou PNG valide.',
  413: 'L’image est trop lourde. Réduisez sa taille puis réessayez.',
  422: 'Image refusée : la vérification a détecté un contenu potentiellement explicite ou violent.',
  503: 'La vérification du contenu est temporairement indisponible. Réessayez plus tard.',
};

@Component({
  selector: 'app-upload', standalone: true, imports: [RouterLink],
  templateUrl: './upload.html', styleUrl: './upload.css'
})
export class UploadComponent implements OnDestroy {
  readonly selectedFile = signal<File | undefined>(undefined);
  readonly preview = signal('');
  readonly message = signal('');
  readonly error = signal('');
  readonly uploading = signal(false);
  constructor(private imageService: ImageService) {}
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    this.clearPreview();
    this.selectedFile.set(undefined);
    this.message.set('');
    this.error.set('');
    if (!file) return;
    if (!['image/jpeg', 'image/png'].includes(file.type)) {
      this.error.set('Choisissez une image au format JPG ou PNG.');
      input.value = '';
      return;
    }
    this.selectedFile.set(file);
    this.preview.set(URL.createObjectURL(file));
  }
  upload(): void {
    const file = this.selectedFile();
    if (!file || this.uploading()) return;
    this.uploading.set(true);
    this.message.set('');
    this.error.set('');
    this.imageService.upload(file).pipe(finalize(() => this.uploading.set(false))).subscribe({
      next: () => {
        this.message.set('Image importée ! Retrouvez-la dans la galerie pour lancer son évaluation.');
        this.selectedFile.set(undefined);
      },
      error: (response: HttpErrorResponse) => this.error.set(
        uploadErrors[response.status] ?? 'L’import a échoué. Vérifiez que le serveur est disponible, puis réessayez.'
      )
    });
  }
  private clearPreview(): void {
    if (this.preview()) URL.revokeObjectURL(this.preview());
    this.preview.set('');
  }
  ngOnDestroy(): void { this.clearPreview(); }
}
