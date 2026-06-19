import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatToolbarModule } from '@angular/material/toolbar';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { SessionService } from './services/session.service';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let mockSessionService: any;
  let router: Router;

  beforeEach(async () => {
    mockSessionService = {
      $isLogged: jest.fn().mockReturnValue(of(true)),
      logOut: jest.fn()
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, MatToolbarModule],
      declarations: [AppComponent],
      providers: [
        { provide: SessionService, useValue: mockSessionService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should return isLogged observable from sessionService', (done) => {
    component.$isLogged().subscribe(value => {
      expect(value).toBe(true);
      done();
    });
    expect(mockSessionService.$isLogged).toHaveBeenCalled();
  });

  it('should logout and navigate to home', () => {
    component.logout();
    expect(mockSessionService.logOut).toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['']);
  });
});
