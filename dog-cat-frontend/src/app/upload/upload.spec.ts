import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { UploadComponent } from './upload';

describe('UploadComponent', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [UploadComponent],
    providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
  }));
  afterEach(() => TestBed.inject(HttpTestingController).verify());
  it('updates the view after an HTTP response without Zone.js', async () => {
    const fixture = TestBed.createComponent(UploadComponent);
    const http = TestBed.inject(HttpTestingController);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelector('button')!.disabled).toBe(true);
    fixture.componentInstance.selectedFile.set(new File(['image'], 'cat.png'));
    await fixture.whenStable();
    element.querySelector('button')!.click();
    const request = http.expectOne({ method: 'POST', url: '/images' });
    expect(request.request.body.get('file').name).toBe('cat.png');
    request.flush('image-id');
    await fixture.whenStable();
    expect(element.textContent).toContain('Image importée !');
    expect(element.querySelector('a')!.getAttribute('href')).toBe('/gallery');
  });
  it.each([
    [413, 'trop lourde'],
    [422, 'contenu potentiellement explicite ou violent'],
    [503, 'temporairement indisponible']
  ] as const)('explains upload failure %i', async (status, message) => {
    const fixture = TestBed.createComponent(UploadComponent);
    fixture.componentInstance.selectedFile.set(new File(['image'], 'cat.png'));
    fixture.componentInstance.upload();
    TestBed.inject(HttpTestingController).expectOne('/images').flush(null, { status, statusText: 'Error' });
    await fixture.whenStable();
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain(message);
  });
  it('keeps the selected file available to retry a failed upload', async () => {
    const fixture = TestBed.createComponent(UploadComponent);
    const http = TestBed.inject(HttpTestingController);
    await fixture.whenStable();
    fixture.componentInstance.selectedFile.set(new File(['image'], 'cat.png'));
    fixture.componentInstance.upload();
    fixture.componentInstance.upload();
    http.expectOne('/images')
      .flush(null, { status: 500, statusText: 'Server error' });
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelector('[role="alert"]')).not.toBeNull();
    expect(element.querySelector('button')!.disabled).toBe(false);
    expect(fixture.componentInstance.selectedFile()?.name).toBe('cat.png');
  });
});
