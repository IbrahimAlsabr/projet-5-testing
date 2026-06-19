const mockSessionCreate = {
	id: 1,
	name: 'Morning Yoga',
	description: 'A refreshing morning session',
	date: '2026-07-01T00:00:00.000Z',
	teacher_id: 1,
	users: [],
	createdAt: '2026-06-01T00:00:00.000Z',
	updatedAt: '2026-06-01T00:00:00.000Z',
};

const mockTeachers = [
	{ id: 1, firstName: 'Alice', lastName: 'Smith', createdAt: '2026-01-01T00:00:00.000Z', updatedAt: '2026-01-01T00:00:00.000Z' },
];

describe('Session form - Create', () => {
	beforeEach(() => {
		cy.intercept('GET', '/api/teacher', mockTeachers).as('teachers');
		cy.loginAsAdmin([mockSession]);
		cy.get('button').contains('Create').click();
	});

	it('displays the "Create session" title', () => {
		cy.get('h1').should('contain', 'Create session');
	});

	it('creates a session and redirects to the sessions list', () => {
		cy.intercept('POST', '/api/session', { body: { ...mockSession, id: 2 } }).as('createSession');
		cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

		cy.get('input[formControlName=name]').type('New Yoga Session');
		cy.get('input[formControlName=date]').type('2026-07-15');
		cy.get('mat-select[formControlName=teacher_id]').click();
		cy.get('mat-option').first().click();
		cy.get('textarea[formControlName=description]').type('A great session');
		cy.get('button[type=submit]').click();

		cy.url().should('include', '/sessions');
	});

	it('keeps Save button disabled when the form is empty', () => {
		cy.get('button[type=submit]').should('be.disabled');
	});
});

describe('Session form - Update', () => {
	beforeEach(() => {
		cy.intercept('GET', '/api/teacher', mockTeachers).as('teachers');
		cy.intercept('GET', '/api/session/1', mockSession).as('sessionDetail');
		cy.loginAsAdmin([mockSession]);
		cy.get('button').contains('Edit').click();
	});

	it('displays the "Update session" title with pre-filled data', () => {
		cy.get('h1').should('contain', 'Update session');
		cy.get('input[formControlName=name]').should('have.value', 'Morning Yoga');
	});

	it('updates a session and redirects to the sessions list', () => {
		cy.intercept('PUT', '/api/session/1', { body: { ...mockSession, name: 'Updated Yoga' } }).as('updateSession');
		cy.intercept('GET', '/api/session', [mockSession]).as('sessions');

		cy.get('input[formControlName=name]').clear().type('Updated Yoga');
		cy.get('button[type=submit]').click();

		cy.url().should('include', '/sessions');
	});
});
