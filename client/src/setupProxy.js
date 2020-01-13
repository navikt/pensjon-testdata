//Setup proxy configuration for use in development environment, in production NGINX will handle proxy configuration
const proxy = require('http-proxy-middleware');
module.exports = function (app) {
    app.use(proxy('/moog', {
        target: 'http://d26dbvl010.test.local:8080',
        logLevel: 'debug',
        prependPath: true,
        changeOrigin: true,
        pathRewrite: function (path, req) { return path.replace('/moog', '/') }
    }));
    app.use(proxy('/api', {
        target: 'http://localhost:8080',
        logLevel: 'debug',
        prependPath: true
    }));
};