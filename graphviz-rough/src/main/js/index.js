console = {}
setTimeout = function () {
}
clearTimeout = function () {
}
setInterval = function () {
}
clearInterval = function () {
}
rough = function (svg, options) {
    const coarse = require('coarse-domino')
    return coarse(svg, [], options)
}
