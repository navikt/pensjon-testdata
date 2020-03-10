//Setup proxy configuration for use in development environment, in production NGINX will handle proxy configuration
const proxy = require('http-proxy-middleware');
module.exports = function (app) {
    app.use(proxy('/api', {
        target: 'http://localhost:8080',
        logLevel: 'debug',
        prependPath: true
    }));
};