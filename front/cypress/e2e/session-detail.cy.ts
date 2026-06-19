const mockTeacher = {
	id: 1,
	firstName: 'Alice',
	lastName: 'Smith',
	createdAt: '2026-01-01T00:00:00.000Z',
	updatedAt: '2026-01-01T00:00:00.000Z',
};

const mockSession = {
	id: 1,
	name: 'Morning Yoga',
	description: 'A refreshing morning session',
	date: '2026-07-01T00:00:00.000Z',
	teacher_id: 1,
	users: [],
	createdAt: '2026-06-01T00:00:00.000Z',
	updatedAt: '2026-06-01T00:00:00.000Z',
};

describe('Session detail - Admin', () => {
	beforeEach(() => {
		cy.intercept('GET', '/api/session/1', mockSession).as('sessionDetail');
		cy.intercept('GET', '/api/teacher/1', mockTeacher).as('teacher');
		cy.loginAsAdmin([mockSession]);
		cy.get('button').contains('Detail').click();
		cy.url().should('include', '/detail/');
	});

	it('shows the Delete button for admin users', () => {
		cy.get('button').contains('Delete').should('be.visible');
	});

	it('deletes the session and redirects to the sessions list', () => {
		cy.intercept('DELETE', '/api/session/1', {}).as('delete');
		cy.intercept('GET', '/api/session', []).as('sessions');

		cy.get('button').contains('Delete').click();

		cy.url().should('include', '/sessions');
		cy.url().should('not.include', '/detail/');
	});

	it('does not show the Participate button for admin users', () => {
		cy.get('button').contains('Participate').should('not.exist');
	});
});

describe('Session detail - Regular user not participating', () => {
	beforeEach(() => {
		cy.intercept('GET', '/api/session/1', mockSession).as('sessionDetail');
		cy.intercept('GET', '/api/teacher/1', mockTeacher).as('teacher');
		cy.loginAsUser([mockSession]);
		cy.get('button').contains('Detail').click();
		cy.url().should('include', '/detail/');
	});

	it('shows the Participate button when user is not participating', () => {
		cy.get('button').contains('Participate').should('be.visible');
	});

	it('participates in a session and shows the Do not participate button', () => {
		cy.intercept('POST', '/api/session/1/participate/2', {}).as('participate');
		cy.intercept('GET', '/api/session/1', { ...mockSession, users: [2] }).as('sessionUpdated');

		cy.get('button').contains('Participate').click();

		cy.get('button').contains('Do not participate').should('be.visible');
	});
});

describe('Session detail - Regular user already participating', () => {
	beforeEach(() => {
		const participatingSession = { ...mockSession, users: [2] };
		cy.intercept('GET', '/api/session/1', participatingSession).as('sessionDetail');
		cy.intercept('GET', '/api/teacher/1', mockTeacher).as('teacher');
		cy.loginAsUser([participatingSession]);
		cy.get('button').contains('Detail').click();
		cy.url().should('include', '/detail/');
	});

	it('shows the "Do not participate" button when user is already participating', () => {
		cy.get('button').contains('Do not participate').should('be.visible');
	});
});
