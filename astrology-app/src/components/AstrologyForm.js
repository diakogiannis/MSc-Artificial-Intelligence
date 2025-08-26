import React, { useState } from 'react';
import { Container, Form, Button, Card, Spinner, Alert } from 'react-bootstrap';
import axios from 'axios';

import AstrologyResult from './AstrologyResult';
import { mapApiResponseToUiData } from '../utils/mappers';

function AstrologyForm() {
  const initialFormState = {
    birthDate: '', birthTime: '', address: '', yearOfForecast: new Date().getFullYear(), sex: '',
  };

  const [form, setForm] = useState(initialFormState);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null); // Εδώ θα μπει το *επεξεργασμένο* αποτέλεσμα
  const [error, setError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: name === 'yearOfForecast' ? parseInt(value, 10) || '' : value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setResult(null);
    try {
      const response = await axios.post(
        'http://localhost:8081/api/astrology/predict/western', form,
        { headers: { 'Accept': 'application/json', 'Content-Type': 'application/json' } }
      );

      if (Array.isArray(response.data) && response.data.length > 0) {
        // Χρησιμοποιούμε τον mapper για να μετατρέψουμε τα δεδομένα
        const mappedResult = mapApiResponseToUiData(response.data[0]);
        if (mappedResult.error) {
          setError(mappedResult.error);
        } else {
          setResult(mappedResult.data); // Αποθηκεύουμε τα καθαρά δεδομένα
        }
      } else {
        setError("Η απάντηση από το σύστημα δεν έχει την αναμενόμενη δομή.");
      }
    } catch (err) {
      setError('Υπήρξε σφάλμα στην επικοινωνία με το σύστημα πρόβλεψης.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setResult(null);
    setError('');
    setForm(initialFormState);
  };

  if (result) {
    // Περνάμε τα ήδη επεξεργασμένα δεδομένα στο component των αποτελεσμάτων
    return <AstrologyResult uiData={result} onReset={handleReset} />;
  }

  return (
    <Container className="mt-5" style={{ maxWidth: 500 }}>
      <Card className="shadow-lg">
        <Card.Body>
          <Card.Title>Δυτική Αστρολογική Πρόβλεψη</Card.Title>
          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
              <Form.Label>Ημερομηνία Γέννησης</Form.Label>
              <Form.Control
                type="date"
                name="birthDate"
                value={form.birthDate}
                onChange={handleChange}
                required
                />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Ώρα Γέννησης</Form.Label>
              <Form.Control
                type="time"
                name="birthTime"
                value={form.birthTime}
                onChange={handleChange}
                required
                />
            </Form.Group>
            <Form.Group className="mb-3">
              <Form.Label>Πόλη</Form.Label>
              <Form.Control 
                type="text" 
                name="address"    
                value={form.address}                  
                placeholder="Πχ. Νοσοκομείο Έλενα Χολαργός Αττικής"
                onChange={handleChange} 
                required 
                />
            </Form.Group>
            <Form.Group className="mb-4">
              <Form.Label>Φύλο</Form.Label>
              <Form.Control
                type="text"
                name="sex"
                placeholder="Άνδρας"
                value={form.sex}
                onChange={handleChange}
                required
                />
            </Form.Group>
            <Form.Group className="mb-4">
              <Form.Label>Έτος Πρόβλεψης</Form.Label>
              <Form.Control
                type="number"
                name="yearOfForecast"
                value={form.yearOfForecast}
                min="1900"
                max="2100"
                onChange={handleChange}
                required
                />
            </Form.Group>
            <Button type="submit" variant="primary" disabled={loading}>
              {loading ? <Spinner animation="border" size="sm" /> : 'Πάρε Πρόβλεψη'}
            </Button>
          </Form>

          {error && <Alert variant="danger" className="mt-3">{error}</Alert>}

          {result && <AstrologyResult data={result} />}

        </Card.Body>
      </Card>
    </Container>
  );
}

export default AstrologyForm;
