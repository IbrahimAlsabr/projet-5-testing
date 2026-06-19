describe('Register', () => {
	beforeEach(() => {
		cy.visit('/register');
	});

	it('displays the registration form', () => {
		cy.get('mat-card-title').should('contain', 'Register');
		cy.get('input[formControlName=firstName]').should('exist');
		cy.get('input[formControlName=lastName]').should('exist');
		cy.get('input[formControlName=email]').should('exist');
		cy.get('input[formControlName=password]').should('exist');
		cy.get('button[type=submit]').should('be.disabled');
	});

	it('redirects to login page on successful registration', () => {
		cy.intercept('POST', '/api/auth/register', { statusCode: 200, body: {} }).as('register');

		cy.get('input[formControlName=firstName]').type('John');
		cy.get('input[formControlName=lastName]').type('Doe');
		cy.get('input[formControlName=email]').type('newuser@test.com');
		cy.get('input[formControlName=password]').type('password123');
		cy.get('button[type=submit]').click();

		cy.url().should('include', '/login');
	});

	it('shows an error message when email is already taken', () => {
		cy.intercept('POST', '/api/auth/register', { statusCode: 400 }).as('registerFail');

		cy.get('input[formControlName=firstName]').type('John');
		cy.get('input[formControlName=lastName]').type('Doe');
		cy.get('input[formControlName=email]').type('existing@test.com');
		cy.get('input[formControlName=password]').type('password123');
		cy.get('button[type=submit]').click();

		cy.get('.error').should('be.visible').and('contain', 'An error occurred');
	});
});
