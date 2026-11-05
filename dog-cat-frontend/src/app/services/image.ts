import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Image {
  id: string;
  status: string;
  predictedLabel: string | null;
  confidence: number | null;
}

@Injectable({ providedIn: 'root' })
export class ImageService {

  private readonly imagesUrl = '/images';

  constructor(private http: HttpClient) {}

  upload(file: File): Observable<string> {
    const data = new FormData();
    data.append('file', file);
    return this.http.post<string>(this.imagesUrl, data);
  }

  list(): Observable<Image[]> {
    return this.http.get<Image[]>(this.imagesUrl);
  }

  predict(id: string): Observable<void> {
    return this.http.post<void>(`${this.imagesUrl}/${id}/predict`, {});
  }

  imageUrl(id: string): string {
    return `${this.imagesUrl}/${id}/file`;
  }
}