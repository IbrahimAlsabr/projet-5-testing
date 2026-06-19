const adminProfile = {
  id: 1,
  email: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'User',
  admin: true,
  password: 'hashed',
  createdAt: '2026-01-01T00:00:00.000Z',
  updatedAt: '2026-01-01T00:00:00.000Z',
};

const userProfile = {
  id: 2,
  email: 'user@studio.com',
  firstName: 'John',
  lastName: 'Doe',
  admin: false,
  password: 'hashed',
  createdAt: '2026-01-01T00:00:00.000Z',
  updatedAt: '2026-01-01T00:00:00.000Z',
};

describe('User profile - Admin', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/user/1', adminProfile).as('userProfile');
    cy.loginAsAdmin();
    cy.get('.link').contains('Account').click();
    cy.url().should('include', '/me');
  });

  it('displays "You are admin" for admin users', () => {
    cy.contains('You are admin').should('be.visible');
  });

  it('does not display the delete account button for admin users', () => {
    cy.get('button').contains('Detail').should('not.exist');
  });
});

describe('User profile - Regular user', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/user/2', userProfile).as('userProfile');
    cy.loginAsUser();
    cy.get('.link').contains('Account').click();
    cy.url().should('include', '/me');
  });

  it('displays the delete account button for regular users', () => {
    cy.get('button[color=warn]').should('be.visible');
  });

  it('deletes the account and redirects away from the profile page', () => {
    cy.intercept('DELETE', '/api/user/2', {}).as('deleteAccount');

    cy.get('button[color=warn]').click();

    cy.url().should('not.include', '/me');
    cy.get('.link').contains('Login').should('be.visible');
  });
});
