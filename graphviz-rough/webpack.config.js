const path = require('path')

module.exports = {
    module: {
        rules: [{
            test: /\.js$/,
            use: {
                loader: 'babel-loader',
                options: {
                    presets: ['@babel/preset-env', {sourceType: 'script'}]
                }
            }
        }]
    },
    mode: 'production',
    optimization: {
        minimize: true
    },
    entry: ['@babel/polyfill', './src/main/js/index.js'],
    output: {
        path: path.resolve(__dirname, 'target/generated-resources'),
        filename: 'graphviz-rough.js'
    }
}
