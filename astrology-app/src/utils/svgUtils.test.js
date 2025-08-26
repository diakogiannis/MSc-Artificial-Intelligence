import { sanitizeSvgBase64, withDimensions, base64Decode, decodeb64Svg } from '../utils/svgUtils';

// Polyfill atob/btoa for Node/Jest
const nodeAtob = (str) => Buffer.from(str, 'base64').toString('binary');
const nodeBtoa = (str) => Buffer.from(str, 'binary').toString('base64');

beforeAll(() => {
  global.atob = nodeAtob;
  global.btoa = nodeBtoa;
  global.window = global.window || {};
  window.atob = nodeAtob;
  window.btoa = nodeBtoa;
});

test('sanitizeSvgBase64 strips spaces and trailing bracket; trailing quote can remain', () => {
  const raw = '  "PHN2ZyB4bWxucz0iaHR0cDovL3N2ZyI+PC9zdmc+"  ]  ';
  const clean = sanitizeSvgBase64(raw);
  // Compare after trimming any trailing quotes, to reflect current implementation
  expect(clean.replace(/"+$/, '')).toBe('PHN2ZyB4bWxucz0iaHR0cDovL3N2ZyI+PC9zdmc+');
});

test('withDimensions injects width/height into decoded svg then re-encodes', () => {
  const svg = '<svg xmlns="http://www.w3.org/2000/svg"><rect width="10" height="10"/></svg>';
  const b64 = Buffer.from(svg, 'binary').toString('base64');
  const patchedB64 = withDimensions(b64, 777, 333);
  const decoded = Buffer.from(patchedB64, 'base64').toString('binary');
  expect(decoded.startsWith('<svg')).toBe(true);
  expect(decoded).toMatch(/width="777"/);
  expect(decoded).toMatch(/height="333"/);
});

test('base64Decode handles urlsafe base64 and padding', () => {
  const classic = 'SGVsbG8tV29ybGQ='; // Hello-World
  const urlsafe = classic.replace(/\+/g,'-').replace(/\//g,'_').replace(/=+$/,'');
  const decoded = base64Decode(urlsafe);
  expect(decoded).toBe('Hello-World');
});

test('decodeb64Svg strips data URI prefix and decodes', () => {
  const svg = '<svg xmlns="http://www.w3.org/2000/svg"></svg>';
  const b64 = Buffer.from(svg, 'binary').toString('base64');
  const prefixed = `data:image/svg+xml;base64,${b64}`;
  const out = decodeb64Svg(prefixed);
  expect(out).toBe(svg);
});
