/**
 * @type {Cypress.PluginConfig}
 */
export default (on, config) => {
	// Register code coverage tasks
	require('@cypress/code-coverage/task')(on, config);
	return config;
};
