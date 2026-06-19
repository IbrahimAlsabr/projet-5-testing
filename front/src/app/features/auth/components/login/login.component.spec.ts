import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { expect } from '@jest/globals';
import { SessionService } from 'src/app/services/session.service';

import { LoginComponent } from './login.component';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent', () => {
	let component: LoginComponent;
	let fixture: ComponentFixture<LoginComponent>;

	// Mock services
	let mockAuthService: any;
	let mockSessionService: any;
	let mockRouter: any;

	const mockSessionData = {
		token: 'abc123',
		username: 'john',
		admin: false,
	};

	beforeEach(async () => {
		mockAuthService = {
			login: jest.fn()
		};
		mockSessionService = {
			logIn: jest.fn()
		};
		mockRouter = {
			navigate: jest.fn()
		};


		await TestBed.configureTestingModule({
			declarations: [LoginComponent],
			providers: [
				{ provide: SessionService, useValue: mockSessionService },
				{ provide: AuthService, useValue: mockAuthService },
				{ provide: Router, useValue: mockRouter },
			],
			imports: [
				RouterTestingModule,
				BrowserAnimationsModule,
				HttpClientModule,
				MatCardModule,
				MatIconModule,
				MatFormFieldModule,
				MatInputModule,
				ReactiveFormsModule]
		})
			.compileComponents();
		fixture = TestBed.createComponent(LoginComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	describe('form validation', () => {
		it('should start with empty email and password', () => {
			expect(component.form.value).toEqual({ email: '', password: '' });
		});

		it('should validate email field', () => {
			const emailControl = component.form.get('email');
			expect(emailControl?.valid).toBe(false);
			emailControl?.setValue('invalid-email');
			expect(emailControl?.valid).toBe(false);
			emailControl?.setValue('valid@email.com');
			expect(emailControl?.valid).toBe(true);
		});

		it('should validate password field', () => {
			const passwordControl = component.form.get('password');
			expect(passwordControl?.valid).toBe(false);
			passwordControl?.setValue('0');
			expect(passwordControl?.valid).toBe(false);
			passwordControl?.setValue('valid-password');
			expect(passwordControl?.valid).toBe(true);
		});
	});


	describe('login behaviour', () => {
		it('should handle login error', () => {
			component.form.setValue({ email: 'john@test.com', password: 'wrong' });
			mockAuthService.login.mockReturnValue(throwError(() => new Error('401')));
			component.submit();
			fixture.detectChanges();
			expect(component.onError).toBe(true);
			const compiled = fixture.nativeElement as HTMLElement;
			expect(compiled.querySelector('.error')?.textContent)
				.toContain('An error occurred');
		});

		it('should navigate to sessions page on success', () => {
			component.form.setValue({ email: 'john@test.com', password: 'Password1' });
			mockAuthService.login.mockReturnValue(of(mockSessionData));
			component.submit();
			expect(component.onError).toBe(false);
			expect(mockSessionService.logIn).toHaveBeenCalledWith(mockSessionData);
			expect(mockRouter.navigate).toHaveBeenCalledWith(['/sessions']);
		});

	});

});
