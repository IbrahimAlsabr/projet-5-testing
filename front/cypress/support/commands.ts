// Custom Cypress commands

export const adminUser = {
  token: 'fake-token',
  type: 'Bearer',
  id: 1,
  username: 'yoga@studio.com',
  firstName: 'Admin',
  lastName: 'User',
  admin: true,
};

export const regularUser = {
  token: 'fake-token',
  type: 'Bearer',
  id: 2,
  username: 'user@studio.com',
  firstName: 'John',
  lastName: 'Doe',
  admin: false,
};

declare global {
  namespace Cypress {
    interface Chainable {
      loginAsAdmin(sessions?: object[]): Chainable<void>;
      loginAsUser(sessions?: object[]): Chainable<void>;
    }
  }
}

Cypress.Commands.add('loginAsAdmin', (sessions: object[] = []) => {
  cy.intercept('POST', '/api/auth/login', { body: adminUser }).as('loginAdmin');
  cy.intercept('GET', '/api/session', sessions).as('getSessions');
  cy.visit('/login');
  cy.get('input[formControlName=email]').type('yoga@studio.com');
  cy.get('input[formControlName=password]').type('test!1234');
  cy.get('button[type=submit]').click();
  cy.url().should('include', '/sessions');
});

Cypress.Commands.add('loginAsUser', (sessions: object[] = []) => {
  cy.intercept('POST', '/api/auth/login', { body: regularUser }).as('loginUser');
  cy.intercept('GET', '/api/session', sessions).as('getSessions');
  cy.visit('/login');
  cy.get('input[formControlName=email]').type('user@studio.com');
  cy.get('input[formControlName=password]').type('test!1234');
  cy.get('button[type=submit]').click();
  cy.url().should('include', '/sessions');
});
