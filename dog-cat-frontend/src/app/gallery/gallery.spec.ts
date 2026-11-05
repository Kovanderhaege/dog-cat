import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { GalleryComponent } from './gallery';

describe('GalleryComponent', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [GalleryComponent],
    providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
  }));
  afterEach(() => TestBed.inject(HttpTestingController).verify());
  it('updates the view after an HTTP response without Zone.js', async () => {
    const fixture = TestBed.createComponent(GalleryComponent);
    const http = TestBed.inject(HttpTestingController);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    http.expectOne('/images').flush([
      { id: '1', status: 'PENDING', predictedLabel: null, confidence: null }
    ]);
    await fixture.whenStable();
    expect(element.textContent).toContain('1 image');
    element.querySelector<HTMLButtonElement>('.evaluate')!.click();
    await fixture.whenStable();
    expect(element.querySelector<HTMLButtonElement>('.evaluate')!.disabled).toBe(true);
    fixture.componentInstance.predict('1');
    http.expectOne({ method: 'POST', url: '/images/1/predict' }).flush(null);
    http.expectOne('/images').flush([
      { id: '1', status: 'DONE', predictedLabel: 'cat', confidence: 0.9 }
    ]);
    await fixture.whenStable();
    expect(element.textContent).toContain('Chat');
    expect(element.querySelector('.evaluate')?.textContent).toContain('Réévaluer');
  });
  it('allows retrying an image after a model failure and a network error', async () => {
    const fixture = TestBed.createComponent(GalleryComponent);
    const http = TestBed.inject(HttpTestingController);
    await fixture.whenStable();
    http.expectOne('/images').flush([
      { id: 'failed', status: 'ERROR', predictedLabel: null, confidence: null }
    ]);
    await fixture.whenStable();
    const element = fixture.nativeElement as HTMLElement;
    const button = element.querySelector<HTMLButtonElement>('.evaluate')!;
    expect(button.textContent).toContain('Réessayer');
    button.click();
    http.expectOne('/images/failed/predict')
      .flush(null, { status: 503, statusText: 'Unavailable' });
    await fixture.whenStable();
    expect(button.disabled).toBe(false);
    expect(element.querySelector('[role="alert"]')).not.toBeNull();
    button.click();
    http.expectOne('/images/failed/predict').flush(null);
    http.expectOne('/images').flush([
      { id: 'failed', status: 'DONE', predictedLabel: 'dog', confidence: 0.95 }
    ]);
    await fixture.whenStable();
    expect(element.textContent).toContain('Chien');
    expect(element.querySelector('[role="alert"]')).toBeNull();
  });
});
