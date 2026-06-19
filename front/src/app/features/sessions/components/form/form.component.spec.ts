import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from 'src/app/services/session.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';
import { FormComponent } from './form.component';

describe('FormComponent', () => {
  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'A great yoga session',
    date: new Date('2024-01-15'),
    teacher_id: 1,
    users: []
  };

  const mockTeacher = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const buildModuleConfig = (isAdmin: boolean) => ({
    imports: [
      RouterTestingModule,
      HttpClientModule,
      MatCardModule,
      MatIconModule,
      MatFormFieldModule,
      MatInputModule,
      ReactiveFormsModule,
      MatSnackBarModule,
      MatSelectModule,
      NoopAnimationsModule
    ],
    declarations: [FormComponent],
    providers: [
      {
        provide: SessionService,
        useValue: { sessionInformation: { admin: isAdmin } }
      },
      {
        provide: SessionApiService,
        useValue: {
          detail: jest.fn().mockReturnValue(of(mockSession)),
          create: jest.fn().mockReturnValue(of(mockSession)),
          update: jest.fn().mockReturnValue(of(mockSession))
        }
      },
      {
        provide: TeacherService,
        useValue: { all: jest.fn().mockReturnValue(of([mockTeacher])) }
      },
      {
        provide: MatSnackBar,
        useValue: { open: jest.fn() }
      },
      {
        provide: ActivatedRoute,
        useValue: { snapshot: { paramMap: { get: () => '1' } } }
      }
    ]
  });

  // ─── Create mode (admin) ───────────────────────────────────────────────────

  describe('Create mode (admin)', () => {
    let component: FormComponent;
    let fixture: ComponentFixture<FormComponent>;
    let router: Router;
    let sessionApiService: SessionApiService;
    let snackBar: MatSnackBar;

    beforeEach(async () => {
      await TestBed.configureTestingModule(buildModuleConfig(true)).compileComponents();

      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      router = TestBed.inject(Router);
      sessionApiService = TestBed.inject(SessionApiService);
      snackBar = TestBed.inject(MatSnackBar);

      // URL without 'update' → create mode
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      jest.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not set onUpdate for create mode', () => {
      expect(component.onUpdate).toBe(false);
    });

    it('should initialize an empty form', () => {
      expect(component.sessionForm).toBeDefined();
      expect(component.sessionForm?.get('name')?.value).toBe('');
      expect(component.sessionForm?.get('description')?.value).toBe('');
      expect(component.sessionForm?.get('teacher_id')?.value).toBe('');
    });

    it('should call create on submit and navigate', () => {
      component.sessionForm?.setValue({
        name: 'Test Session',
        date: '2024-01-15',
        teacher_id: 1,
        description: 'Description'
      });
      component.submit();
      expect(sessionApiService.create).toHaveBeenCalled();
      expect(snackBar.open).toHaveBeenCalledWith('Session created !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  // ─── Update mode (admin) ───────────────────────────────────────────────────

  describe('Update mode (admin)', () => {
    let component: FormComponent;
    let fixture: ComponentFixture<FormComponent>;
    let router: Router;
    let sessionApiService: SessionApiService;
    let snackBar: MatSnackBar;

    beforeEach(async () => {
      await TestBed.configureTestingModule(buildModuleConfig(true)).compileComponents();

      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      router = TestBed.inject(Router);
      sessionApiService = TestBed.inject(SessionApiService);
      snackBar = TestBed.inject(MatSnackBar);

      // URL with 'update' → update mode
      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/update/1');
      jest.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      fixture.detectChanges();
    });

    it('should set onUpdate to true', () => {
      expect(component.onUpdate).toBe(true);
    });

    it('should fetch existing session and populate form', () => {
      expect(sessionApiService.detail).toHaveBeenCalledWith('1');
      expect(component.sessionForm?.get('name')?.value).toBe(mockSession.name);
      expect(component.sessionForm?.get('description')?.value).toBe(mockSession.description);
      expect(component.sessionForm?.get('teacher_id')?.value).toBe(mockSession.teacher_id);
    });

    it('should call update on submit and navigate', () => {
      component.submit();
      expect(sessionApiService.update).toHaveBeenCalledWith('1', expect.any(Object));
      expect(snackBar.open).toHaveBeenCalledWith('Session updated !', 'Close', { duration: 3000 });
      expect(router.navigate).toHaveBeenCalledWith(['sessions']);
    });
  });

  // ─── Non-admin redirect ────────────────────────────────────────────────────

  describe('Non-admin user', () => {
    let component: FormComponent;
    let fixture: ComponentFixture<FormComponent>;
    let router: Router;

    beforeEach(async () => {
      await TestBed.configureTestingModule(buildModuleConfig(false)).compileComponents();

      fixture = TestBed.createComponent(FormComponent);
      component = fixture.componentInstance;
      router = TestBed.inject(Router);

      jest.spyOn(router, 'url', 'get').mockReturnValue('/sessions/create');
      jest.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      fixture.detectChanges();
    });

    it('should redirect to /sessions if user is not admin', () => {
      expect(router.navigate).toHaveBeenCalledWith(['/sessions']);
    });
  });
});
