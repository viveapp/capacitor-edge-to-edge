import { registerPlugin } from '@capacitor/core';
const EdgeToEdge = registerPlugin('EdgeToEdge', {
    web: () => import('./web').then((m) => new m.EdgeToEdgeWeb()),
});
export * from './definitions';
export { EdgeToEdge };
//# sourceMappingURL=index.js.map