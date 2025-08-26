import { render, screen } from '@testing-library/react';
import AstrologyResult from './AstrologyResult';

// Mute React's DOM nesting warnings for this suite only
const realError = console.error;
beforeEach(() => {
  jest.spyOn(console, 'error').mockImplementation((...args) => {
    const [msg] = args;
    if (
      typeof msg === 'string' &&
      (msg.includes('<div> cannot be a descendant of <p>') ||
       msg.includes('<p> cannot contain a nested <div>'))
    ) {
      return; // ignore only the invalid nesting warnings
    }
    realError(...args);
  });
});

afterEach(() => {
  console.error.mockRestore();
});

const minimalUi = {
  birthData: { birthDate: '2000-01-01', birthTime: '12:00', address: 'Athens' },
  ascendant: { sign: 'Gemini', degree: '15', interpretation: 'z', calculation_explanation: 'ce' },
  personalitySummary: '<b>Hello</b>',
  chartSvg: '<svg xmlns="http://www.w3.org/2000/svg"></svg>',
  planets: [{ name: 'Sun', sign: 'Aries (10°)', house: '1', interpretation: 'x', technical_interpretation: 'tx' }],
  yearlyForecast: { title: 'Ετήσια Πρόβλεψη 2025', summary: 's', transits: [] },
  careerGuidance: { idealEnvironment: 'remote', suggestedRoles: ['r1'], finalSummary: 'done' },
  finalOverview: 'Final',
  yearPrediction: 'Year pred'
};

test('shows warning when no data', () => {
  render(<AstrologyResult uiData={null} onReset={() => {}} />);
  expect(screen.getByText(/Δεν βρέθηκαν δεδομένα/i)).toBeInTheDocument();
});

test('renders section headings with data', () => {
  render(<AstrologyResult uiData={minimalUi} onReset={() => {}} />);
  expect(screen.getByText(/Αστρολογική Ανάλυση/i)).toBeInTheDocument();
  expect(screen.getByText(/Ετήσια Πρόβλεψη 2025/i)).toBeInTheDocument();
  expect(screen.getByText(/Τελική Επισκόπηση/i)).toBeInTheDocument();
});
