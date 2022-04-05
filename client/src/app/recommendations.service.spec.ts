import { TestBed, inject } from '@angular/core/testing';

import { RecommendationsService } from './recommendations.service';

describe('RecommendationsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RecommendationsService]
    });
  });

  it('should be created', inject([RecommendationsService], (service: RecommendationsService) => {
    expect(service).toBeTruthy();
  }));
});
