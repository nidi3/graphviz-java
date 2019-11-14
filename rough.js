(function () {
   // 'use strict';
//const coarse = require('coarse');
    var rough = require('roughjs');

    var domino = require('domino');
    console.log('a')
    var window = domino.createWindow('<h1>Hello world</h1><svg id="s"></svg>', 'http://example.com');
    console.log('b')
// window={};
//let svg={
//    createElementNS:function()
//};
    var svg = window.document.getElementById('s');
    console.log('c')
    var rc = rough.svg(svg);
    console.log('d')
    var node = rc.rectangle(10, 10, 200, 200);
    svg.appendChild(node);
  //  print(svg.innerHTML)
}());