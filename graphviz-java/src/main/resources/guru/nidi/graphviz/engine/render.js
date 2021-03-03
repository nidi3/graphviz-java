var viz;
var totalMemory = 16777216;

function initViz(force) {
  if (force || !viz || viz.totalMemory !== totalMemory) {
    viz = new Viz({
      Module: function () {
        return Viz.Module({
          print: function (e) {
            log(e);
          },
          printErr: function (e) {
            log(e);
          },
          TOTAL_MEMORY: totalMemory
        });
      },
      render: Viz.render
    });
    viz.totalMemory = totalMemory;
  }
  return viz;
}

function render(src, options) {
  try {
    initViz().renderString(src, options)
        .then(function (res) {
          result(res);
        })
        .catch(function (err) {
          initViz(true);
          error(err.toString());
        });
  } catch (e) {
    error(e.toString());
  }
}
