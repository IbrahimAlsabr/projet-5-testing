describe('Not Found page', () => {
	it('displays "Page not found !" when visiting /404 directly', () => {
		cy.visit('/404');
		cy.get('h1').should('contain', 'Page not found !');
	});

	it('redirects to /404 when visiting an unknown route', () => {
		cy.visit('/this-route-does-not-exist');
		cy.url().should('include', '/404');
		cy.get('h1').should('contain', 'Page not found !');
	});
});
