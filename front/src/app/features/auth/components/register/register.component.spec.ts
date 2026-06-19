import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { expect } from '@jest/globals';

import { RegisterComponent } from './register.component';
import { AuthService } from '../../services/auth.service';
import { of, throwError } from 'rxjs';
import { Router } from '@angular/router';

describe('RegisterComponent', () => {
	let component: RegisterComponent;
	let fixture: ComponentFixture<RegisterComponent>;

	let mockAuthService: any;
	let mockRouter: any;
	const mockUserData = { id: 1, email: 'john@test.com' };

	beforeEach(async () => {
		mockAuthService = {
			register: jest.fn()
		};
		mockRouter = {
			navigate: jest.fn()
		};

		await TestBed.configureTestingModule({
			declarations: [RegisterComponent],
			providers: [
				FormBuilder,
				{ provide: AuthService, useValue: mockAuthService },
				{ provide: Router, useValue: mockRouter },
			],
			imports: [
				BrowserAnimationsModule,
				HttpClientModule,
				ReactiveFormsModule,
				MatCardModule,
				MatFormFieldModule,
				MatIconModule,
				MatInputModule
			]
		})
			.compileComponents();

		fixture = TestBed.createComponent(RegisterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should mark form invalid when required fields are missing', () => {
		component.form.setValue({
			firstName: '',
			lastName: '',
			email: '',
			password: ''
		});

		expect(component.form.valid).toBe(false);
	});

	it('should call AuthService.register on submit', () => {
		component.form.setValue({
			firstName: 'John',
			lastName: 'Doe',
			email: 'john@test.com',
			password: 'Password1'
		});

		mockAuthService.register.mockReturnValue(of(mockUserData));

		component.submit();

		expect(mockAuthService.register).toHaveBeenCalledWith({
			firstName: 'John',
			lastName: 'Doe',
			email: 'john@test.com',
			password: 'Password1'
		});
	});

	it('should set onError = true when register fails', () => {
		component.form.setValue({
			firstName: 'John',
			lastName: 'Doe',
			email: 'john@test.com',
			password: 'Password1'
		});

		mockAuthService.register.mockReturnValue(throwError(() => new Error('400')));

		component.submit();

		expect(component.onError).toBe(true);
	});
});
