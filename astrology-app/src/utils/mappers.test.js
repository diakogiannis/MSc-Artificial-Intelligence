import { mapApiResponseToUiData } from '../utils/mappers';

test('mapApiResponseToUiData returns error when report missing', () => {
  const out = mapApiResponseToUiData({ error: 'boom' });
  expect(out.error).toBeTruthy();
  expect(out.data).toBeNull();
});

test('mapApiResponseToUiData maps core fields', () => {
  const api = {
    report: {
      birth_data: { date: '2000-01-01', time: '12:00', location: 'Athens', zodiac_sign: 'Gemini' },
      ascendant: { sign: 'Gemini', degree: '15', interpretation: 'Asc desc', calculation_explanation: 'math' },
      personality_summary: '<b>Strong</b>',
      chart_svg: '<svg></svg>',
      planets: {
        sun: { sign: 'Aries', house: '1', degree: '10', interpretation: 'x', technical_interpretation: 'tx' },
        moon: { sign: 'Taurus', house: '2', degree: '20', interpretation: 'y', technical_interpretation: 'ty' }
      },
      yearly_forecast: { year: 2025, summary: 'good', important_transits: ['t1','t2'] },
      career_guidance: { ideal_environment: 'remote', suggested_roles: ['r1'], final_summary: 'fs' },
      final_overview: 'final text',
      year_prediction: 'long year'
    }
  };

  const out = mapApiResponseToUiData(api);
  expect(out.error).toBeNull();
  expect(out.data).toBeTruthy();
  const ui = out.data;
  expect(ui.birthData.location).toBe('Athens');
  expect(ui.ascendant.title).toContain('Gemini');
  expect(ui.personalitySummary).toContain('Strong'); // inner HTML allowed
  expect(ui.chartSvg).toContain('<svg');

  // planets become array of objects with combined sign+degree
  expect(Array.isArray(ui.planets)).toBe(true);
  const sun = ui.planets.find(p => p.name.toLowerCase() === 'sun');
  expect(sun.sign).toContain('Aries');
  expect(sun.sign).toContain('10');

  // yearly forecast + career guidance
  expect(ui.yearlyForecast.title).toContain('2025');
  expect(ui.yearlyForecast.transits.length).toBe(2);
  expect(ui.careerGuidance.idealEnvironment).toBe('remote');
  expect(ui.finalOverview).toBe('final text');
  expect(ui.yearPrediction).toBe('long year');
});
