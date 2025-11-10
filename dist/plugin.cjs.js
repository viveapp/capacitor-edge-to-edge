'use strict';

var core = require('@capacitor/core');

const EdgeToEdge = core.registerPlugin('EdgeToEdge', {
    web: () => Promise.resolve().then(function () { return web; }).then((m) => new m.EdgeToEdgeWeb()),
});

class EdgeToEdgeWeb extends core.WebPlugin {
    async enable() {
        console.error('EdgeToEdgeWeb#enable(), not implemented!');
    }
    async disable() {
        console.error('EdgeToEdgeWeb#disable(), not implemented!');
    }
    async getInsets() {
        console.error('EdgeToEdgeWeb#getInsets(), not implemented!');
        return {
            bottom: 0,
            left: 0,
            right: 0,
            top: 0,
        };
    }
    async setBackgroundColor() {
        console.error('EdgeToEdgeWeb#setBackgroundColor(), not implemented!');
    }
}

var web = /*#__PURE__*/Object.freeze({
    __proto__: null,
    EdgeToEdgeWeb: EdgeToEdgeWeb
});

exports.EdgeToEdge = EdgeToEdge;
//# sourceMappingURL=plugin.cjs.js.map
