// jest-dom adds custom jest matchers for asserting on DOM nodes.
// allows you to do things like:
// expect(element).toHaveTextContent(/react/i)
// learn more: https://github.com/testing-library/jest-dom

// Force axios to a simple mocked default so Jest never loads its ESM build
jest.mock('axios', () => ({
  __esModule: true,
  default: { post: jest.fn() }
}));

import '@testing-library/jest-dom';
