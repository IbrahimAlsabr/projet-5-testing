describe('Login', () => {
	it('logs in successfully and redirects to sessions', () => {
		cy.visit('/login');

		cy.intercept('POST', '/api/auth/login', {
			body: {
				id: 1,
				username: 'yoga@studio.com',
				firstName: 'Admin',
				lastName: 'User',
				admin: true,
				token: 'fake-token',
			},
		});

		cy.intercept({ method: 'GET', url: '/api/session' }, []).as('session');

		cy.get('input[formControlName=email]').type('yoga@studio.com');
		cy.get('input[formControlName=password]').type('test!1234{enter}{enter}');

		cy.url().should('include', '/sessions');
	});

	it('shows an error message when credentials are wrong', () => {
		cy.visit('/login');

		cy.intercept('POST', '/api/auth/login', { statusCode: 401 }).as('loginFail');

		cy.get('input[formControlName=email]').type('wrong@test.com');
		cy.get('input[formControlName=password]').type('wrongpassword');
		cy.get('button[type=submit]').click();

		cy.get('.error').should('be.visible').and('contain', 'An error occurred');
	});

	it('shows Login and Register links after logout', () => {
		cy.loginAsAdmin();

		cy.get('.link').contains('Logout').click();

		cy.get('.link').contains('Login').should('be.visible');
		cy.get('.link').contains('Register').should('be.visible');
	});
});
