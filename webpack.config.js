const path = require('path');

module.exports = {

    entry: ['./rough.js'],
    output: {
        path: path.resolve(__dirname, 'dist'),
        filename: 'bundle.js'
    }
};