import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import axios from 'axios';
import AstrologyForm from './AstrologyForm';

jest.mock('axios');

function fillForm(container) {
  const birthDate = container.querySelector('input[name="birthDate"]');
  const birthTime = container.querySelector('input[name="birthTime"]');
  const address   = container.querySelector('input[name="address"]');
  const sex       = container.querySelector('input[name="sex"]');
  const year      = container.querySelector('input[name="yearOfForecast"]');

  fireEvent.change(birthDate, { target: { value: '2000-01-01' } });
  fireEvent.change(birthTime, { target: { value: '12:00' } });
  fireEvent.change(address,   { target: { value: 'Athens' } });
  fireEvent.change(sex,       { target: { value: 'M' } });
  fireEvent.change(year,      { target: { value: '2025' } }); // number input expects a string
}

test('submits, maps API data, and shows results', async () => {
  const apiPayload = {
    report: {
      birth_data: { date: '2000-01-01', time: '12:00', location: 'Athens', zodiac_sign: 'Gemini' },
      ascendant: { sign: 'Gemini', degree: '15', interpretation: 'Asc', calculation_explanation: 'math' },
      personality_summary: '<b>Strong</b>',
      chart_svg: '<svg xmlns="http://www.w3.org/2000/svg"></svg>',
      planets: {
        sun:  { sign: 'Aries',  house: '1', degree: '10', interpretation: 'x', technical_interpretation: 'tx' },
        moon: { sign: 'Taurus', house: '2', degree: '20', interpretation: 'y', technical_interpretation: 'ty' }
      },
      yearly_forecast: { year: 2025, summary: 'good', important_transits: ['t1','t2'] },
      career_guidance: { ideal_environment: 'remote', suggested_roles: ['r1'], final_summary: 'fs' },
      final_overview: 'final text',
      year_prediction: 'long year'
    }
  };

  axios.post.mockResolvedValueOnce({ data: [apiPayload] });

  const { container } = render(<AstrologyForm />);
  const submitBtn = screen.getByRole('button', { name: /Πάρε Πρόβλεψη/i });
  expect(submitBtn).toBeInTheDocument();

  fillForm(container);
  fireEvent.click(submitBtn);

  await waitFor(() => {
    expect(screen.getByText(/Αστρολογική Ανάλυση/i)).toBeInTheDocument();
  });

  expect(screen.getByText(/Ετήσια Πρόβλεψη 2025/i)).toBeInTheDocument();
  expect(screen.getByText(/Τελική Επισκόπηση/i)).toBeInTheDocument();
});
