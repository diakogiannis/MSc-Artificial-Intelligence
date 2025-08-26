// src/utils/mappers.js

/**
 * Μετατρέπει την απάντηση του API σε μια δομή κατάλληλη για το UI.
 * @param {object} apiResponse - Το αντικείμενο από το API (response.data[0]).
 * @returns {object} - Ένα αντικείμενο με πεδία 'data' (τα επεξεργασμένα δεδομένα) και 'error'.
 */
export const mapApiResponseToUiData = (apiResponse) => {
  // Έλεγχος για ασφάλεια, αν η απάντηση δεν έχει τη σωστή δομή
  if (!apiResponse || !apiResponse.report) {
    return {
      error: apiResponse?.error || "Λάθος δομή δεδομένων από το API.",
      data: null,
    };
  }

  const { report, error } = apiResponse;

  if (error) {
    return { error, data: null };
  }

  // Αποδομούμε τα πάντα από το report για ευκολία
  const {
    birth_data, ascendant, planets, personality_summary,
    yearly_forecast, career_guidance, final_overview, chart_svg, year_prediction
  } = report;

  // Επιστρέφουμε τα δεδομένα στη μορφή που θέλει το UI
  return {
    error: null,
    data: {
      birthData: {
        text: `${birth_data?.date || ''} στις ${birth_data?.time || ''}`,
        location: birth_data?.location || 'N/A', zodiac_sign: birth_data?.zodiac_sign || 'N/A',
      },
      ascendant: {
        title: `${ascendant?.sign || 'N/A'} (${ascendant?.degree || 'N/A'}°)`,
        interpretation: ascendant?.interpretation || '',
        calculation_explanation: ascendant?.calculation_explanation || '',
      },
      personalitySummary: personality_summary || '',
      chartSvg: chart_svg || '',
      planets: planets ? Object.entries(planets).map(([name, data]) => ({
        name,
        sign: `${data.sign || ''} (${data.degree || ''}°)`,
        house: data.house || '',
        interpretation: data.interpretation || '',
        technical_interpretation: data.technical_interpretation || ''
      })) : [],
      yearlyForecast: {
        title: `Ετήσια Πρόβλεψη ${yearly_forecast?.year || ''}`,
        summary: yearly_forecast?.summary || '',
        transits: yearly_forecast?.important_transits || [],
      },
      careerGuidance: {
        idealEnvironment: career_guidance?.ideal_environment || '',
        suggestedRoles: career_guidance?.suggested_roles || [],
        finalSummary: career_guidance?.final_summary || '',
      },
      finalOverview: final_overview || '',
      yearPrediction: year_prediction || '',
    },
  };
};
