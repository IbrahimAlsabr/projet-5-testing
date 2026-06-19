const mockSessions = [
	{
		id: 1,
		name: 'Morning Yoga',
		description: 'A refreshing morning session',
		date: '2026-07-01T00:00:00.000Z',
		teacher_id: 1,
		users: [],
		createdAt: '2026-06-01T00:00:00.000Z',
		updatedAt: '2026-06-01T00:00:00.000Z',
	},
];

describe('Sessions list - Admin', () => {
	beforeEach(() => {
		cy.loginAsAdmin(mockSessions);
	});

	it('shows the Create button for admin users', () => {
		cy.get('button').contains('Create').should('be.visible');
	});

	it('shows the Edit button on each session card for admin users', () => {
		cy.get('button').contains('Edit').should('be.visible');
	});

	it('displays session names in the list', () => {
		cy.get('mat-card-title').contains('Morning Yoga').should('be.visible');
	});
});

describe('Sessions list - Regular user', () => {
	beforeEach(() => {
		cy.loginAsUser(mockSessions);
	});

	it('does not show the Create button for regular users', () => {
		cy.get('button').contains('Create').should('not.exist');
	});
});
