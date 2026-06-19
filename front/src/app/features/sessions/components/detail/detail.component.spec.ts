import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { of } from 'rxjs';
import { SessionService } from '../../../../services/session.service';
import { TeacherService } from '../../../../services/teacher.service';
import { SessionApiService } from '../../services/session-api.service';
import { Session } from '../../interfaces/session.interface';
import { DetailComponent } from './detail.component';

describe('DetailComponent', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;
  let mockSessionApiService: any;
  let mockTeacherService: any;
  let mockSnackBar: any;
  let mockRouter: any;

  const mockTeacher = {
    id: 1,
    firstName: 'John',
    lastName: 'Teacher',
    createdAt: new Date(),
    updatedAt: new Date()
  };

  const mockSession: Session = {
    id: 1,
    name: 'Yoga Session',
    description: 'Test description',
    date: new Date(),
    teacher_id: 1,
    users: [1]
  };

  const mockSessionService = {
    sessionInformation: { admin: true, id: 1 }
  };

  const setupTestBed = async (session: Session = mockSession, isAdmin: boolean = true) => {
    mockSessionApiService = {
      detail: jest.fn().mockReturnValue(of(session)),
      delete: jest.fn().mockReturnValue(of(null)),
      participate: jest.fn().mockReturnValue(of(null)),
      unParticipate: jest.fn().mockReturnValue(of(null))
    };
    mockTeacherService = {
      detail: jest.fn().mockReturnValue(of(mockTeacher))
    };
    mockSnackBar = { open: jest.fn() };
    mockRouter = { navigate: jest.fn() };

    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MatSnackBarModule,
        MatCardModule,
        MatIconModule,
        ReactiveFormsModule
      ],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: { sessionInformation: { admin: isAdmin, id: 1 } } },
        { provide: SessionApiService, useValue: mockSessionApiService },
        { provide: TeacherService, useValue: mockTeacherService },
        { provide: MatSnackBar, useValue: mockSnackBar },
        { provide: Router, useValue: mockRouter },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await setupTestBed();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set sessionId and userId from route and sessionService', () => {
    expect(component.sessionId).toBe('1');
    expect(component.userId).toBe('1');
  });

  it('should set isAdmin from sessionService', () => {
    expect(component.isAdmin).toBe(true);
  });

  it('should load session and teacher on init', () => {
    expect(mockSessionApiService.detail).toHaveBeenCalledWith('1');
    expect(component.session).toEqual(mockSession);
    expect(mockTeacherService.detail).toHaveBeenCalledWith('1');
    expect(component.teacher).toEqual(mockTeacher);
  });

  it('should set isParticipate to true when user is in session.users', () => {
    expect(component.isParticipate).toBe(true);
  });

  it('should set isParticipate to false when user is NOT in session.users', async () => {
    const sessionWithoutUser: Session = { ...mockSession, users: [2, 3] };
    mockSessionApiService.detail.mockReturnValue(of(sessionWithoutUser));
    component.ngOnInit();
    expect(component.isParticipate).toBe(false);
  });

  it('should call window.history.back on back()', () => {
    const historyBackSpy = jest.spyOn(window.history, 'back').mockImplementation(() => {});
    component.back();
    expect(historyBackSpy).toHaveBeenCalled();
    historyBackSpy.mockRestore();
  });

  it('should delete session, show snackbar and navigate', () => {
    component.delete();
    expect(mockSessionApiService.delete).toHaveBeenCalledWith('1');
    expect(mockSnackBar.open).toHaveBeenCalledWith('Session deleted !', 'Close', { duration: 3000 });
    expect(mockRouter.navigate).toHaveBeenCalledWith(['sessions']);
  });

  it('should call participate and refresh session', () => {
    component.participate();
    expect(mockSessionApiService.participate).toHaveBeenCalledWith('1', '1');
    // detail is called once on init + once after participate
    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(2);
  });

  it('should call unParticipate and refresh session', () => {
    component.unParticipate();
    expect(mockSessionApiService.unParticipate).toHaveBeenCalledWith('1', '1');
    expect(mockSessionApiService.detail).toHaveBeenCalledTimes(2);
  });

});

// ─── Separate describe to avoid TestBed double-configure issue ────────────────
describe('DetailComponent (non-admin)', () => {
  let component: DetailComponent;
  let fixture: ComponentFixture<DetailComponent>;

  const mockSession: any = {
    id: 1,
    name: 'Yoga Session',
    description: 'Test',
    date: new Date(),
    teacher_id: 1,
    users: []
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule, MatSnackBarModule, MatCardModule, MatIconModule, ReactiveFormsModule],
      declarations: [DetailComponent],
      providers: [
        { provide: SessionService, useValue: { sessionInformation: { admin: false, id: 1 } } },
        { provide: SessionApiService, useValue: { detail: jest.fn().mockReturnValue(of(mockSession)), delete: jest.fn(), participate: jest.fn(), unParticipate: jest.fn() } },
        { provide: TeacherService, useValue: { detail: jest.fn().mockReturnValue(of({})) } },
        { provide: MatSnackBar, useValue: { open: jest.fn() } },
        { provide: Router, useValue: { navigate: jest.fn() } },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: { get: () => '1' } } } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should set isAdmin to false for non-admin user', () => {
    expect(component.isAdmin).toBe(false);
  });
});
