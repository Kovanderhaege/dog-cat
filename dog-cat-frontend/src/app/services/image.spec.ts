import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ImageService } from './image';

describe('ImageService', () => {
  it('should be created', () => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    expect(TestBed.inject(ImageService)).toBeTruthy();
  });
});
